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

import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
@Slf4j
@Getter
public class MqttInNode extends Node implements Output {
    private final Set<Wire> outWires = new HashSet<>();
    private final String URI;
    
    /*
     * 기본 URI = tcp://ems.nhnacademy.com:1883
     */
    public MqttInNode() {
        URI = "tcp://ems.nhnacademy.com:1883";
    }

    /*
     * 사용자 지정 uri
     */
    public MqttInNode(String uri) {
        URI = uri;
    }

    /*
     * wire 연결
     */
    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    /*
     * 들어오는 모든 data를 연결된 wire에 넣어준다
     */
    @Override
    public void process() {
        
        String id = UUID.randomUUID().toString();

        try (IMqttClient client = new MqttClient(URI, id)) {
            client.connect();
            for (Wire wire : outWires) {
                client.subscribe("#", (topic, msg) -> {
                    JSONObject object = new JSONObject();
                    JSONObject jsonTopic = new JSONObject(topic);
                    JSONObject jsonmsg = new JSONObject(new String(msg.getPayload()));
                    object.put("topic",jsonTopic);
                    object.put("payload", jsonmsg);
                    wire.getBq().add(object);
                });
            }

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(100);
            }

            client.disconnect();
        } catch (Exception e) {
            log.info("{}", e);
            Thread.currentThread().interrupt();
        }
    }

    /*
     * process를 실행한다
     */
    @Override
    public void run() {
        process();

    }
