package com.zhang.yang.config;

import org.apache.commons.lang3.StringUtils;

import com.zhang.yang.utils.NameFormatter;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class TableConfig {

    private String tableName;
    private String moduleName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    private String mapperName;

    public TableConfig(String tableName, String moduleName, String serviceName, String controllerName,
                       String mapperName) {
        this.tableName = tableName;
        this.moduleName = moduleName;
        if (StringUtils.isEmpty(moduleName)) {
            this.moduleName = NameFormatter.firstCharUpper(NameFormatter.getHump(tableName));
        }
        this.serviceName = serviceName;
        if (StringUtils.isEmpty(serviceName)) {
            this.serviceName = moduleName + "Service";
        }
        this.serviceImplName = this.serviceName + "Impl";
        this.controllerName = controllerName;
        if (StringUtils.isEmpty(controllerName)) {
            this.controllerName = moduleName + "Controller";
        }
        this.mapperName = mapperName;
        if (StringUtils.isEmpty(mapperName)) {
            this.mapperName = moduleName + "Mapper";
        }
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

    public String getServiceImplName() {
        return serviceImplName;
    }

    public void setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }
}
