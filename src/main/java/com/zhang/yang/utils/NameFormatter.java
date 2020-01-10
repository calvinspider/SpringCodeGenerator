package com.zhang.yang.utils;

import java.lang.reflect.Type;
import java.util.Date;

import com.zhang.yang.module.Field;

/**
 * Created by zhangyang56 on 2020/1/10.
 */
public class NameFormatter {

    public static void toHumpName(Field field) {
        String name = field.getFieldName();
        if (name.contains("_")) {
            String[] parts = name.split("_");
            String humpName = "";
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    humpName += parts[i];
                    continue;
                }
                String part = parts[i];
                String firstChar = part.charAt(0) + "";
                firstChar = firstChar.toUpperCase();
                humpName += firstChar + part.substring(1);
            }
            field.setModuleFieldName(humpName);
        } else {
            field.setModuleFieldName(name);
        }
    }

    public static String getHump(String name) {
        if (name.contains("_")) {
            String[] parts = name.split("_");
            String humpName = "";
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    humpName += parts[i];
                    continue;
                }
                String part = parts[i];
                String firstChar = part.charAt(0) + "";
                firstChar = firstChar.toUpperCase();
                humpName += firstChar + part.substring(1);
            }
            return humpName;
        } else {
            return name;
        }
    }

    public static String getMethodName(String name) {
        String first = name.charAt(0) + "";
        return "get" + first.toUpperCase() + name.substring(1);
    }

    public static String setMethodName(String name) {
        String first = name.charAt(0) + "";
        return "set" + first.toUpperCase() + name.substring(1);
    }

    public static String firstCharLower(String name) {
        String first = name.charAt(0) + "";
        return first.toLowerCase() + name.substring(1);
    }

    public static String firstCharUpper(String name) {
        String first = name.charAt(0) + "";
        return first.toUpperCase() + name.substring(1);
    }

    public static Type getFieldType(Field field) {
        String typeName = field.getFieldType();
        switch (typeName) {
            case "CHAR":
            case "VARCHAR":
            case "BLOB":
            case "TEXT":
                return String.class;
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
                return Double.class;
            case "TINYINT":
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                return Integer.class;
            case "MEDIUMINT":
            case "BIGINT":
                return Long.class;
            case "DATE":
            case "TIME":
            case "DATETIME":
            case "TIMESTAMP":
                return Date.class;
            default:
                return null;
        }
    }
}
