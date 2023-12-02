package com.nhnacademy.function;

import java.util.Set;

import com.nhnacademy.Wire;

@FunctionalInterface
public interface Executable {
    void execute(Set<Wire> inWires, Set<Wire> outWires);
}
