package com.cxq.pool;

import com.cxq.datasource.DataSource;
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
        DataSource dataSource = new DataSource();
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
