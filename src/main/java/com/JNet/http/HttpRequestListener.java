package com.JNet.http;

@FunctionalInterface
public interface HttpRequestListener {

    String handler(HttpRequest httpRequest);
}
