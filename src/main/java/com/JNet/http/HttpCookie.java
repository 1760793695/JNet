package com.JNet.http;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.util.Date;

public class HttpCookie {

    private String key;
    private String value;
    private String expires;
    private String path;

    public HttpCookie() {
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream inputStream = HttpCookie.class.getClassLoader().getResourceAsStream("jnet.xml");
            Document document = builder.build(inputStream);
            String timeoutString = document.getRootElement().getChild("cookie").getChild("timeout").getText();
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + Integer.parseInt(timeoutString) * 60 * 1000L);
            this.expires = date.toGMTString();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return key + "=" + value + ";path=" + path;
    }
}
