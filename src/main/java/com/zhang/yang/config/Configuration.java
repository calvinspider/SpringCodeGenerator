package com.zhang.yang.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhang.yang.jdbc.JDBCManager;
import com.zhang.yang.module.Field;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class Configuration {

    private DataBaseConfig dataBaseConfig;
    private PackageConfig packageConfig;
    private Map<String, TableConfig> tableConfig;
    private Set<String> tableNames;
    private Map<String, List<Field>> tableFields;

    private Configuration(Builder builder) {
        this.dataBaseConfig = builder.dataBaseConfig;
        this.packageConfig = builder.packageConfig;
        this.tableConfig = builder.tableConfig;
        if (this.tableConfig != null) {
            this.tableNames = tableConfig.keySet();
            this.tableFields = new JDBCManager().getColumn(this);
        }
    }

    public static Configuration.Builder ConfigurationBuilder() {
        return new Configuration.Builder();
    }

    public static final class Builder {

        private DataBaseConfig dataBaseConfig;
        private PackageConfig packageConfig;
        private Map<String, TableConfig> tableConfig;

        public Builder dataBaseConfig(DataBaseConfig dataBaseConfig) {
            this.dataBaseConfig = dataBaseConfig;
            return this;
        }

        public Builder packageConfig(PackageConfig packageConfig) {
            this.packageConfig = packageConfig;
            return this;
        }

        public Builder tableConfigs(Map<String, TableConfig> tableConfig) {
            this.tableConfig = tableConfig;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }

    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public Map<String, TableConfig> getTableConfig() {
        return tableConfig;
    }

    public Map<String, List<Field>> getTableFields() {
        return tableFields;
    }

    public Set<String> getTableNames() {
        return tableNames;
    }
}
