package com.nhnacademy.function;

import com.nhnacademy.Executable;
import com.nhnacademy.Wire;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * 전처리 한 데이터를 가지고, 후처리를 하는 class입니다.
 * <p>
 * mqtt in =wire= 전처리 =wire= 후처리 =wire= mqtt out.
 * <p>
 * 
 * <p>
 * 센서 데이터가 여러개일 경우에 대한 대처. = map
 * <p>
 * value는 double값으로 가정. 타입 캐스팅 전 undefined는 걸러냄.
 * <p>
 * 
 * 데이터는 센서별로 분리.
 * 센서 정보에는 지사, 위치, 장비 식별 번호, 시간 정보, 센서 값이 포함이 되어야 합니다.
 * <p>
 * 예를 들면, temperature라는 센서 내부에는, 이 센서가 들어온 정보.
 * <p>
 * 즉, 위의 5가지의 값이 들어있어야 합니다.
 * + topic 수정까지.
 */
@Slf4j
public class Postprocess implements Executable {

    Map<String, Object> value = new HashMap<>();
    Set<String> key;

    JSONObject jsonData = new JSONObject();
    JSONObject jsonPayload = new JSONObject();
    JSONObject objectData;

    String branchData;
    String placeData;
    String devEuiData;
    String timeData;

    String branchPathData;
    String placePathData;
    String devEuiPathData;
    String timePathData;

    String branchPath;
    String placePath;
    String devEuiPath;
    String timePath;

    /*
     * 11:30:34.551 [Thread-2] WARN com.nhnacademy.node.PostprocessNode --
     * JSONObject["payload"] is not a JSONObject (class
     * org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage
     * 
     * => payload는 mqttReceivedMessage? : 내 역할 아님 pass
     * 
     * terminal에서 입력한 걸 받아서 path로 사용. 확장성을 높여봄.
     */
    @Override
    public void execute(Set<Wire> inWires, Set<Wire> outWires) {
        String allowedSeneor = Config.getCurrentConfig().getString("s");
        String[] sensorArray = allowedSeneor.trim().split(",");

        for (Wire wire : inWires) {
            var bq = wire.getBq();

            // log.info("bq");

            if (!bq.isEmpty()) {
                JSONObject firstpreprocessData = bq.poll();

                log.info(firstpreprocessData.toString() + "test1");
                if ("application/d/1234".equals(firstpreprocessData.get("topic"))) continue;

                JSONObject payloadData = firstpreprocessData.getJSONObject("payload");
                JSONObject tagsData = payloadData.getJSONObject("deviceInfo").getJSONObject("tags");

                branchData = tagsData.getString("branch"); // 지사
                placeData = tagsData.getString("place"); // 위치
                devEuiData = payloadData.getJSONObject("deviceInfo").getString("devEui"); // 장비 식별 번호
                timeData = payloadData.getString("time"); // 시간
                objectData = payloadData.getJSONObject("object"); // object data
                key = objectData.keySet();

                // branchPath = Config.getCurrentConfig().getString("branchPath");
                // placePath = Config.getCurrentConfig().getString("placePath");
                // devEuiPath = Config.getCurrentConfig().getString("devEuiPath");
                // timePath = Config.getCurrentConfig().getString("timePath");

                // branchPathData = tagsData.getString(branchPath);
                // placePathData = tagsData.getString(placePath);
                // devEuiPathData = payloadData.getJSONObject("deviceInfo").getString(devEuiPath);
                // timePathData = payloadData.getString(timePath);

                Set<String> allKey = objectData.keySet(); // TODO error
                for (String keyset : allKey) {

                    Double sensorData = (Double) objectData.get(keyset);
                    value.put(keyset, sensorData); // 값이 여러개일 경우에 대한 대처.
                    // value = objectData.get(keyset); // object의 모든 value값.

                }

                for (String postprocess : sensorArray) {
                    if (key.contains(postprocess)) {
                        // log.info("check postprocess :{}", postprocess);
                        jsonData.put("topic",
                                "data/b/" + branchData + "/p/" + placeData + "/d/" + devEuiData + "/t/");
                        jsonPayload.put("time", timeData);
                        jsonPayload.put("sensor", key);
                        jsonPayload.put("value", value);
                        jsonData.put("payload", jsonPayload);
                    }
                }
                
                log.info(jsonData.toString() + "test2");

                for (Wire outWire : outWires) {
                    outWire.getBq().add(jsonData);
                }
            }
        }
    }

}
