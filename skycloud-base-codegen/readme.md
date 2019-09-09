### 代码生成工具

```
请求地址 post 

address:5003/generator/code
参数
{
"packageName":"com.skycloud.service",
"author":"sky",
"moduleName":"xxx",
"tablePrefix":"dc",
"tableName":"dc_order",
"datasource":"one"
}
```

```
参数说明:

/**
 * 包名
 */
private String packageName;
/**
 * 作者
 */
private String author;
/**
 * 模块名称
 */
private String moduleName;
/**
 * 表前缀
 */
private String tablePrefix;

/**
 * 表名称
 */
private String tableName;

/**
 * 表备注
 */
private String comments;
/**
 * 数据源,默认 one
 */
private String datasource;
```