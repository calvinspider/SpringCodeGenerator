package com.zhang.yang.config;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class PackageConfig {

    private String modulePackage;

    private String xmlPackage;

    private String mapperPackage;

    private String servicePackage;

    private String controllerPackage;

    private String javaBase;

    public String getModulePackage() {
        return modulePackage;
    }

    public void setModulePackage(String modulePackage) {
        this.modulePackage = modulePackage;
    }

    public String getXmlPackage() {
        return xmlPackage;
    }

    public void setXmlPackage(String xmlPackage) {
        this.xmlPackage = xmlPackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public String getJavaBase() {
        return javaBase;
    }

    public void setJavaBase(String javaBase) {
        this.javaBase = javaBase;
    }
}
