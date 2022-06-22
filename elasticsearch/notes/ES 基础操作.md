# ES 基础操作

理解索引 文档 倒排索引等概念  然后学习各类查询 文档的维护 索引的维护 会基本的使用了 就可以开始学搭集群 学习内部分片原理 体会elastic的含义 首先学习各类聚合分析  会基本使用 再深入理解倒排索引 bitmap   docvalues  这些数据结构的区别  以及他们具体的应用场景 然后是文档更新内部原理 理解提交点 段合并 版本内外控制等等  学完这些  这时你打开官方文档 你会发现es给了很多各种各样的参数  有关于索引的 mapping的  分布式的 查询的 维护文档的  只有学了原理才能理解每个参数各自的含义  才能根据自己实际的业务需求有针对性的用好es  最后就是深入搜索和控制相关度了   理解各种搜索内部原理  分片查询原理 学习相关度评分的内部算法  和控制相关度的各种手段   引用官方文档里的一句话“控制相关度是最后10%要做的事情”

## 索引创建（index）

```java
PUT shopping
```

```java
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "shopping"
}
```

PUT 有幂等性，不论操作多少次，返回的结果都是一样的，而 POST 不具有

## 索引查询

```java
GET shopping
```

```java
{
  "shopping" : {
    "aliases" : { },
    "mappings" : { },
    "settings" : {
      "index" : {
        "creation_date" : "1655864085874",
        "number_of_shards" : "1",
        "number_of_replicas" : "1",
        "uuid" : "87EDC_N5TDOc2MtD8C3H2g",
        "version" : {
          "created" : "7060299"
        },
        "provided_name" : "shopping"
      }
    }
  }
}
```

```java
GET _cat/indices?v  // 查询所有索引信息
```

```java
health status index                    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .kibana_task_manager_1   lmdM4sHYQG6z_OP1fl4StA   1   0          2            0     31.9kb         31.9kb
green  open   .apm-agent-configuration 7NuWRJlfTJ-9hsH8J868cQ   1   0          0            0       283b           283b
green  open   .kibana_1                0DVDynPNTaiZqgAnoptEYg   1   0         13            1     41.5kb         41.5kb
yellow open   shopping                 87EDC_N5TDOc2MtD8C3H2g   1   1          0            0       230b           230b

```

## 索引删除

```java
DELETE shopping
```

```java
{
  "acknowledged" : true
}
```

## 文档创建（document）

```java
POST shopping/_doc
{
  "title": "小米手机",
  "category": "小米",
  "images": "https://image.baidu.com/search/xm.jpg",
  "price": 3999.00
}
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "inM8iYEBVi1I2GSrwzUQ", // 随机ID，每次返回都不一致，所以不能使用 PUT
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

```java
PUT shopping/_doc/1     // 指定固定ID，则可以使用 PUT
{
  "title": "小米手机",
  "category": "小米",
  "images": "https://image.baidu.com/search/xm.jpg",
  "price": 3999.00
}
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

```java
PUT shopping/_create/2      //_create 与 _doc 效果一致
{
  "title": "华为手机",
  "category": "华为",
  "images": "https://image.baidu.com/search/hw.jpg",
  "price": 4999.00
}
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "2",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 2,
  "_primary_term" : 1
}
```

## 文档查询

```java
GET shopping/_doc/1    // 根据ID查询
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 1,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "title" : "小米手机",
    "category" : "小米",
    "images" : "https://image.baidu.com/search/xm.jpg",
    "price" : 3999.0
  }
}
```

```java
GET shopping/_search     // 查询所有 doc
```

```java
{
  "took" : 4,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 4999.0
        }
      },
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 1.0,
        "_source" : {
          "title" : "华为手机",
          "category" : "华为",
          "images" : "https://image.baidu.com/search/hw.jpg",
          "price" : 5999.0
        }
      }
    ]
  }
}

```

## 文档更新

### 全量更新

全量更新每次返回的结果都是一致的，所有使用 PUT

```java
PUT shopping/_doc/1
{
  "title": "小米手机",
  "category": "小米",
  "images": "https://image.baidu.com/search/xm.jpg",
  "price": 4999.00
}
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 3,
  "_primary_term" : 1
}
```

### 局部更新

