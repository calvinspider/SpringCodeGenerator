package com.zhang.yang.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class ConfigurationParse {

    public Configuration parse(String xmlPath) {
        try {

            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            Document doc = buildDoc(xmlPath);
            DataBaseConfig dataBaseConfig = parseDBConfig(xpath, doc);
            PackageConfig packageConfig = parsePackageConfig(xpath, doc);
            Map<String, TableConfig> tableConfigs = parseTableConfig(xpath, doc);

            return new Configuration(dataBaseConfig, packageConfig, tableConfigs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, TableConfig> parseTableConfig(XPath xpath, Document doc) {
        try {
            Map<String, TableConfig> configs = new HashMap<>();
            Object result = xpath.compile("//tables/child::table").evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                String tableName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String moduleName = nodes.item(i).getAttributes().getNamedItem("moduleName").getNodeValue();
                String serviceName = nodes.item(i).getAttributes().getNamedItem("serviceName").getNodeValue();
                String controllerName = nodes.item(i).getAttributes().getNamedItem("controllerName").getNodeValue();
                TableConfig tableConfig = new TableConfig(tableName, moduleName, serviceName, controllerName);
                configs.put(tableName, tableConfig);
            }
            return configs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private PackageConfig parsePackageConfig(XPath xpath, Document doc) {
        try {
            String modulePackage = (String) xpath.compile("//modulePackage/attribute::package").evaluate(doc, XPathConstants.STRING);
            String myBatisXmlPackage = (String) xpath.compile("//myBatisXmlPackage/attribute::package").evaluate(doc, XPathConstants.STRING);
            String mapperPackage = (String) xpath.compile("//mapperPackage/attribute::package").evaluate(doc, XPathConstants.STRING);
            String servicePackage = (String) xpath.compile("//servicePackage/attribute::package").evaluate(doc, XPathConstants.STRING);
            String controllerPackage = (String) xpath.compile("//controllerPackage/attribute::package").evaluate(doc, XPathConstants.STRING);
            String javaBase = (String) xpath.compile("//packages/attribute::javaBase").evaluate(doc, XPathConstants.STRING);
            PackageConfig config = new PackageConfig();
            config.setModulePackage(modulePackage);
            config.setXmlPackage(myBatisXmlPackage);
            config.setMapperPackage(mapperPackage);
            config.setServicePackage(servicePackage);
            config.setControllerPackage(controllerPackage);
            config.setJavaBase(javaBase);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DataBaseConfig parseDBConfig(XPath xpath, Document doc) {
        try {
            String driver = (String) xpath.compile("//dataBase/attribute::driver").evaluate(doc, XPathConstants.STRING);
            String url = (String) xpath.compile("//dataBase/attribute::url").evaluate(doc, XPathConstants.STRING);
            String userName =
                    (String) xpath.compile("//dataBase/attribute::userName").evaluate(doc, XPathConstants.STRING);
            String passWord =
                    (String) xpath.compile("//dataBase/attribute::passWord").evaluate(doc, XPathConstants.STRING);
            return new DataBaseConfig(url, driver, userName, passWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Document buildDoc(String xmlPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
