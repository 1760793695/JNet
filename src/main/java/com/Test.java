package com;

import com.JNet.http.HttpCookie;
import com.JNet.starter.JNet;

public class Test {

    public static void main(String[] args) throws Exception {
        JNet jNet = JNet.open();
        jNet.get("/user/login").handle((httpRequest, httpResponse) -> {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.add("name", "zhangsan");
            httpResponse.setCookie(httpCookie);
            return "hello";
        });
        jNet.listen(8080);
    }
}
