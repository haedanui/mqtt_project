package com.nhnacademy.function;

import com.nhnacademy.Wire;

import java.util.Arrays;
import java.util.HashSet;
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
    private boolean isAllSensorDataReceived = false;
    private Set<String> required;

    // TODO rename
    // 형태는 유지하고 원하는 값만 가지고 오기위해 재귀적으로 돌림
    private void recursive(JSONObject out, JSONObject data) {
        Set<String> keys = data.keySet();

        for (String key : keys) {
            if (required.contains(key) || (isAllSensorDataReceived && key.equals("object"))) { // 모든 센서를 허용하는 경우 (object 값에 센서 데이터가 전부 들어 있음)
                out.put(key, data.get(key));
            }
            else if (data.get(key) instanceof JSONObject) {
                JSONObject temp = new JSONObject(); // TODO rename
                recursive(temp, data.getJSONObject(key));

                if (!temp.isEmpty()) {
                    out.put(key, temp);
                }
            }
        }
    }

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
        if (required == null) {
            required = new HashSet<>();

            if (Config.getCurrentConfig().get("s").equals("all")) {
                isAllSensorDataReceived = true;
            }

            if (!isAllSensorDataReceived) {
                String[] allowedSeneor = Config.getCurrentConfig().getString("s").split(",");

                required.addAll(Arrays.asList(allowedSeneor));
            }

            String[] requiredList = Config.getCurrentConfig().getString("required").split(",");

            required.addAll(Arrays.asList(requiredList));
        }

        for (Wire wire : inWires) {
            var bq = wire.getBq();

            if (!bq.isEmpty()) {
                JSONObject msg = bq.poll();

                JSONObject parsedData = new JSONObject();
                recursive(parsedData, msg.getJSONObject("payload"));

                for (Wire outWire : outWires) {
                    outWire.getBq().add(parsedData);
                }
            }
        }
    }
}
