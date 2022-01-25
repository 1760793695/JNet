package com;

import com.JNet.http.HttpRequest;
import com.JNet.http.HttpRequestListener;
import com.JNet.http.HttpResponse;

public class SimpleListener implements HttpRequestListener {
    @Override
    public String handler(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpRequest.headers().forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
        System.out.println("------------------------------------------------------------\n\n\n");
        return "hello";
    }
}
