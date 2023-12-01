package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

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
public class MqttOutNode extends ActiveNode implements Input {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;
    private IMqttClient client;
    
    /*
     * 기본 URI = tcp://ems.nhnacademy.com:1883
     */
    public MqttOutNode() {
        this(DEFAULT_URI);
    }

    /*
     * 사용자 지정 constructors
     */
    public MqttOutNode(String uri) {
        super("MqttOuNode");
        this.uri = uri;
    }

    /*
     * wire 연결
     */
    @Override
    public void wireIn(Wire wire) {
        outWires.add(wire);
    }

    /*
     * 연결된 wire에서 data를 꺼내 localhost에 보낸다
     */
    @Override
    public void preprocess() {
        try {
            client = new MqttClient(uri, getId().toString());
            client.connect();
            
        } catch (Exception e) {
            log.info("{}", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void process(){
        for (Wire wire : outWires) {
                if(!wire.getBq().isEmpty()){
                    JSONObject object = wire.getBq().poll();
                    
                    String payload = object.getString("payload");
                    try {
                        client.publish(object.getString("topic"), new MqttMessage(payload.getBytes()));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
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
            e.printStackTrace();
        }
        outNode.start();
    } */
}
