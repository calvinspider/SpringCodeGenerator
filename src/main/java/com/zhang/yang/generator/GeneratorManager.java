package com.zhang.yang.generator;

import org.apache.commons.lang3.StringUtils;

import com.zhang.yang.config.Configuration;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class GeneratorManager {
    public static void generator(Configuration configuration) {

        if (StringUtils.isNotEmpty(configuration.getPackageConfig().getMapperPackage())) {
            new MapperCodeGenerator().generatorCode(configuration);
        }
        if (StringUtils.isNotEmpty(configuration.getPackageConfig().getServicePackage())) {
            new ServiceCodeGenerator().generatorCode(configuration);
        }
        if (StringUtils.isNotEmpty(configuration.getPackageConfig().getModulePackage())) {
            new ModuleGenerator().generatorCode(configuration);
        }
        if (StringUtils.isNotEmpty(configuration.getPackageConfig().getControllerPackage())) {
            new ControllerCodeGenerator().generatorCode(configuration);
        }
        if (StringUtils.isNotEmpty(configuration.getPackageConfig().getXmlPackage())) {
            new XmlCodeGenerator().generatorCode(configuration);
        }
    }
}
