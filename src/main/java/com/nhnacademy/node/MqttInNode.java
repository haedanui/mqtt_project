package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;

@Getter
public class MqttInNode extends Node implements Output {
    private final Set<Wire> outWires = new HashSet<>();

    @Override
    public void wireOut(Wire wire) {
        // TODO Auto-generated method stub
    }

    @Override
    public void process() {
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }
}
