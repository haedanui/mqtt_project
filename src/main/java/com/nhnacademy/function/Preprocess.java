package com.nhnacademy.function;

import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Preprocess implements Executable {
    private static final String WIDECARD_ALL = "#";
    private static final String WILDCARD_ANY = "+";

    /**
     * 토픽이 필터로 설정한 값과 같다면 true를 반환해 다음 단계를 처리하게 한다.
     */
    public static boolean enableTopic(String filter, final String topic) {
        if (topic == null || filter == null) throw new IllegalArgumentException();

        var topicList = topic.split("/");
        var filterList = filter.split("/");

        boolean temp = false; // 필터의 마지막 값이 WILDCARD_ANY인지 확인하는 변수
        int len = Math.min(topicList.length, filterList.length);
        for (int i = 0; i < len; ++i) {
            if (filterList[i].equals(WIDECARD_ALL)) return true;
            if (!filterList[i].equals(topicList[i]) && !filterList[i].equals(WILDCARD_ANY)) return false;

            temp = (filterList[i].equals(WILDCARD_ANY));
        }

        return !(temp && filterList.length != topicList.length);
    }

    /**
     * 지정된 양식 속 원하는 데이터 값이 있는지 확인하고 하나라도 있다면 true를 반환해 다음 단계를 처리하게 한다.
     */
    private static boolean enableSensor(String allowedSensor, final JSONObject payload) {
        if (allowedSensor.equals("all")) return true;

        var allowedSeneorList = allowedSensor.split(",");

        int count = 0;
        if(payload.has("object")) {
            JSONObject object = payload.getJSONObject("object");
            for(int i = 0; i < allowedSeneorList.length; i++) {
                if (object.has(allowedSeneorList[i])) count++;
            }
        }

        return (allowedSeneorList.length == count);
    }

    @Override
    public void execute(Set<Wire> inWires, Set<Wire> outWires) {
        try {
            for (Wire inWire : inWires) {
                var messageQ = inWire.getBq();
    
                if (!messageQ.isEmpty()) {
                    JSONObject msg = messageQ.poll();
                    
                    // TODO config rename하면서 여기도 수정해야 함.
                    boolean isAllowedTopic = enableTopic(Config.getCurrentConfig().getString("applicationName"), msg.getString("topic"));
                    boolean isAllowedSensor = enableSensor(Config.getCurrentConfig().getString("s"), msg.getJSONObject("payload"));
                    
                    if (isAllowedTopic && isAllowedSensor) {
                        for (Wire outWire : outWires) {
                            outWire.getBq().put(msg);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
