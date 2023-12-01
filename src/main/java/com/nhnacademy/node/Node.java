package com.nhnacademy.node;

import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class Node {
    private final UUID id;
    private final long createdTime;

    protected Node() {
        this.id = UUID.randomUUID();
        this.createdTime = System.currentTimeMillis();
    }
}
