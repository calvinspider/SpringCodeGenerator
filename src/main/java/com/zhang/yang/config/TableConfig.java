package com.zhang.yang.config;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class TableConfig {

    private String tableName;
    private String moduleName;
    private String serviceName;
    private String controllerName;

    public TableConfig(String tableName, String moduleName, String serviceName, String controllerName) {
        this.tableName = tableName;
        this.moduleName = moduleName;
        this.serviceName = serviceName;
        this.controllerName = controllerName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
}
