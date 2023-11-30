package com.nhnacademy.node;

import java.util.Set;

import org.json.JSONObject;

public class JSONprocess {

    private JSONprocess() {
    }

    public static JSONObject jsonProcess(JSONObject preprocessData) {
        JSONObject jsonData = new JSONObject();
        JSONObject jsonPayload = new JSONObject();

        String branchData = preprocessData.getJSONObject("tags").getString("branch"); // 지사
        String placeData = preprocessData.getJSONObject("tags").getString("place"); // 위치
        String devEuiData = preprocessData.getJSONObject("deviceInfo").getString("devEui"); // 장비 식별 번호
        String timeData = preprocessData.getJSONObject("payload").getString("time"); // 시간
        JSONObject objectData = preprocessData.getJSONObject("object"); // object data
        Set<String> key = objectData.keySet();

        jsonPayload.put("time", timeData);
        jsonPayload.put("sensor", key);

        jsonData.put("topic",
                "data/b/" + branchData + "/p/" + placeData + "/d/" + devEuiData + "/t/");
        jsonData.put("payload", jsonPayload);

        return jsonData;
    }
}