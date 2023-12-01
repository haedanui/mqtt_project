package com.nhnacademy;

import java.util.ArrayList;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class Config extends JSONObject {
    private static final String DEFAULT_TOPIC = "application/+/#";
    private static final String DEFAULT_ALLOWED_SENSOR = "all";

    @Getter
    @Setter
    private static Config currentConfig = new Config(DEFAULT_TOPIC, DEFAULT_ALLOWED_SENSOR);

    public Config(String topic, String allowedSensor) {
        put("applicationName", topic);
        put("allowedSensor", allowedSensor);

        put("required", new ArrayList<>()); // TODO post process 하는 쪽에서 이 부분 채워주세요.
    }
}