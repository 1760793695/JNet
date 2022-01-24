package com;

import com.JNet.http.HttpRequest;
import com.JNet.interceptor.Interceptor;

public class SimpleInterceptor implements Interceptor {
    @Override
    public boolean preHandler(HttpRequest httpRequest) {
        if (httpRequest.uri().equals("/favicon.ico")) {
            return false;
        } else {
            return true;
        }
    }
}
