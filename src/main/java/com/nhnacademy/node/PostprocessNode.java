package com.nhnacademy.node;

import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Wire;
import java.lang.Thread;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * 전처리 한 데이터를 가지고, 후처리를 하는 class입니다.
 * <p>
 * mqtt in =wire= 전처리 =wire= 후처리 =wire= mqtt out.
 * </p>
 * 2개의 func를 method로 묶은 형태. wire은 총 3개가 필요합니다. 하지만 functionNode에 wireIn,out이
 * 이미 있기에 굳이 구현을 할 필요는 없어 안했습니다.
 * </p>
 * 
 * 데이터는 센서별로 분리.
 * 센서 정보에는 지사, 위치, 장비 식별 번호, 시간 정보, 센서 값이 포함이 되어야 합니다.
 * <p>
 * 예를 들면, temperature라는 센서 내부에는, 이 센서가 들어온 정보.
 * </p>
 * 즉, 위의 5가지의 값이 들어있어야 합니다.
 * + topic 수정까지.
 */

@Slf4j
public class PostprocessNode extends FunctionNode {

    Object value;
    Set<String> key;
    JSONObject jsonData = new JSONObject();
    JSONObject jsonPayload = new JSONObject();
    String branchData;
    String placeData;
    String devEuiData;
    String timeData;

    /**
     * Thread runnable.
     * process를 작동하는 장소.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();
                Thread.sleep(500);
            } catch (Exception e) {
                log.warn(e.getMessage());
                Thread.interrupted();
            }
        }
    }

    /**
     * 작업 장소.
     * 센서 정보에는 지사, 위치, 장비 식별 번호, 시간 정보, 센서 값이 포함
     */
    @Override
    public void process() {

        String allowedSeneor = Config.getCurrentConfig().getString("s");
        String[] sensorArray = allowedSeneor.trim().split(",");
        for (Wire data : getInWires()) {
            var bq = data.getBq();
            if (!bq.isEmpty()) {
                JSONObject preprocessData = bq.poll();
                branchData = preprocessData.getJSONObject("tags").getString("branch"); // 지사
                placeData = preprocessData.getJSONObject("tags").getString("place"); // 위치
                devEuiData = preprocessData.getJSONObject("deviceInfo").getString("devEui"); // 장비 식별 번호
                timeData = preprocessData.getJSONObject("payload").getString("time"); // 시간
                JSONObject objectData = preprocessData.getJSONObject("object"); // object data
                key = objectData.keySet();

                for (String key1 : objectData.keySet()) {
                    value = objectData.get(key1); // object의 모든 value값.
                }

                for (String postprocess : sensorArray) {
                    if (key.contains(postprocess)) {
                        log.info("check postprocess :{}", postprocess);
                        jsonData.put("topic",
                                "data/b/" + branchData + "/p/" + placeData + "/d/" + devEuiData + "/t/");
                        jsonPayload.put("time", timeData);
                        jsonPayload.put("sensor", key);
                        jsonPayload.put("value", value);
                        jsonData.put("payload", jsonPayload);
                    }
                }
            }
        }
    }
}