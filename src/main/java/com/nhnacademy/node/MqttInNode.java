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

    private IMqttClient client;

    public MqttInNode(String name) {
        this(name, DEFAULT_URI);
    }

    public MqttInNode(String uri, String name) {
        super(name);
        this.uri = uri;
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void process() {
    }

    @Override
    public void preprocess() {
        String clientId = UUID.randomUUID().toString();
        try {
            client = new MqttClient(uri, clientId);
            client.connect();

            client.subscribe("applictaion/#", (topic, payload) -> {
                log.info("payload {}", payload.toString());
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("topic", topic);
                jsonObject.put("payload", new JSONObject(payload.toString()));

                log.info("asdasd {}", jsonObject);

                for (Wire wire : outWires) {
                    var messageQ = wire.getBq();

                    messageQ.put(jsonObject);
                }
            });
        } catch (Exception ignore) {
            log.warn(ignore.getMessage());
        }
    }
}
