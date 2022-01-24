package com.JNet.http;

import java.util.Map;

public class HttpSession {

    private String sessionId;
    private Map<String, Object> attributes;

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void destroy() {
        JNetManagement jNetManagement = JNetManagement.getInstance();
        jNetManagement.destroy(this.sessionId);
    }
}
