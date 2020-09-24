package com.cxq.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSource {
    private static List<Connection> connectionPool;
    private static int connectionPoolSize = 10;


    static {
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(driver);
            initConnectionPool();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void initConnectionPool() {
        connectionPool = new LinkedList<>();
        for (int i = 0; i < connectionPoolSize; i++) {
            connectionPool.add(createNewConnection());
        }
    }

    private static Connection createNewConnection() {
        String user = "steven";
        String password = "steven";
        String url = "jdbc:mysql://localhost:3306/dbcxq";
        String urlParam = "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
        url += urlParam;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private synchronized Connection getConnection() {
        //获取一个新的连接
        Connection connection = null;
        if (connectionPool.isEmpty()) {
            connection = createNewConnection();
        } else {
            connection = connectionPool.remove(0);
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connectionPool.size() < connectionPoolSize) {
                connectionPool.add(connection);
            } else {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

