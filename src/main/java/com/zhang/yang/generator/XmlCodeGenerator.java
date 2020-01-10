package com.zhang.yang.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.PackageConfig;
import com.zhang.yang.config.TableConfig;
import com.zhang.yang.module.Field;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class XmlCodeGenerator implements CodeGenerator {

    @Override
    public void generatorCode(Configuration configuration) {
        try {

            for (String tabName : configuration.getTableNames()) {

                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();
                List<Field> fields = configuration.getTableFields().get(tabName);

                String mapperPackage = packageConfig.getMapperPackage() + "." + tableConfig.getMapperName();
                String moduelPackage = packageConfig.getModulePackage() + "." + tableConfig.getModuleName();

                Document document = DocumentHelper.createDocument();
                document.addDocType("mapper",
                        "-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd",
                        null);

                Element mapper = document.addElement("mapper");
                mapper.addAttribute("namespace", mapperPackage);

                Element insert = mapper.addElement("insert");

                insert.addAttribute("id", "insertSelective");
                insert.addAttribute("keyColumn", "id");
                insert.addAttribute("keyProperty", "id");
                insert.addAttribute("parameterType", moduelPackage);
                insert.addAttribute("useGeneratedKeys", "true");
                insert.setText("\n\tinsert into " + tabName);

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
                select.setText("\n\tselect * from " + tabName + " where id = #{id}");

                Element delete = mapper.addElement("delete");
                delete.addAttribute("id", "deleteByPrimaryKey");
                delete.addAttribute("parameterType", moduelPackage);
                delete.setText("\n\tdelete from " + tabName + " where id = #{id}");

                Element update = mapper.addElement("update");
                update.addAttribute("id", "updateByPrimaryKeySelective");
                update.addAttribute("parameterType", moduelPackage);
                update.setText("\n\tupdate " + tabName);
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
                format.setTrimText(false);
                format.setIndent(true);
                Writer fileWriter = new FileWriter(packageConfig.getXmlPackage() + File.separator + tableConfig
                        .getModuleName() + "Mapper.xml");
                XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
                xmlWriter.setEscapeText(false);
                xmlWriter.write(document);
                xmlWriter.flush();
                xmlWriter.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
