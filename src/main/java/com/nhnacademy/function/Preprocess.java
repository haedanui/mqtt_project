package com.nhnacademy.function;

import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Executable;
import com.nhnacademy.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Preprocess implements Executable {
    // rename wild card
    private static final String WIDECARD_ALL = "#";
    private static final String WIRDCARD_SINGLE = "+";

    private static boolean enableTopic(String filter, final String topic) {
        /**
         * 토픽이 필터로 설정한 값과 같다면 true를 반환해 다음 단계를 처리하게 한다.
         */
        if (topic == null || filter == null) throw new IllegalArgumentException();

        var topicList = topic.split("/");
        var filterList = filter.split("/");

        int len = Math.min(topicList.length, filterList.length);
        for (int i = 0; i < len; ++i) {
            if (filterList[i].equals(WIDECARD_ALL)) return true;
            if (!filterList[i].equals(topicList[i]) && !filterList[i].equals(WIRDCARD_SINGLE)) return false;
            if (filterList[i].equals(WIRDCARD_SINGLE) && i + 1 == len && filterList.length == len && topicList.length != filterList.length) return false;
        }

        return true;
    }

    private static boolean enableSensor(String filter, final JSONObject target){
        /**
         * 지정된 양식 속 원하는 데이터 값이 있는지 확인하고 있다면 true를 반환해 다음 단계를 처리하게 한다.
         */
        var filterList = filter.split(",".trim());
        if(target.has("payload")){
            JSONObject payload = target.getJSONObject("payload");
            if(payload.has("object")){
                JSONObject object = payload.getJSONObject("object");
                int count = 0;
                for(int i = 0; i < filterList.length; i++){
                    if(object.has(filterList[i])){
                        count ++;
                    }
                }
                if(count >= 1){
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void execute(Set<Wire> inWires, Set<Wire> outWires) {
        try {
            for (Wire inWire : inWires) {
                var messageQ = inWire.getBq();
    
                if (!messageQ.isEmpty()) {
                    JSONObject msg = messageQ.poll();
                    
                    boolean allowedTopic = enableTopic(Config.getCurrentConfig().getString("applicationName"), msg.getString("topic"));
                    boolean allowedSensor = enableSensor(Config.getCurrentConfig().getString("allowedSensor"), msg);
                    
                    if (allowedTopic&&allowedSensor) {
                        log.info(msg.getString("topic"));
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
