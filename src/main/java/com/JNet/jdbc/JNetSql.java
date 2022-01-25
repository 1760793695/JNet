package com.JNet.jdbc;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JNetSql {

    private final Connection connection;
    private volatile static JNetSql jNetSql;

    public static JNetSql getInstance() throws Exception {
        if (jNetSql == null) {
            synchronized (JNetSql.class) {
                if (jNetSql == null) {
                    jNetSql = new JNetSql();
                }
            }
        }
        return jNetSql;
    }

    public static JNetSql getInstance(Map<String, String> connectionParams) throws Exception {
        if (jNetSql == null) {
            synchronized (JNetSql.class) {
                if (jNetSql == null) {
                    jNetSql = new JNetSql(connectionParams);
                }
            }
        }
        return jNetSql;
    }

    private JNetSql(Map<String, String> connectionParams) throws Exception {
        String driver = connectionParams.get("driver");
        String url = connectionParams.get("url");
        String username = connectionParams.get("username");
        String password = connectionParams.get("password");
        Class.forName(driver);
        this.connection = DriverManager.getConnection(url, username, password);
    }

    private JNetSql() throws Exception {
        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream = JNetSql.class.getClassLoader().getResourceAsStream("jnet.xml");
        Document document = saxBuilder.build(inputStream);
        Element mysql = document.getRootElement().getChild("mysql");
        String driverName = mysql.getChild("driver").getAttributeValue("class");
        Class.forName(driverName);
        Element connection = mysql.getChild("connection");
        String url = connection.getChild("url").getText();
        String username = connection.getChild("username").getText();
        String password = connection.getChild("password").getText();
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public <T> List<T> selectList(String sql, Class<T> resultType, Object... params) throws SQLException, InstantiationException, IllegalAccessException {
        PreparedStatement statement = this.connection.prepareStatement(sql);
        int position = 1;
        if (params != null && params.length > 1) {
            for (Object param : params) {
                statement.setObject(position++, param);
            }
        }
        ResultSet resultSet = statement.executeQuery();
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            T t = resultType.newInstance();
            Field[] fields = resultType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(t, resultSet.getObject(field.getName()));
            }
            result.add(t);
        }
        return result;
    }

    public <T> T selectOne(String sql, Class<T> resultType, Object... params) throws SQLException, InstantiationException, IllegalAccessException {
        PreparedStatement statement = this.connection.prepareStatement(sql);
        int position = 1;
        if (params != null && params.length > 0) {
            for (Object param : params) {
                statement.setObject(position++, param);
            }
        }
        ResultSet resultSet = statement.executeQuery();
        T t = resultType.newInstance();
        if (resultSet.next()) {
            Field[] fields = resultType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(t, resultSet.getObject(field.getName()));
            }
        }
        return t;
    }

    public boolean executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(sql);
        int position = 1;
        if (params != null && params.length > 0) {
            for (Object param : params) {
                statement.setObject(position++, param);
            }
        }
        int effect = statement.executeUpdate();
        return effect > 0;
    }
}
