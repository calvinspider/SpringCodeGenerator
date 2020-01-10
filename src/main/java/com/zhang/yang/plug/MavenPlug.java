package com.zhang.yang.plug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.zhang.yang.generator.PlugIniter;

/**
 * Created by zhangyang56 on 2020/1/10.
 */
@Mojo(name = "codeGenerator")
public class MavenPlug extends AbstractMojo {

    Logger logger = LogManager.getLogger(MavenPlug.class);

    @Parameter
    private String configXmlPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logger.info("start generator spring framework code......");
        new PlugIniter().init(configXmlPath);
        logger.info("generator done......");
    }
}
