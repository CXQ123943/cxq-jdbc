package com.cxq.test;

import com.cxq.pool.DataSourcePool;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSourcePoolTest {

    @Test
    public void dataSourceTest() {
        DataSourcePool dataSourcePool = new DataSourcePool();
        Connection connection = dataSourcePool.getConnection();
        try {
            System.out.println(connection.isClosed() ? "fail" : "success");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSourcePool.closeConnection(connection);
        }
    }

}
