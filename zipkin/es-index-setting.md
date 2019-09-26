## 通过kibana设置ES索引模板

### 编写模板
此处设置索引前缀为logback- 的全部索引,分片数3,副本数0
```
{
    "order": 1,
    "index_patterns": "logback-*",
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 0
    }
}
```

### 在kibana中执行
#[logback-es-template](../docs/image/logback-es-template.png)
