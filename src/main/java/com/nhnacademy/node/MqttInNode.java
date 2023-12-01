package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
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
     * id를 가져올 수 있도록 한다.
     */
    public String getMqttId() {
        return getId().toString();
    }

    /*
     * 들어오는 모든 data를 연결된 wire에 넣어준다
     */
    @Override
    public void process() {
        try (IMqttClient client = new MqttClient(uri, getMqttId())){
            client.connect();

            client.subscribe("application/#", (topic, msg) -> {
                JSONObject object = new JSONObject();

                log.info("dddddd - {}",msg);
                object.put("topic", new JSONObject(topic));
                object.put("payload", new JSONObject(msg.toString()));

                log.info("object: {}",object);
                
                for (Wire wire : outWires) {
                    
                    // String sTopic = "\""+topic + "\"";
                    // String sMsg = "\""+ new String(msg.getPayload()) + "\"";

                    // JSONObject jsonTopic = new JSONObject("{\"topic\":\""+topic+"\", \"payload\":\""+new String(msg.getPayload())+"\"}");
                    // JSONObject object = new JSONObject();
                    // object.put("topic", sTopic);
                    // object.put("payload", sMsg);

                    wire.getBq().add(object);
                    // log.info("object: {}",jsonTopic);
                    

                }
            });
            while(!Thread.currentThread().isInterrupted()){
                Thread.sleep(100);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    public static void main(String[] args) {
        MqttInNode mqttInNode = new MqttInNode();
        Wire wire = new Wire();
        mqttInNode.wireOut(wire);
        mqttInNode.start();
    }

}