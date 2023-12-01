package com.nhnacademy.function;

import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Executable;
import com.nhnacademy.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Preprocess implements Executable {
    // TODO rename wild card
    private static final String a = "#";
    private static final String b = "+";

    private static boolean enableTopic(String filter, final String topic) {
        if (topic == null || filter == null) throw new IllegalArgumentException();

        var topicList = topic.split("/");
        var filterList = filter.split("/");

        int len = Math.min(topicList.length, filterList.length);
        for (int i = 0; i < len; ++i) {
            if (filterList[i].equals(a)) return false;

            if (!filterList[i].equals(topicList[i]) && !filterList[i].equals(b)) return true;
            if (filterList[i].equals(b) && i + 1 == len && filterList.length == len && topicList.length != filterList.length) return true;
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
                    
                    boolean shouldFilter = enableTopic(Config.getCurrentConfig().getString("an"), msg.getString("topic"));
                    
                    if (!shouldFilter) {
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
