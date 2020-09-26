package com.cxq.jdbc;

import com.cxq.factory.DataSourceFactory;
import com.cxq.factory.DataSourcePoolByFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CXQ
 * @version 1.0
 */
public class JdbcTemplate {
    private DataSourcePoolByFactory dataSourceFactory =  DataSourceFactory.getDataSource();

    public void execute(String sql) {
        boolean result = true;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSourceFactory.getConnection();
            statement = connection.createStatement();
            result = statement.execute(sql);
            if (!result) {
                System.out.println("创建成功");
            } else {
                System.out.println("创建表失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
            dataSourceFactory.closeConnection(connection);
        }
    }



    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;


        try {
            //获取一个connection
            connection = dataSourceFactory.getConnection();
            //创建一个Statement，prepareStatement为Statement得到子类 可以使用“？”；
            prepareStatement = connection.prepareStatement(sql);
            resultSet = sendSqlAndGetResultSet(prepareStatement, params);
            result = changeResultSetToList(resultSet);
            //iter(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(prepareStatement);
            dataSourceFactory.closeConnection(connection);
        }
        return result;
    }

    public Map<String, Object> queryForMap(String sql, Object... params) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = queryForList(sql, params);
        if (!list.isEmpty()) {
            resultMap = list.get(0);
        }
        return resultMap;
    }

    private ResultSet sendSqlAndGetResultSet(PreparedStatement prepareStatement, Object[] params) throws SQLException {
        for (int i = 0, j = params.length; i < j; i++) {
            prepareStatement.setObject(i +1, params[i]);
        }
        return prepareStatement.executeQuery();
    }

    private List<Map<String, Object>> changeResultSetToList(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        //获取表的总列数
        int columnCount = metaData.getColumnCount();
        //创建一个临时Map
        Map<String, Object> tempMap;

        //rows 获取行数
        while (resultSet.next()) {
            tempMap = new HashMap<>(10);
            //数据库没有从0开始的概念
            for (int i = 1; i <= columnCount; i++) {
                //通过元数据获取列名
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                tempMap.put(columnName,value);
            }
            resultList.add(tempMap);
        }
        return resultList;
    }

    /**
     * 迭代器
     * 测试用的，正常执行得注释
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

    private void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
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
