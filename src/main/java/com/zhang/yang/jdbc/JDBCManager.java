package com.zhang.yang.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.DataBaseConfig;
import com.zhang.yang.module.Field;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class JDBCManager {

    private static Connection conn = null;
    private static PreparedStatement preparedStatement = null;

    public Map<String, List<Field>> loadTable(Configuration configuration) {
        if (conn == null) {
            setConnection(configuration.getDataBaseConfig());
        }
        Map<String, List<Field>> tableFields = new HashMap<>();
        for (String tableName : configuration.getTableNames()) {
            String sql = "select * from " + tableName + " limit 1";
            Map<String, String> fileds = query(sql);
            tableFields.put(tableName, getField(fileds));
        }
        return tableFields;
    }

    public Map<String, List<Field>> getColumn(Configuration configuration) {
        Map<String, List<Field>> tableFields = new HashMap<>();
        try {
            if (conn == null) {
                setConnection(configuration.getDataBaseConfig());
            }
            DatabaseMetaData dbmd = conn.getMetaData();
            for (String tableName : configuration.getTableNames()) {
                ResultSet resultSet = dbmd.getTables(null, "%", tableName, new String[] {"TABLE"});
                Map<String, String> map = new HashMap<>();
                while (resultSet.next()) {
                    String name = resultSet.getString("TABLE_NAME");
                    if (name.equals(tableName)) {
                        ResultSet rs = dbmd.getColumns(null, "%", name, "%");
                        while (rs.next()) {
                            String colName = rs.getString("COLUMN_NAME");
                            String dbType = rs.getString("TYPE_NAME");
                            map.put(colName, dbType);
                        }
                    }
                }
                tableFields.put(tableName, getField(map));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableFields;
    }

    private List<Field> getField(Map<String, String> fileds) {
        List<Field> fields = new ArrayList<>();
        for (Map.Entry<String, String> entry : fileds.entrySet()) {
            Field field = new Field();
            field.setFieldName(entry.getKey());
            field.setFieldType(entry.getValue());
            fields.add(field);
        }
        return fields;
    }

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
    private static Map<String, String> query(String sql) {
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

    private static void setConnection(DataBaseConfig config) {
        try {
            Class.forName(config.getDriver());
            conn = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     *
     * @param rs 结果集
     */
    private static void free(ResultSet rs) {

        free(null, preparedStatement, rs);
    }

    /**
     * 释放资源
     *
     * @param conn
     * @param statement
     * @param rs
     */
    private static void free(Connection conn, Statement statement, ResultSet rs) {
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
