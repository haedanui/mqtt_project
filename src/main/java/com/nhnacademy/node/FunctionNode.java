package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import com.nhnacademy.Executable;
import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class FunctionNode extends Node implements Input, Output {
    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    private final Executable function;

    public FunctionNode(Executable function) {
        this.function = function;
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
    public void run() {
        while (!Thread.interrupted()) {

            try {
                process();

            } catch (Exception e) {
                log.warn(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void process() {
        function.execute(inWires, outWires);
    }
}
