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
    private DataSourcePoolByFactory dataSourceFactory = DataSourceFactory.getDataSource();

    /**
     * 创建一个表DDL
     *
     * @param sql 接收的sql语句
     */
    public void execute(String sql) {
        boolean result = true;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSourceFactory.getConnection();
            statement = connection.createStatement();
            //execute(String sql)：结果只有是`ResultSet结果集时才返回true。
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

    /**
     * 处理单条DML操作(增删改)
     *
     * @param sql    接收的sql语句
     * @param params 传入一个一维数组（值）
     * @return 返回int类型结果，表示本次操作所影响的条目数
     */
    public int update(String sql, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int result = -1;
        try {
            connection = dataSourceFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            result = sendSql(preparedStatement, params);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(preparedStatement);
            dataSourceFactory.closeConnection(connection);
        }
        return result;
    }

    /**
     * 处理同类型多条DML操作(增删改)
     *
     * @param sql    接收的sql语句
     * @param params 传入一个二维数组（值）
     * @return 返回int类型数组结果，表示本次操作所影响的条目数组
     */
    public int[] batchUpdate(String sql, Object[]... params) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        int[] result = null;
        try {
            connection = dataSourceFactory.getConnection();
            //事务操作：关闭连接实例connection的自动提交操作
            connection.setAutoCommit(false);
            prepareStatement = connection.prepareStatement(sql);
            result = sendSqlAndGetIntArray(prepareStatement, params);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                //必须在finally()块中将连接实例的自动提交属性还原
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeStatement(prepareStatement);
            dataSourceFactory.closeConnection(connection);
        }
        return result;
    }

    /**
     * SQL多查询
     *
     * @param sql    接收的sql语句
     * @param params 传入一个数组
     * @return 返回一个List<Map>结构的结果集
     */
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

    /**
     * SQL单查询
     * 中间同样调用queryForList()
     *
     * @param sql    接收的sql语句
     * @param params 传入一个数组
     * @return 返回一个Map结构的结果集
     */
    public Map<String, Object> queryForMap(String sql, Object... params) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = queryForList(sql, params);
        if (!list.isEmpty()) {
            resultMap = list.get(0);
        }
        return resultMap;
    }

    //---------------------------Encapsulate JDBC methods------------------------------------------

    /**
     * sql多查询方法 -
     * sendSqlAndGetResultSet()，负责向SQL中的问号赋值，以及发送SQL并接收ResultSet类型结果。
     *
     * @param preparedStatement 预执行区域;
     * @param params            一个一维数组;
     */
    private ResultSet sendSqlAndGetResultSet(PreparedStatement preparedStatement, Object[] params) throws SQLException {
        for (int i = 0, j = params.length; i < j; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    /**
     * SQL多查询 - 结果集类型转换
     * 封装一个 `changeResultSetToList()`，负责将 `ResultSet` 转化为 `List<Map>` 结构
     *
     * @param resultSet 结果集
     * @return 返回一个List<Map>结构的结果集
     */
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
                tempMap.put(columnName, value);
            }
            resultList.add(tempMap);
        }
        return resultList;
    }

    /**
     * 处理同类型多操作方法 DML
     * 封装一个sendSqlAndGetIntArray()，负责向SQL中的问号赋值，以及发送SQL并接收int类型结果。
     *
     * @param preparedStatement 预执行区域;
     * @param params            一个二维数组;
     */
    private int[] sendSqlAndGetIntArray(PreparedStatement preparedStatement, Object[]... params) throws SQLException {
        for (Object[] param : params) {
            for (int i = 0, j = param.length; i < j; i++) {
                preparedStatement.setObject(i + 1, param[i]);
            }
            preparedStatement.addBatch();
        }
        return preparedStatement.executeBatch();
    }

    /**
     * 处理单条DML操作 -
     * 封装一个sendSqlAndGetIntArray()，负责向SQL中的问号赋值，以及发送SQL并接收int类型结果
     *
     * @param preparedStatement 预执行区域;
     * @param params            一个二维数组;
     */
    private int sendSql(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0, j = params.length; i < j; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }


    /**
     * 迭代器
     * 多查询测试用的，正常执行得注释
     *
     * @param resultSet 获得的结果
     */
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
