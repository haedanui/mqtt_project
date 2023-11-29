package com.nhnacademy.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.Wire;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;

class DebugNodeTest {
    Wire w;
    MqttInNode n1;
    DebugNode n2;

    @BeforeEach
    void setUp() {
        w = new Wire();
        n1 = new MqttInNode();
        n2 = new DebugNode();
        n1.wireOut(w);
        n2.wireIn(w);
        n1.start();
        n2.start();
    }

    @Test
    void typeTest() {
        Assertions.assertTrue(n1 instanceof MqttInNode);
        Assertions.assertTrue(n2 instanceof DebugNode);
    }

    @Test
    void checkSend() {

    }

}
