package com.nhnacademy.node;

import java.util.HashSet;
import java.util.Set;

import com.nhnacademy.Input;
import com.nhnacademy.Wire;

import lombok.extern.slf4j.Slf4j;

/**
 * 이전 노드에 있는 WireOut을 Wire클래스를 통해 같은 Blocking Queue를 공유하며
 * WireOut을 통해 Queue에 들어온 자료를 받아 출력하는 노드이다.
 * 잘 이해가 가지 않는다면 Node-Red의 초록색 Debug 블럭을 생각하면 된다.
 */
@Slf4j
public class DebugNode extends Node implements Input {
    private Set<Wire> inputWire = new HashSet<>();

    /**
     * 와이어를 inputWire Set 에 넣는다.
     * 그러면 inputWire를 통해 해당 노드에 연결된 와이어에 있는 Blocking Queue에 접근 할 수있다.
     */
    @Override
    public void wireIn(Wire wire) {
        inputWire.add(wire);
    }

    /**
     * 들어온 스레드를 시작하며 인터럽트가 들어올때까지 반복한다.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * input와이어에 있는 큐가 비어있는 동안 메시지를 출력하지 않다가 큐에 메시지가 들어오면 해당 메시지를 로그로 출력함
     */
    @Override
    public void process() {
        try {
            for (Wire wires : inputWire) {
                if (wires.getBq().isEmpty()) {
                    continue;
                }

                log.info(wires.getBq().poll().toString());
            }
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
