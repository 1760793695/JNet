package com.JNet.http;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

public class HttpCookie {

    private String key;
    private String value;
    private String expires;
    private String domain;
    private String path;

    public HttpCookie() {
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream inputStream = new FileInputStream("src/main/resources/jnet.xml");
            Document document = builder.build(inputStream);
            String timeoutString = document.getRootElement().getChild("cookie").getChild("timeout").getText();
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + Long.parseLong(timeoutString) * 60 * 1000);
            this.expires = date.toGMTString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void add(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return key + "=" + value + ";expires=" + expires + ";domain=" + domain + ";path=" + path;
    }
}
