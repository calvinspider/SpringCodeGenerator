package com.zhang.yang.config;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class GeneratorManager {

    private Logger logger = LogManager.getLogger(GeneratorManager.class);

    private Configuration configuration;

    public void parseConfiguration(String xmlPath) {
        if (StringUtils.isEmpty(xmlPath)) {
            logger.error("generator xml file no exist!");
            return;
        }
        this.configuration = new ConfigurationParse().parse(xmlPath);
    }

    public void connectDataBase() {
        JDBCManager.setConnection(this.configuration.getDataBaseConfig());
    }

    public void loadTable() {
        Map<String, List<Field>> tableFields = new HashMap<>();
        Set<String> tableNames = this.configuration.getTableConfig().keySet();
        for (String tableName : tableNames) {
            String sql = "select * from " + tableName + " limit 1";
            Map<String, String> fileds = JDBCManager.query(sql);
            tableFields.put(tableName, getField(fileds));
        }
        this.configuration.setTableNames(tableNames);
        this.configuration.setTableFields(tableFields);
    }

    public void generatorModule() {
        try {
            for (String tabName : this.configuration.getTableNames()) {
                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();
                List<Field> fields = this.configuration.getTableFields().get(tabName);
                TypeSpec.Builder builder = TypeSpec.classBuilder(tableConfig.getModuleName())
                        .addModifiers(Modifier.PUBLIC);
                for (Field field : fields) {
                    toHumpName(field);
                    Type type = getFieldType(field);
                    if (type == null) {
                        logger.error("no Identify field type {}", field.getFieldType());
                        continue;
                    }
                    builder.addField(
                            FieldSpec.builder(type, field.getModuleFieldName()).addModifiers(Modifier.PRIVATE)
                                    .build());
                    builder.addMethod(MethodSpec.methodBuilder(getMethodName(field.getModuleFieldName()))
                            .addModifiers(Modifier.PUBLIC).returns(type)
                            .addStatement("return $N", field.getModuleFieldName()).build());

                    builder.addMethod(MethodSpec.methodBuilder(setMethodName(field.getModuleFieldName()))
                            .addModifiers(Modifier.PUBLIC).returns(void.class)
                            .addParameter(type, field.getModuleFieldName())
                            .addStatement("this.$N = $N", field.getModuleFieldName(), field.getModuleFieldName())
                            .build());
                }
                TypeSpec tableType = builder.build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getModulePackage(), tableType).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatorMapper() {
        try {
            for (String tabName : this.configuration.getTableNames()) {

                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();

                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                ClassName repositoryAnnotation = ClassName.get("org.springframework.stereotype", "Repository");

                TypeSpec typeSpec = TypeSpec.interfaceBuilder(tableConfig.getModuleName() + "Mapper")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("updateByPrimaryKeySelective")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectByPrimaryKey")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(moduleType)
                                .addParameter(Integer.class, "id")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insertSelective")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteByPrimaryKey")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(Integer.class, "id")
                                .build())
                        .addAnnotation(repositoryAnnotation)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getMapperPackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatorIService() {

        try {
            for (String tabName : this.configuration.getTableNames()) {

                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                ClassName service = ClassName.get("org.springframework.stereotype", "Service");
                TypeSpec typeSpec = TypeSpec.interfaceBuilder(tableConfig.getModuleName() + "Service")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(moduleType)
                                .addParameter(Integer.class, "id")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(int.class)
                                .addParameter(Integer.class, "id")
                                .build())
                        .addAnnotation(service)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getServicePackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatorServiceImpl() {

        try {
            for (String tabName : this.configuration.getTableNames()) {

                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();

                ClassName mappertype =
                        ClassName.get(packageConfig.getMapperPackage(), tableConfig.getModuleName() + "Mapper");
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                ClassName autowire = ClassName.get("org.springframework.beans.factory.annotation", "Autowired");
                ClassName service = ClassName.get("org.springframework.stereotype", "Service");
                ClassName iservice =
                        ClassName.get(packageConfig.getServicePackage(), tableConfig.getModuleName() + "Service");

                String serviceFieldName = firstCharLower(tableConfig.getModuleName() + "Mapper");
                TypeSpec typeSpec = TypeSpec.classBuilder(tableConfig.getModuleName() + "ServiceImpl")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.updateByPrimaryKeySelective(record);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(moduleType)
                                .addParameter(Integer.class, "id")
                                .addCode("return $N.selectByPrimaryKey(id);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.insertSelective(record);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(Integer.class, "id")
                                .addCode("return $N.deleteByPrimaryKey(id);\n", serviceFieldName)
                                .build())
                        .addAnnotation(service)
                        .addField(FieldSpec
                                .builder(mappertype, serviceFieldName, Modifier
                                        .PRIVATE).addAnnotation(autowire).build())
                        .addSuperinterface(iservice)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getServicePackage() + ".impl", typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatorController() {

        try {
            for (String tabName : this.configuration.getTableNames()) {

                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();

                ClassName serviceType =
                        ClassName.get(packageConfig.getServicePackage(), tableConfig.getModuleName() + "Service");
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                ClassName autowire = ClassName.get("org.springframework.beans.factory.annotation", "Autowired");
                ClassName controller = ClassName.get("org.springframework.web.bind.annotation", "RestController");
                ClassName requestMapping = ClassName.get("org.springframework.web.bind.annotation", "RequestMapping");
                ClassName postMapping = ClassName.get("org.springframework.web.bind.annotation", "PostMapping");
                ClassName getMapping = ClassName.get("org.springframework.web.bind.annotation", "GetMapping");

                String serviceFieldName = firstCharLower(tableConfig.getModuleName() + "Service");
                TypeSpec typeSpec = TypeSpec.classBuilder(tableConfig.getModuleName() + "Controller")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.update(record);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(postMapping)
                                        .addMember("value", "$S", "/update")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(moduleType)
                                .addParameter(Integer.class, "id")
                                .addCode("return $N.selectById(id);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(getMapping)
                                        .addMember("value", "$S", "/selectById")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.insert(record);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(postMapping)
                                        .addMember("value", "$S", "/insert")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(int.class)
                                .addParameter(Integer.class, "id")
                                .addCode("return $N.deleteById(id);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(getMapping)
                                        .addMember("value", "$S", "/deleteById")
                                        .build())
                                .build())
                        .addAnnotation(controller)
                        .addAnnotation(AnnotationSpec.builder(requestMapping)
                                .addMember("value", "$S", "/" + firstCharLower(tableConfig.getModuleName()))
                                .build())
                        .addField(FieldSpec
                                .builder(serviceType, serviceFieldName, Modifier
                                        .PRIVATE).addAnnotation(autowire).build())
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getControllerPackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatorXml() {

        try {

            for (String tabName : this.configuration.getTableNames()) {

                TableConfig tableConfig = this.configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = this.configuration.getPackageConfig();
                List<Field> fields = this.configuration.getTableFields().get(tabName);

                String mapperPackage = packageConfig.getMapperPackage() + "." + tableConfig.getModuleName() + "Mapper";
                String moduelPackage = packageConfig.getModulePackage() + "." + tableConfig.getModuleName();

                Document document = DocumentHelper.createDocument();
                document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd",
                        null);


                Element mapper = document.addElement("mapper");
                mapper.addAttribute("namespace", mapperPackage);

                Element insert = mapper.addElement("insert");

                insert.addAttribute("id", "insertSelective");
                insert.addAttribute("keyColumn", "id");
                insert.addAttribute("keyProperty", "id");
                insert.addAttribute("parameterType", moduelPackage);
                insert.addAttribute("useGeneratedKeys", "true");
                insert.setText(" insert into " + tabName);

                Element trim1 = insert.addElement("trim");

                trim1.addAttribute("prefix", "(");
                trim1.addAttribute("suffix", ")");
                trim1.addAttribute("suffixOverrides", ",");

                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Element eif = trim1.addElement("if");

                    eif.addAttribute("test", field.getModuleFieldName() + "!= null");
                    if (i == fields.size() - 1) {
                        eif.setText(field.getFieldName());
                    } else {
                        eif.setText(field.getFieldName() + ",");
                    }
                }

                Element trim2 = insert.addElement("trim");

                trim2.addAttribute("prefix", "values (");
                trim2.addAttribute("suffix", ")");
                trim2.addAttribute("suffixOverrides", ",");

                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Element eif = trim2.addElement("if");

                    eif.addAttribute("test", field.getModuleFieldName() + "!= null");
                    if (i == fields.size() - 1) {
                        eif.setText(field.getModuleFieldName());
                    } else {
                        eif.setText(field.getModuleFieldName() + ",");
                    }
                }

                Element select = mapper.addElement("select");
                select.addAttribute("id", "selectByPrimaryKey");
                select.addAttribute("resultType", moduelPackage);
                select.setText("select * from " + tabName + " where id = #{id}");

                Element delete = mapper.addElement("delete");
                delete.addAttribute("id", "deleteByPrimaryKey");
                delete.addAttribute("parameterType", moduelPackage);
                delete.setText("delete from " + tabName + " where id = #{id}");

                Element update = mapper.addElement("update");
                update.addAttribute("id", "updateByPrimaryKeySelective");
                update.addAttribute("parameterType", moduelPackage);
                update.setText("update " + tabName);
                Element set = update.addElement("set");
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Element eif = set.addElement("if");
                    eif.addAttribute("test", field.getModuleFieldName() + "!= null");
                    if (i == fields.size() - 1) {
                        eif.setText(field.getFieldName() + "=" + field.getModuleFieldName());
                    } else {
                        eif.setText(field.getFieldName() + "=" + field.getModuleFieldName() + ",");
                    }
                }
                update.addText("where id = #{id}");

                OutputFormat format = OutputFormat.createPrettyPrint(); //设置XML文档输出格式
                format.setEncoding("UTF-8"); //设置XML文档的编码类型
                format.setSuppressDeclaration(true);
                format.setIndent(true); //设置是否缩进
                format.setIndent(" "); //以空格方式实现缩进
                format.setNewlines(true); //设置是否换行

                Writer fileWriter = new FileWriter(packageConfig.getXmlPackage() + File.separator + tableConfig
                        .getModuleName() + "Mapper.xml");
                XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
                xmlWriter.write(document);
                xmlWriter.flush();
                xmlWriter.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private Type getFieldType(Field field) {
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

    private void toHumpName(Field field) {
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

    private String getMethodName(String name) {
        String first = name.charAt(0) + "";
        return "get" + first.toUpperCase() + name.substring(1);
    }

    private String setMethodName(String name) {
        String first = name.charAt(0) + "";
        return "set" + first.toUpperCase() + name.substring(1);
    }

    private String firstCharLower(String name) {
        String first = name.charAt(0) + "";
        return first.toLowerCase() + name.substring(1);
    }
}
