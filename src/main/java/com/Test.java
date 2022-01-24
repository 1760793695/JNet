package com;

import com.JNet.starter.JNet;

public class Test {

    public static void main(String[] args) throws Exception {
        JNet jNet = JNet.openWithXML();
        jNet.listen(8080);
    }
}
