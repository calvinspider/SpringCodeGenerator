package com.zhang.yang.config;

import com.squareup.javapoet.ClassName;

/**
 * Created by zhangyang56 on 2020/1/10.
 */
public class AnnotationPool {

    public static ClassName autowire = ClassName.get("org.springframework.beans.factory.annotation", "Autowired");
    public static ClassName controller = ClassName.get("org.springframework.web.bind.annotation", "RestController");
    public static ClassName requestMapping = ClassName.get("org.springframework.web.bind.annotation", "RequestMapping");
    public static ClassName postMapping = ClassName.get("org.springframework.web.bind.annotation", "PostMapping");
    public static ClassName getMapping = ClassName.get("org.springframework.web.bind.annotation", "GetMapping");
    public static ClassName repository = ClassName.get("org.springframework.stereotype", "Repository");
    public static ClassName service = ClassName.get("org.springframework.stereotype", "Service");
    public static ClassName requestBody = ClassName.get("org.springframework.web.bind.annotation", "RequestBody");
    public static ClassName requestParam = ClassName.get("org.springframework.web.bind.annotation", "RequestParam");

}
