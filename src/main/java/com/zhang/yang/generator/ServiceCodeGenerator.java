package com.zhang.yang.generator;

import java.io.File;

import javax.lang.model.element.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zhang.yang.config.AnnotationPool;
import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.PackageConfig;
import com.zhang.yang.config.TableConfig;
import com.zhang.yang.utils.NameFormatter;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class ServiceCodeGenerator implements CodeGenerator {

    private Logger logger = LogManager.getLogger(ServiceCodeGenerator.class);

    @Override
    public void generatorCode(Configuration configuration) {
        try {
            for (String tabName : configuration.getTableNames()) {

                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                TypeSpec typeSpec = TypeSpec.interfaceBuilder(tableConfig.getServiceName())
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(moduleType)
                                .addParameter(Long.class, "id")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(Long.class, "id")
                                .build())
                        .addAnnotation(AnnotationPool.service)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getServicePackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
                logger.info("java file {} already generator!", tableConfig.getServiceName());
            }
        } catch (Exception e) {
            logger.info("service interface java file generator error! {} ", e.getMessage());
        }

        try {
            for (String tabName : configuration.getTableNames()) {

                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();

                ClassName mappertype = ClassName.get(packageConfig.getMapperPackage(), tableConfig.getMapperName());
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());
                ClassName iservice = ClassName.get(packageConfig.getServicePackage(), tableConfig.getServiceName());

                String serviceFieldName = NameFormatter.firstCharLower(tableConfig.getMapperName());
                TypeSpec typeSpec = TypeSpec.classBuilder(tableConfig.getServiceImplName())
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.updateByPrimaryKeySelective(record);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(moduleType)
                                .addParameter(Long.class, "id")
                                .addCode("return $N.selectByPrimaryKey(id);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .addCode("return $N.insertSelective(record);\n", serviceFieldName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(Long.class, "id")
                                .addCode("return $N.deleteByPrimaryKey(id);\n", serviceFieldName)
                                .build())
                        .addAnnotation(AnnotationPool.service)
                        .addField(FieldSpec
                                .builder(mappertype, serviceFieldName, Modifier
                                        .PRIVATE).addAnnotation(AnnotationPool.autowire).build())
                        .addSuperinterface(iservice)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getServicePackage() + ".impl", typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
                logger.info("java file {} already generator!", tableConfig.getServiceImplName());

            }
        } catch (Exception e) {
            logger.info("service impl java file generator error! {} ", e.getMessage());
        }
    }
}
