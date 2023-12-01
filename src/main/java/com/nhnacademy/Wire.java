package com.nhnacademy;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import lombok.Getter;

@Getter
public class Wire {
    private final UUID id;
    private final Queue<JSONObject> bq = new LinkedList<>();

    public Wire() {
        id = UUID.randomUUID();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Wire)) return false;
        
        return this.id.equals(((Wire) obj).id);
    }
}
