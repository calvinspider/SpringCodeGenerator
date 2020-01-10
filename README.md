# SpringCodeGenerator
### 此项目编译后为MAVEN插件，可以根据数据库一键生成MVC项目从mybatis xml到Controller的所有代码（初版）
#### 生成时需要配置以下几项：
- dataBase：目标数据库连接信息
- packages javaBase：maven项目java包的绝对路径
- modulePackage package：实体类的包名
- myBatisXmlPackage package：xml文件夹的绝对路径
- mapperPackage package：mapper接口的包名
- servicePackage package：service的包名
- controllerPackage package：controller的包名
- table name moduleName serviceName controllerName：分别对应表名，实体类名，service类名，controller类名
#### 插件目前未上传到maven仓库中，需要手动安装，直接clone项目后mvn install即可
可通过以下pom片段引用,configXmlPath为generatorCode.xml的绝对路径
```
<plugin>
    <groupId>com.zhang.yang</groupId>
    <artifactId>spring-code-generator</artifactId>
    <version>1.0.0</version>
    <configuration>
        <configXmlPath>/resources/generatorCode.xml</configXmlPath>
    </configuration>
</plugin>
```
