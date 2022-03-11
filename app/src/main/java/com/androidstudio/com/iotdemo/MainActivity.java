package com.androidstudio.com.iotdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    TextView txtTemp, txtHumi;
    MQTTHelper mqttHelper;
    ToggleButton btnLed;

    private void startMQTT(){
        mqttHelper = new MQTTHelper(this, "test");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Mqtt", "message is from: " + topic);
                Log.d("Mqtt", message.toString());
                if(topic.equals("khoikieu1608/feeds/AIR_TEMP")){
                    txtTemp.setText(message.toString() + "°C");
                }
                if(topic.equals("khoikieu1608/feeds/AIR_HUMID")){
                    txtHumi.setText(message.toString() + "%");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void sendDataToMQTT(String topic, String mess){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = mess.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (Exception e){}


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLed = findViewById(R.id.btnLed);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);

        txtTemp.setText("25°C");
        txtHumi.setText("80%");
        startMQTT();

        btnLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    sendDataToMQTT("khoikieu1608/feeds/vgu-led", "1");
                } else {
                    sendDataToMQTT("khoikieu1608/feeds/vgu-led", "0");
                }
            }
        });
    }
}