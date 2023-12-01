package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;

import lombok.Getter;

@Getter
public class MqttOutNode extends ActiveNode implements Input {
    private final Set<Wire> inWires = new HashSet<>();

    @Override
    public void wireIn(Wire wire) {
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
