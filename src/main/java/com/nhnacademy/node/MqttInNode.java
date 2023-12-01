package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
@Getter
public class MqttInNode extends ActiveNode implements Output {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";
    private static final String DEFAULT_TOPIC = "#";

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;
    private String fromTopic;
    
    /*
     * 기본 URI = tcp://ems.nhnacademy.com:1883
     */
    public MqttInNode() {
        this(DEFAULT_URI, DEFAULT_TOPIC);
    }

    /*
     * 사용자 지정 constructors
     */
    public MqttInNode(String uri) {
        this(uri, DEFAULT_TOPIC);
    }

    public MqttInNode(String uri, String topic) {
        super("MqttInNode");
        this.uri = uri;
        fromTopic = topic;
    }

    /*
     * topic 설정하기
     */
    public void setTopic(String topic){
        fromTopic = topic;
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
    public void preprocess() {
        try (IMqttClient client = new MqttClient(uri, getId().toString())){
            client.connect();

            client.subscribe(fromTopic, (topic, msg) -> {
                JSONObject object = new JSONObject();

                try {
                    object.put("topic", topic);
                    object.put("payload", new JSONObject(msg.toString()));
                } catch(JSONException e) {
                    log.warn(e.getMessage());
                }
                for (Wire wire : outWires) {
                    wire.getBq().add(object);  
                }
            });
            
        } catch (Exception e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

    }
    @Override
    public void process(){
    }

}