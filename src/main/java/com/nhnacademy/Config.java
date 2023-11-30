package com.nhnacademy;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class Config extends JSONObject {
    private static final String DEFAULT_TOPIC = "#";
    private static final String DEFAULT_ALLOWED_SENSOR = "all";

    @Getter
    @Setter
    private static Config currentConfig = new Config(DEFAULT_TOPIC, DEFAULT_ALLOWED_SENSOR);

    public Config(String topic, String allowedSensor) {
        put("an", topic);
        put("s", allowedSensor);
    }
    public static Config getCurrentConfig() {
        return currentConfig;
    }
}