package com.cxq.test;

import com.cxq.properties.DataSourcePoolByRes;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSourcePoolByResTest {

    @Test
    public void dataSourcePoolByResTest() {
        DataSourcePoolByRes dataSourcePoolByRes = new DataSourcePoolByRes();
        Connection connection = dataSourcePoolByRes.getConnection();
        try {
            System.out.println(connection.isClosed() ? "fail" : "success");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSourcePoolByRes.closeConnection(connection);
        }
    }

}
