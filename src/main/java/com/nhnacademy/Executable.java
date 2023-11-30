package com.nhnacademy;

import java.util.Set;


@FunctionalInterface
public interface Executable {
    void execute(Set<Wire> inWires, Set<Wire> outWires);
}
