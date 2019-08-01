## 配置中心(apollo)

github: https://github.com/ctripcorp/apollo

版本: v1.4.0

## 自定义扩展

####服务端
1.将eureka去掉,使用公共consul作为注册中心

修改后源码: [点我](../pom.xml)

####客户端
1.meta server address 加载方式

2.支持配置密文
``
1. 配置增加
jasypt.encryptor.password = skycloud

2.使用com.skycloud.base.config.util.EncryptUtil将值加密
EncryptUtil.PASSWORD="skycloud"
EncryptUtil.getEncryptedParams(值);
输出有{input=xxx,password=skycloud}

3.将密文加到配置中心
ENC(密文)

``

3.增加日志级别动态调整 (发布后即可生效)
logging.level.xxxxxxxx = info

xxxxxxxx为包名


4.增加某些属性直接按照文件形式读取,而统一配置注册中心后,无法知道其路径问题,无法读取文件问题

在配置中心配置如下文件,多个以 , 分割
格式: 
apollo.custom.replace = 属性名称=配置中心文件名称(加格式后缀)=替换成哪种格式文件

例子:
apollo.custom.replace = spring.redis.redisson.config=redisson.properties=yml

流程:
1.在配置中心配置redisson.properties属性文件,本客户端会从apollo拉取文件后,根据此文件创建新yml格式文件

2.将spring.redis.redisson.config属性增加到environment ，且路径是替换成apollo本地缓存目录文件路径


例子:
配置中心拉取文件: /Users/sky/develop/workspace_github/skycloud-service/skycloud-service-member/target/classes/config-cache/skycloud-service-member+default+redisson.properties
客户端创建新文件: /Users/sky/develop/workspace_github/skycloud-service/skycloud-service-member/target/classes/config-cache/skycloud-service-member+default+redisson.yml
spring.redis.redisson.config 路径为 [客户端创建新文件] 路径

注意: apollo.custom.replace 配置中只有按 = 分割 ,第三个格式文件为 yml 时才创建文件 ,其他仅替换路径


## 使用方式

```
1. 申请 apollo id

2. 在 apollo portal 创建项目

3. 引入jar包
    
    <dependency>
        <groupId>com.sky.skycloud</groupId>
        <artifactId>skycloud-base-config</artifactId>
        <version>${project.version}</version>
    </dependency>
    
4. 在application.yml添加配置

    app:
      id: skycloud-base-gateway
    apollo:
      meta: http://192.168.1.224:8080
```

## Future

1.服务端邮件接入

## 介绍
[apollo介绍PDF](../doc/image/apollo.pdf)

![图片展示](../doc/image/apollo.png)