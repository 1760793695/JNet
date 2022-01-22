package com.JNet.http;

public class Route {

    private String method;
    private HttpRequestListener listener;

    public void setMethod(String method) {
        this.method = method;
    }

    public HttpRequestListener getListener() {
        return listener;
    }

    public String getMethod() {
        return method;
    }

    public void handle(HttpRequestListener listener) {
        this.listener = listener;
    }

    public void setListener(HttpRequestListener listener) {
        this.listener = listener;
    }
}
