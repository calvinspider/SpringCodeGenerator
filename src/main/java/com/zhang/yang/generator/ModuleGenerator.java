package com.zhang.yang.generator;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zhang.yang.config.Configuration;
import com.zhang.yang.config.PackageConfig;
import com.zhang.yang.config.TableConfig;
import com.zhang.yang.module.Field;
import com.zhang.yang.utils.NameFormatter;

/**
 * Created by zhangyang56 on 2020/1/10.
 */
public class ModuleGenerator implements CodeGenerator {

    private Logger logger = LogManager.getLogger(ModuleGenerator.class);

    @Override
    public void generatorCode(Configuration configuration) {

        try {
            for (String tabName : configuration.getTableNames()) {
                TableConfig tableConfig = configuration.getTableConfig().get(tabName);
                PackageConfig packageConfig = configuration.getPackageConfig();
                List<Field> fields = configuration.getTableFields().get(tabName);
                TypeSpec.Builder builder = TypeSpec.classBuilder(tableConfig.getModuleName())
                        .addModifiers(Modifier.PUBLIC);
                for (Field field : fields) {
                    NameFormatter.toHumpName(field);
                    Type type = NameFormatter.getFieldType(field);
                    if (type == null) {
                        logger.error("no Identify field type {}", field.getFieldType());
                        continue;
                    }
                    builder.addField(
                            FieldSpec.builder(type, field.getModuleFieldName()).addModifiers(Modifier.PRIVATE)
                                    .build());
                    builder.addMethod(MethodSpec.methodBuilder(NameFormatter.getMethodName(field.getModuleFieldName()))
                            .addModifiers(Modifier.PUBLIC).returns(type)
                            .addStatement("return this.$N", field.getModuleFieldName()).build());

                    builder.addMethod(MethodSpec.methodBuilder(NameFormatter.setMethodName(field.getModuleFieldName()))
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
                logger.info("java file {} already generator!", tableConfig.getModuleName());
            }
        } catch (Exception e) {
            logger.error("generator module file error {}", e.getMessage());
        }
    }
}
