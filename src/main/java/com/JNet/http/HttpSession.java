package com.JNet.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private String sessionId;
    private Map<String, Object> attributes;

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public String getSessionId() {
        return sessionId;
    }

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
        this.attributes = new ConcurrentHashMap<>();
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void destroy() {
        JNetManagement jNetManagement = JNetManagement.getInstance();
        jNetManagement.destroy(this.sessionId);
    }
}
