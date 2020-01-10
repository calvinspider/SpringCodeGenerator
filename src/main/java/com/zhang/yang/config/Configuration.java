package com.zhang.yang.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class Configuration {

    private DataBaseConfig dataBaseConfig;
    private PackageConfig packageConfig;
    private Map<String, TableConfig> tableConfig;
    private Map<String, List<Field>> tableFields;
    private Set<String> tableNames;

    public Configuration(DataBaseConfig dataBaseConfig, PackageConfig packageConfig,
                         Map<String, TableConfig> tableConfig) {
        this.dataBaseConfig = dataBaseConfig;
        this.packageConfig = packageConfig;
        this.tableConfig = tableConfig;
    }

    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public void setPackageConfig(PackageConfig packageConfig) {
        this.packageConfig = packageConfig;
    }

    public Map<String, TableConfig> getTableConfig() {
        return tableConfig;
    }

    public void setTableConfig(Map<String, TableConfig> tableConfig) {
        this.tableConfig = tableConfig;
    }

    public Map<String, List<Field>> getTableFields() {
        return tableFields;
    }

    public void setTableFields(Map<String, List<Field>> tableFields) {
        this.tableFields = tableFields;
    }

    public Set<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(Set<String> tableNames) {
        this.tableNames = tableNames;
    }
}
