package com.nhnacademy.node;

import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class Node implements Runnable {
    private final UUID id;
    private final Thread thread;
    private final long createdTime;

    protected Node() {
        this.id = UUID.randomUUID();
        this.thread = new Thread(this);
        this.createdTime = System.currentTimeMillis();
    }

    public void start(){
        thread.start();
    }

    public abstract void process();

}