```java
POST shopping/_update/2
{
  "doc": {
    "price": 5999.00
  }
}
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "2",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 4,
  "_primary_term" : 1
}
```

## 文档删除

```java
DELETE shopping/_doc/2   // 根据 ID 删除
```

```java
{
  "_index" : "shopping",
  "_type" : "_doc",
  "_id" : "2",
  "_version" : 3,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 5,
  "_primary_term" : 1
}
```

## 条件查询

```java
GET shopping/_search
{
  "query": {
    "match": {
      "category": "小米"
    }
  }
}
```

```java
{
  "took" : 13,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 0.7133499,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.7133499,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        }
      }
    ]
  }
}
```

### 分页

```java
GET shopping/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 2
}
```

from：（页码-1）* size

### 指定字段

```java
GET shopping/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 2,
  "_source": ["title"]
}
```

```java
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "title" : "小米手机"
        }
      }
    ]
  }
}
```

### 排序

```java
GET shopping/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 2,
  "_source": ["title","price"],
  "sort": [
    {
      "price": {
        "order": "desc" // 降序
      }
    }
  ]
}
```

```java
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : null,
        "_source" : {
          "price" : 5999.0,
          "title" : "小米手机"
        },
        "sort" : [
          5999.0
        ]
      },
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : null,
        "_source" : {
          "price" : 4999.0,
          "title" : "华为手机"
        },
        "sort" : [
          4999.0
        ]
      }
    ]
  }
}

```

### 多条件查询

查询 category 和 price 字段同时满足的条件

```java
GET shopping/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "category": "小米"
          }
        },
        {
          "match": {
            "price": "5999.00"
          }
        }
      ]
    }
  }
}
```

```java
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 1.9400072,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.9400072,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        }
      }
    ]
  }
}
```

查询 category 任意满足的条件

```java
GET shopping/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "category": "小米"
          }
        },
        {
          "match": {
            "category": "华为"
          }
        }
      ]
    }
  }
}
```

```java
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 1.9616582,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 1.9616582,
        "_source" : {
          "title" : "华为手机",
          "category" : "华为",
          "images" : "https://image.baidu.com/search/hw.jpg",
          "price" : 4999.0
        }
      },
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.9400072,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        }
      }
    ]
  }
}

```

### 范围查询

查询 price 字段指定范围的条件

```java
GET shopping/_search
{
  "query": {
    "bool": {
      "filter": [
        {"range": {
          "price": {
            "gte": 4000,
            "lte": 6000
          }
        }}
      ]
    }
  }
}
```

```java
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 0.0,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.0,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        }
      },
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 0.0,
        "_source" : {
          "title" : "华为手机",
          "category" : "华为",
          "images" : "https://image.baidu.com/search/hw.jpg",
          "price" : 4999.0
        }
      }
    ]
  }
}

```

### 完全匹配

ES 会将每个词拆分开，放入倒排索引中

而 match 会将条件拆开，每个词进行全文检索，所以会匹配所有满足的条件，如果要完全匹配则需使用 match_phrase

```java
GET shopping/_search
{
  "query": {
    "match": {
      "category": "小华"
    }
  }
}
```

```java
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 0.9808291,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 0.9808291,
        "_source" : {
          "title" : "华为手机",
          "category" : "华为",
          "images" : "https://image.baidu.com/search/hw.jpg",
          "price" : 4999.0
        }
      },
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.4700036,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        }
      }
    ]
  }
}

```

```java
GET shopping/_search
{
  "query": {
    "match_phrase": {
      "category": "小华"
    }
  }
}
```

```java
{
  "took" : 6,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  }
}

```

### 高亮

```java
GET shopping/_search
{
  "query": {
    "match_phrase": {
      "category": "小米"
    }
  },
  "highlight": {
    "fields": {
      "category": {}
    }
  }
}

```

```java
{
  "took" : 142,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 0.9400072,
    "hits" : [
      {
        "_index" : "shopping",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.9400072,
        "_source" : {
          "title" : "小米手机",
          "category" : "小米",
          "images" : "https://image.baidu.com/search/xm.jpg",
          "price" : 5999.0
        },
        "highlight" : {
          "category" : [
            "<em>小</em><em>米</em>"
          ]
        }
      }
    ]
  }
}

```

## 聚合查询

