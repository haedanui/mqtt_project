package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Wire;

public class PreprocessNode extends FunctionNode {

    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();
    Wire ppap = new Wire();
    String[] sortTopic;
    String[] allowedSendors;

    public void setTopicSensors() {
        Config config = Config.getCurrentConfig();
        String applicationName = config.getString("an");
        String sensorName = config.getString("s");
        sortTopic = applicationName.split("/".trim());
        allowedSendors = sensorName.split(",".trim());
    }

    public boolean checkTopic(JSONObject target) {
        String[] targetTopic = target.getString("topic").split("/".trim());
        for (int i = 0; i < targetTopic.length; i++) {
            if ((!sortTopic[i].equals(targetTopic[i])) || (!sortTopic[i].equals("+"))) {
                return false;
            }
            if (sortTopic[i].equals("#")) {//--an app/# app/ppap/abc
                return true;
            }
        }
        return true;
    }

    public boolean checkSensor(JSONObject target) {
        int count = 0;
        for (int i = 0; i < allowedSendors.length; i++) {
            if (target.has(allowedSendors[i])) {
                count++;
            }
        }
        if (count >= 1) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void wireIn(Wire wire) {
        inWires.add(wire);
    }

    @Override
    public void wireOut(Wire wire) {
        outWires.add(wire);
    }

    @Override
    public void process() {
        setTopicSensors();
        while (!Thread.currentThread().isInterrupted()) {
            if (!inWires.isEmpty()) {
                for (Wire inWire : inWires) {
                    JSONObject beforeTest = inWire.getBq().poll();
                    if (beforeTest != null) {
                        if (checkSensor(beforeTest) && checkTopic(beforeTest)) {
                            for (Wire outWire : outWires) {
                                outWire.getBq().add(beforeTest);
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        process();
    }
}
