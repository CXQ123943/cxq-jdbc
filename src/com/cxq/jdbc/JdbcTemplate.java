package com.cxq.jdbc;

import com.cxq.factory.DataSourceFactory;
import com.cxq.factory.DataSourcePoolByFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author CXQ
 * @version 1.0
 */
public class JdbcTemplate {
    private DataSourcePoolByFactory dataSourceFactory =  DataSourceFactory.getDataSource();

    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;

        try {
            //获取一个connection
            connection = dataSourceFactory.getConnection();
            //创建一个Statement，prepareStatement为Statement得到子类 可以使用“？”；
            prepareStatement = connection.prepareStatement(sql);
            resultSet = sendSqlAndGetResultSet(prepareStatement, params);
            iter(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(prepareStatement);
            dataSourceFactory.closeConnection(connection);
        }
        return null;
    }

    private ResultSet sendSqlAndGetResultSet(PreparedStatement prepareStatement, Object[] params) throws SQLException {
        for (int i = 0, j = params.length; i < j; i++) {
            prepareStatement.setObject(i +1, params[i]);
        }
        return prepareStatement.executeQuery();
    }

    /**
     * 迭代器
     * @param resultSet 获得的结果
     * */
    private void iter(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                System.out.print(resultSet.getInt("EMPNO") + "\t");
                System.out.print(resultSet.getString("ENAME") + "\t");
                System.out.print(resultSet.getInt("SAL") + "\t");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closePreparedStatement(PreparedStatement prepareStatement) {
        try {
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
