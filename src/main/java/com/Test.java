package com;

import com.JNet.starter.JNet;

public class Test {

    public static void main(String[] args) throws Exception {
        JNet jNet = JNet.open(2048);
        jNet.post("/user/upload").handle((httpRequest, httpResponse) -> {
            System.out.println(httpRequest.getParameter("id"));
            System.out.println(httpRequest.getParameter("name"));
            return "upload success";
        });
        jNet.listen(8080);
    }
}
