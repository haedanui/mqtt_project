package com.nhnacademy.node;

import java.util.UUID;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Node {
    private final UUID id;
    private final long createdTime;
    private final String type;
    private String name;

    protected Node(String type, String name) {
        this.id = UUID.randomUUID();
        this.createdTime = System.currentTimeMillis();

        this.type = type;
        this.name = name;
    }

    public JSONObject export() {
        JSONObject obj = new JSONObject();

        obj.put("id", getId());
        obj.put("type", getType());

        return obj;
    }
}
