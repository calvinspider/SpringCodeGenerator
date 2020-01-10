package com.zhang.yang.generator;

import com.zhang.yang.config.GeneratorManager;

/**
 * Created by zhangyang56 on 2020/1/8.
 */
public class PlugIniter {

    public void init(String configXmlPath) {
        GeneratorManager manager = new GeneratorManager();
        manager.parseConfiguration(
                configXmlPath);
        manager.connectDataBase();
        manager.loadTable();
        manager.generatorModule();
        manager.generatorXml();
        manager.generatorMapper();
        manager.generatorIService();
        manager.generatorServiceImpl();
        manager.generatorController();
    }
}
