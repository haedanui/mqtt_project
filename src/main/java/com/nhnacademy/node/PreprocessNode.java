package com.nhnacademy.node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Config;
import com.nhnacademy.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("preprocessNode topic is :{}", Arrays.toString(targetTopic));
        log.info("preprocessNode topic is :{}", Arrays.toString(sortTopic));
        for (int i = 0; i < targetTopic.length; i++) {
            if (sortTopic[i].equals("#")) {// --an app/# app/ppap/abc
                return true;
            }
            if ((!sortTopic[i].equals(targetTopic[i])) || (!sortTopic[i].equals("+"))) {
                return false;
            }
            
        }
        return true;
    }

    public boolean checkSensor(JSONObject target) {
        int count = 0;
        //log.info("preprocessNode topic is :{}", Arrays.toString(allowedSendors));
        for (int i = 0; i < allowedSendors.length; i++) {
            if (target.has(allowedSendors[i])) {
                count++;
            }
            if (allowedSendors[0].equals("all")) {
                return true;
            }
        }
        if (count >= 0) {
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
        // log.trace("preprocess node start");
        // while (!Thread.currentThread().isInterrupted()) {
        
        for (Wire inWire : inWires) {
            JSONObject beforeTest;
            try {
                // beforeTest = inWire.getBq().take();
                if (inWire.getBq().isEmpty()) continue;

                beforeTest = inWire.getBq().poll();

                //log.info(inWire.getBq().size() + "");
                //og.info(beforeTest+"");
                log.info("checkTopic : "+checkTopic(beforeTest)+"");
                log.info("checkSensor : "+checkSensor(beforeTest)+"");
                if (checkSensor(beforeTest) && checkTopic(beforeTest)) {
                    for (Wire outWire : outWires) {
                        outWire.getBq().add(beforeTest);
                        log.info(outWire.getBq().size()+"");
                    }
                }
                   
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        
        // try {
        // Thread.sleep(300);
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // e.printStackTrace();
        // }
        // }

    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // log.info("preprocess node running");
                process();
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }

        }

    }
}
