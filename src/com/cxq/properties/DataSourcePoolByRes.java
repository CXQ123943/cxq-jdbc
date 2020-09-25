package com.cxq.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSourcePoolByRes {
    private static List<Connection> connectionPool;
    private static int connectionPoolSize;
    private static String driver;
    private static String user;
    private static String password;
    private static String url;


    static {
        readProperties();
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

    private static void readProperties() {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("db");
        driver = bundle.getString("jdbc.driver");
        user = bundle.getString("jdbc.user");
        password = bundle.getString("jdbc.password");
        url = bundle.getString("jdbc.url");
        connectionPoolSize = Integer.parseInt(bundle.getString("jdbc.connectionPoolSize"));
    }

    private static Connection createNewConnection() {
        //创建一个新的连接
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * If connectionPool is null,invoke createConnection.
     * <p>
     * If connectionPool is not null,return linkList[0]
     */
    public synchronized Connection getConnection() {
        //获取连接
        Connection connection = null;
        if (connectionPool.isEmpty()) {
            connection = createNewConnection();
        } else {
            connection = connectionPool.remove(0);
        }
        return connection;
    }

    /**
     * 关闭连接
     *
     * @param connection what connection will be return.
     */
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

