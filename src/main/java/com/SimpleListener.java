package com;

import com.JNet.http.HttpRequest;
import com.JNet.http.HttpRequestListener;
import com.JNet.http.HttpResponse;

public class SimpleListener implements HttpRequestListener {
    @Override
    public String handler(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getSession().getAttribute("name") == null) {
            System.out.println("session中还没有name这个值");
            httpRequest.getSession().setAttribute("name", "zhangsan");
        } else {
            System.out.println(httpRequest.getSession().getAttribute("name"));
        }
        return "hello";
    }
}
