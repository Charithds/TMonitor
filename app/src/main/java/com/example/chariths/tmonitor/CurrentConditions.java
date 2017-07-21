package com.example.chariths.tmonitor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentConditions.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentConditions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentConditions extends Fragment implements View.OnClickListener {

    private static final String TAG = "CurrentConditionsFragment";

    private final String CLIENT_ID = MqttClient.generateClientId();
    private String SUBSCRIPTION_TOPIC = MQTTContract.SubscriptionTopic + "/present_data";

    private MqttAndroidClient mqttClient;
    private EditText messageView;
    private Button connectBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CurrentConditions() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentConditions.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentConditions newInstance(String param1, String param2) {
        CurrentConditions fragment = new CurrentConditions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_conditions, container, false);

        messageView = (EditText) view.findViewById(R.id.messageView);
        connectBtn = (Button) view.findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        mqttClient = new MqttAndroidClient(getActivity().getApplicationContext(), MQTTContract.serverUri, CLIENT_ID);

        setConnectionCallback();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        unsubscribeTopic();
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setConnectionCallback(){
        if (mqttClient == null) return;
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                String text = messageView.getText()+"\n" + MQTTContract.CONNECTION_LOST;
                messageView.setText(text);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String text = messageView.getText()+"\nMessage arrived : "+ new String(message.getPayload());
                messageView.setText(text);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                String text = messageView.getText()+"\n" + MQTTContract.DELIVERY_COMPLETE;
                messageView.setText(text);
            }
        });
    }

    public void connect() throws MqttException {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        mqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                try {
                    subscribeToTopic();
                } catch (MqttException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(getActivity().getApplicationContext(), "Faliure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void subscribeToTopic() throws MqttException {
        final IMqttToken token = mqttClient.subscribe(SUBSCRIPTION_TOPIC, 0);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                String text = messageView.getText()+ "\n" + MQTTContract.SUBSCRIPTION_SUCCESSFUL;
                messageView.setText(text);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                String text = messageView.getText()+"\n" + MQTTContract.CONNECTION_FAILED;
                messageView.setText(text);
            }
        });
    }

    public void unsubscribeTopic(){
        if (mqttClient == null || !mqttClient.isConnected()) return;
        try {
            IMqttToken unsubToken = mqttClient.unsubscribe(SUBSCRIPTION_TOPIC);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
