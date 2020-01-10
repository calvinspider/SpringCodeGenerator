package com.zhang.yang.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class JDBCManager {

    private static Connection conn = null;
    private static PreparedStatement preparedStatement = null;

    /**
     * 用于查询，返回结果集
     *
     * @param sql sql语句
     *
     * @return 结果集
     *
     * @
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> query(String sql) {
        ResultSet rs = null;
        Map<String, String> result = new HashMap<>();
        try {
            getPreparedStatement(sql);
            rs = preparedStatement.executeQuery();
            ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    String columnTypeName = data.getColumnTypeName(i);
                    String columnName = data.getColumnName(i);
                    result.put(columnName, columnTypeName);
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(rs);
        }
        return null;
    }

    /**
     * 获取PreparedStatement
     *
     * @param sql
     *
     * @
     */
    private static void getPreparedStatement(String sql) {
        try {
            preparedStatement = conn.prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Connection setConnection(DataBaseConfig config) {
        try {
            Class.forName(config.getDriver());
            conn = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放资源
     *
     * @param rs 结果集
     */
    public static void free(ResultSet rs) {

        free(null, preparedStatement, rs);
    }

    /**
     * 释放资源
     *
     * @param conn
     * @param statement
     * @param rs
     */
    public static void free(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            freeResultSet(rs);
        }
        if (statement != null) {
            freeStatement(statement);
        }
        if (conn != null) {
            freeConnection(conn);
        }
    }

    /**
     * 释放连接
     *
     * @param conn
     */
    private static void freeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放statement
     *
     * @param statement
     */
    private static void freeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放resultset
     *
     * @param rs
     */
    private static void freeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
