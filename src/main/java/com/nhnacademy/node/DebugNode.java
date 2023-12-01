package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DebugNode extends ActiveNode implements Input {

    protected DebugNode(String name) {
        super(name);
    }

    private Set<Wire> inputWires = new HashSet<>();

    @Override
    public void wireIn(Wire wire) {
        inputWires.add(wire);
    }

    @Override
    public void process() {
        try {
            for (Wire wire : inputWires) {
                var messageQ = wire.getBq();

                JSONObject msg = messageQ.poll();

                log.info(msg.toString());
            }

        } catch (Exception ignore) {
        }
    }
}
