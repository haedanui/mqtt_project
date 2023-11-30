package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MqttOutNode extends Node implements Input {
    private final Set<Wire> inWires = new HashSet<>();

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }

    @Override
    public void process() {
        String id = UUID.randomUUID().toString();

        try (IMqttClient client = new MqttClient("tcp://localhost:1883", id)) {
            client.connect();
            for (Wire wire : inWires) {
                while(!wire.getBq().isEmpty()){
                    JSONObject object = wire.getBq().poll();
                    
                    String payload = object.getString("payload");
    
                    client.publish(object.getString("topic"), new MqttMessage(payload.getBytes()));
                    log.info("{}", object);
                }
            }
            
            while(!Thread.currentThread().isInterrupted()) {
                Thread.sleep(100);
            }

            client.disconnect();
        } catch (Exception e) {
            log.info("{}", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        process();

    }

    /* public static void main(String[] args) {
        MqttOutNode outNode = new MqttOutNode();
        Wire wire = new Wire();
        outNode.wireIn(wire);

        JSONObject object = new JSONObject();
        object.put("topic", "dfkjk");
        object.put("payload", "dkfjkjkkkkkkkkkkk");

        JSONObject object2 = new JSONObject();
        object2.put("topic", "dkdkdkdk");
        object2.put("payload", "ssssssssssssssssssss");
        try {
            wire.getBq().put(object);
            wire.getBq().put(object2);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        outNode.start();
    } */
}

