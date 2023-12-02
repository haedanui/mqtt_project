package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.nhnacademy.Input;
import com.nhnacademy.Output;
import com.nhnacademy.Wire;
import com.nhnacademy.function.Executable;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class FunctionNode extends ActiveNode implements Input, Output {
    private Set<Wire> outWires = new HashSet<>();
    private Set<Wire> inWires = new HashSet<>();

    private final Executable function;

    public FunctionNode(Executable function, String name) {
        super(name);
        this.function = function;
    } 

    @Override
    public JSONObject toJson() {
        // TODO Auto-generated method stub
        return null;
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
        function.execute(inWires, outWires);
    }
}
