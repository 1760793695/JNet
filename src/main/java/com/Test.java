package com;

import com.JNet.starter.JNet;

public class Test {

    public static void main(String[] args) throws Exception {
        JNet jNet = JNet.open(2048);
        jNet.post("/user/login").handle((httpRequest, httpResponse) -> "hello");
        jNet.listen(8080);
    }
}
