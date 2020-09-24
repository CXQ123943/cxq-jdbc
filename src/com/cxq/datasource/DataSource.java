package com.cxq.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSource {
    private static Connection connection;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";
        String user = "steven";
        String password = "steven";
        String url = "jdbc:mysql://localhost:3306/dbcxq";
        String urlParam = "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
        url += urlParam;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() {
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