```java
GET shopping/_search
{
  "aggs": { // 操作类型：聚合
    "price_group": {  // 名称
      "terms": {      // 聚合类型：分组
        "field": "price"  // 字段
      }
    }
  },
  "size": 0
}
```

```java
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "price_group" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : 3999.0,
          "doc_count" : 1
        },
        {
          "key" : 4999.0,
          "doc_count" : 1
        },
        {
          "key" : 5999.0,
          "doc_count" : 1
        }
      ]
    }
  }
}
```

```java
GET shopping/_search
{
  "aggs": {
    "price_avg": {
      "avg": {   // 聚合类型：平均值
        "field": "price"
      }
    }
  },
  "size": 0
}
```

```java
{
  "took" : 6,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "price_avg" : {
      "value" : 4999.0
    }
  }
}
```

## 映射关系

```java
PUT user

PUT user/_mapping
{
  "properties": {
    "name": {
      "type": "text",
      "index": true
    },
    "sex": {
      "type": "keyword",
      "index": true
    },
    "tel": {
      "type": "keyword",
      "index": false
    }
  }
}

GET user/_mapping
```

```java
{
  "user" : {
    "mappings" : {
      "properties" : {
        "name" : {
          "type" : "text"
        },
        "sex" : {
          "type" : "keyword" // 关键字不会被分开
        },
        "tel" : {
          "type" : "keyword",
          "index" : false  // index 为 false 则不能被查询
        }
      }
    }
  }
}
```

```java
// 准备测试数据
PUT user/_doc/1
{
  "name": "张三",
  "sex": "男的",
  "tel": 111111111111
}
```

查询 type 为 text，index 为 true 的 name 字段

```java
GET user/_search
{
  "query": {
    "match": {
      "name": "三"
    }
  }
}
```

```java
{
  "took" : 266,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 0.2876821,
    "hits" : [
      {
        "_index" : "user",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.2876821,
        "_source" : {
          "name" : "张三",
          "sex" : "男的",
          "tel" : 111111111111
        }
      }
    ]
  }
}
```

查询 type 为 keyword，index 为 true 的 sex 字段

```java
GET user/_search
{
  "query": {
    "match": {
      "sex": "男"
    }
  }
}
```

```java
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  }
}
```

查询 type 为 keyword，index 为 false 的 tel 字段

```java
GET user/_search
{
  "query": {
    "match": {
      "tel": 111111111111
    }
  }
}
```

```java
{
  "error" : {
    "root_cause" : [
      {
        "type" : "query_shard_exception",
        "reason" : "failed to create query: {\n  \"match\" : {\n    \"tel\" : {\n      \"query\" : 111111111111,\n      \"operator\" : \"OR\",\n      \"prefix_length\" : 0,\n      \"max_expansions\" : 50,\n      \"fuzzy_transpositions\" : true,\n      \"lenient\" : false,\n      \"zero_terms_query\" : \"NONE\",\n      \"auto_generate_synonyms_phrase_query\" : true,\n      \"boost\" : 1.0\n    }\n  }\n}",
        "index_uuid" : "b8-jTULoQ6GM6NGeLXzosQ",
        "index" : "user"
      }
    ],
    "type" : "search_phase_execution_exception",
    "reason" : "all shards failed",
    "phase" : "query",
    "grouped" : true,
    "failed_shards" : [
      {
        "shard" : 0,
        "index" : "user",
        "node" : "fYVP1puzSCeL_0eMRRcO3Q",
        "reason" : {
          "type" : "query_shard_exception",
          "reason" : "failed to create query: {\n  \"match\" : {\n    \"tel\" : {\n      \"query\" : 111111111111,\n      \"operator\" : \"OR\",\n      \"prefix_length\" : 0,\n      \"max_expansions\" : 50,\n      \"fuzzy_transpositions\" : true,\n      \"lenient\" : false,\n      \"zero_terms_query\" : \"NONE\",\n      \"auto_generate_synonyms_phrase_query\" : true,\n      \"boost\" : 1.0\n    }\n  }\n}",
          "index_uuid" : "b8-jTULoQ6GM6NGeLXzosQ",
          "index" : "user",
          "caused_by" : {
            "type" : "illegal_argument_exception",
            "reason" : "Cannot search on field [tel] since it is not indexed."
          }
        }
      }
    ]
  },
  "status" : 400
}
```
