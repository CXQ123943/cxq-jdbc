package com.cxq.test;

import com.cxq.factory.DataSourceFactory;
import com.cxq.factory.DataSourcePoolByFactory;
import com.cxq.properties.DataSourcePoolByRes;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CXQ
 * @version 1.0
 */
public class DataSourcePoolByFactoryTest {

    @Test
    public void dataSourcePoolByResTest() {
        DataSourcePoolByFactory dataSource = DataSourceFactory.getDataSource();
        Connection connection = dataSource.getConnection();
        try {
            System.out.println(connection.isClosed() ? "fail" : "success");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.closeConnection(connection);
        }
    }

}
