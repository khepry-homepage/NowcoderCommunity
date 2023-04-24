package com.nowcoder.community.entity;

import java.util.Map;
public class Event {
    private String eventType;
    private int userId;
    private int entityUserId;
    private int entityType;
    private int entityId;
    private Map<String, Object> data;

    public String getEventType() {
        return eventType;
    }

    public Event setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int userId) {
        this.entityUserId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
