package com.JNet.http;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class JNetManagement {

    private Map<String, HttpSession> httpSessions;
    private long sessionTimeout;

    private static volatile JNetManagement jNetManagement;

    private JNetManagement() {
        this.httpSessions = new HashMap<>();
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream inputStream = new FileInputStream("src/main/resources/jnet.xml");
            Document document = builder.build(inputStream);
            String timeoutString = document.getRootElement().getChild("httpSession").getChild("timeout").getText();
            this.sessionTimeout = Integer.parseInt(timeoutString) * 60 * 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSession(String sessionId, HttpSession session) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                session.destroy();
                System.out.println("session删除了");
            }
        }, this.sessionTimeout);
        this.httpSessions.put(sessionId, session);
    }

    public static JNetManagement getInstance() {
        if (jNetManagement == null) {
            synchronized (JNetManagement.class) {
                if (jNetManagement == null) {
                    jNetManagement = new JNetManagement();
                }
            }
        }
        return jNetManagement;
    }

    public void destroy(String sessionId) {
        this.httpSessions.remove(sessionId);
        System.gc();
    }
}