package com.zhang.yang.generator;

import java.io.File;

import javax.lang.model.element.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zhang.yang.config.AnnotationPool;
import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.PackageConfig;
import com.zhang.yang.config.TableConfig;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class MapperCodeGenerator implements CodeGenerator {

    private Logger logger = LogManager.getLogger(MapperCodeGenerator.class);

    @Override
    public void generatorCode(Configuration configuration) {
        try {
            for (String tabName : configuration.getTableNames()) {

                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();

                ClassName moduleType = ClassName.get(packageConfig.getModulePackage(), tableConfig.getModuleName());

                TypeSpec typeSpec = TypeSpec.interfaceBuilder(tableConfig.getMapperName())
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("updateByPrimaryKeySelective")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("selectByPrimaryKey")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(moduleType)
                                .addParameter(Long.class, "id")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("insertSelective")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(moduleType, "record")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("deleteByPrimaryKey")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(Long.class)
                                .addParameter(Long.class, "id")
                                .build())
                        .addAnnotation(AnnotationPool.repository)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageConfig.getMapperPackage(), typeSpec).build();
                String filePath = packageConfig.getJavaBase() + File.separator;
                File file = new File(filePath);
                javaFile.writeTo(file);
                logger.info("java file {} already generator!", tableConfig.getMapperName());
            }
        } catch (Exception e) {
            logger.info("mapper java file generator error! {} ", e.getMessage());
        }
    }
}
