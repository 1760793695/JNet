package com;

import com.JNet.jdbc.JNetSql;

import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        JNetSql sql = new JNetSql();
        List<Admin> admins = sql.executeQuery("select id, username, password from `admin`", Admin.class, (Object) null);
        System.out.println(admins.get(0));
    }
}
