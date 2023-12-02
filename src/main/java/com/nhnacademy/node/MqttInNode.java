package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
@Getter
public class MqttInNode extends ActiveNode implements Output {
    private static final String DEFAULT_URI = "tcp://ems.nhnacademy.com:1883";
    private static final String DEFAULT_TOPIC = "#";

    private final Set<Wire> outWires = new HashSet<>();
    private final String uri;

    @Setter
    private String fromTopic;
    
    public MqttInNode() {
        this(DEFAULT_URI, DEFAULT_TOPIC);
    }

    public MqttInNode(String uri) {
        this(uri, DEFAULT_TOPIC);
    }

    public MqttInNode(String uri, String topic) {
        super("mqtt in");

        this.uri = uri;
        fromTopic = topic;
    }

    /*
     *     node-red format
     * 
     *     {
     *         "id": "83ccfc177b9bbf94",
     *         "type": "mqtt in",
     *         "z": "c14b37e58307abb9",
     *         "name": "",
     *         "topic": "",
     *         "qos": "2",
     *         "datatype": "auto-detect",
     *         "nl": false,
     *         "rap": true,
     *         "rh": 0,
     *         "inputs": 0,
     *         "x": 310,
     *         "y": 220,
     *         "wires": [
     *             []
     *         ]
     *     }
     * 
     *     id, type, topic, wire를 가지고 있고 필요하면 더 추가해서 사용
     */
    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        obj.put("id", getId());
        obj.put("type", "mqtt in");
        obj.put("topic", fromTopic);

        // TODO wire 추가.

        return obj;
    }

    /*
     * wire 연결
     */
    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void preprocess() {
        UUID clientId = UUID.randomUUID(); // node, client id를 분리해서 사용하기 위해 따로 받았습니다.

        try (IMqttClient client = new MqttClient(uri, clientId.toString())) {
            client.connect();

            client.subscribe(fromTopic, (topic, payload) -> {
                JSONObject object = new JSONObject();

                try {
                    object.put("topic", topic);
                    object.put("payload", new JSONObject(payload.toString()));
                } catch(JSONException ignore) {
                    log.warn("topic : {} json형식의 데이터가 아닙니다.", topic);
                }

                for (Wire wire : outWires) {
                    wire.getBq().add(object);
                }
            });
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void process() {
    }
}