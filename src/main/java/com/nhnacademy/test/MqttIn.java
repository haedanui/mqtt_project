package com.nhnacademy.test;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONObject;

import lombok.Getter;

// test용 파일 입니다.
@Getter
public class MqttIn implements Runnable {
    private static final String DEFAULT_SERVER_URI = "tcp://ems.nhnacademy.com:1883";

    private final Thread thread;
    private String uri;

    public MqttIn(String uri) {
        this.thread = new Thread(this);

        this.uri = uri;
    }

    public MqttIn() {
        this(DEFAULT_SERVER_URI);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        try (IMqttClient client = new MqttClient(uri, "test");) {
            client.connect();

            client.subscribe("application/#", (topic, payload) -> {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("topic", topic);
                jsonObject.put("payload", payload);

                System.out.println(); // break point 걸고 jsonObject안의 값 확인하면서 하시면 됩니다.
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
