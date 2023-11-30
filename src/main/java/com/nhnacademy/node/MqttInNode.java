package com.nhnacademy.node;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MqttInNode extends Node implements Output {
    private final Set<Wire> outWires = new HashSet<>();

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void process() {
        String id = UUID.randomUUID().toString();

        try (IMqttClient client = new MqttClient("tcp://ems.nhnacademy.com:1883", id)) {
            client.connect();
            for (Wire wire : outWires) {
                client.subscribe("#", (topic, msg) -> {
                    JSONObject object = new JSONObject();
                    object.put("topic", topic);
                    object.put("payload", msg);
                    wire.getBq().add(object);
                    // log.info("topic : {}\n payload : {}", topic, msg);
                });
            }

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(100);
            }

            client.disconnect();
        } catch (Exception e) {
            log.info("{}", e);
        }
    }

    @Override
    public void run() {
        process();

    }

    /*
     * public static void main(String[] args) {
     * MqttInNode mqttInNode = new MqttInNode();
     * Wire wire = new Wire();
     * mqttInNode.wireOut(wire);
     * mqttInNode.run();
     * }
     */
}
