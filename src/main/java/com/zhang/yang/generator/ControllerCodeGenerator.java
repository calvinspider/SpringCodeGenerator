package com.zhang.yang.generator;

import java.io.File;

import javax.lang.model.element.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.zhang.yang.config.AnnotationPool;
import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.PackageConfig;
import com.zhang.yang.config.TableConfig;
import com.zhang.yang.utils.NameFormatter;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class ControllerCodeGenerator implements CodeGenerator {

    private Logger logger = LogManager.getLogger(ControllerCodeGenerator.class);

    @Override
    public void generatorCode(Configuration configuration) {
        try {
            for (String tabName : configuration.getTableNames()) {

                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();

                ClassName serviceType =
                        ClassName.get(packageConfig.getServicePackage(), tableConfig.getServiceName());
                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());

                String serviceFieldName = NameFormatter.firstCharLower(tableConfig.getServiceName());
                TypeSpec typeSpec = TypeSpec.classBuilder(tableConfig.getControllerName())
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("update")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(ParameterSpec.builder(moduleType,"record").addAnnotation(AnnotationPool
                                        .requestBody).build())
                                .addCode("return $N.update(record);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(AnnotationPool.postMapping)
                                        .addMember("value", "$S", "/update")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(moduleType)
                                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(AnnotationPool
                                        .requestParam).build())
                                .addCode("return $N.selectById(id);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(AnnotationPool.getMapping)
                                        .addMember("value", "$S", "/selectById")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insert")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(ParameterSpec.builder(moduleType, "record").addAnnotation(AnnotationPool
                                        .requestBody).build())
                                .addCode("return $N.insert(record);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(AnnotationPool.postMapping)
                                        .addMember("value", "$S", "/insert")
                                        .build())
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteById")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(Long.class)
                                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(AnnotationPool
                                        .requestParam).build())
                                .addCode("return $N.deleteById(id);\n", serviceFieldName)
                                .addAnnotation(AnnotationSpec.builder(AnnotationPool.getMapping)
                                        .addMember("value", "$S", "/deleteById")
                                        .build())
                                .build())
                        .addAnnotation(AnnotationPool.controller)
                        .addAnnotation(AnnotationSpec.builder(AnnotationPool.requestMapping)
                                .addMember("value", "$S",
                                        "/" + NameFormatter.firstCharLower(tableConfig.getModuleName()))
                                .build())
                        .addField(FieldSpec
                                .builder(serviceType, serviceFieldName, Modifier
                                        .PRIVATE).addAnnotation(AnnotationPool.autowire).build())
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getControllerPackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
                logger.info("java file {} already generator!", tableConfig.getControllerName());

            }
        } catch (Exception e) {
            logger.info("controller java file generator error! {} ", e.getMessage());
        }

    }
}
