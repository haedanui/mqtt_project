package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MqttInNode extends ActiveNode implements Output {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";

    private final Set<Wire> outWires = new HashSet<>();
    private String uri;

    public MqttInNode() {
        this(DEFAULT_URI);
    }

    public MqttInNode(String uri) {
        this.uri = uri;
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void process() {
        String clientId = UUID.randomUUID().toString();
        try (IMqttClient client = new MqttClient(uri, clientId);) {
            client.connect();

            client.subscribe("#", (topic, payload) -> {
                // log.info(topic);
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("topic", topic);
                jsonObject.put("payload", payload);

                for (Wire wire : outWires) {
                    var messageQ = wire.getBq();

                    messageQ.put(jsonObject);
                }
            });
        } catch (Exception ignore) {
        }
    }
}
