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

### 通过 ID 删除

```java
DELETE shopping/_doc/2 
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

### 根据查询条件删除

```java
POST /twitter/_delete_by_query
{
  "query": {
    "match": {
      "message": "some message"
    }
  }
}
```

```java
POST shopping/_delete_by_query
{
  "query": {
    "match_all": {}
  }
}
```

## 条件查询 (query)

```java
准备测试数据
https://github.com/elastic/elasticsearch/blob/7.6/docs/src/test/resources/accounts.json

PUT /bank/_bulk
{"index":{"_id":"1"}}
{"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}
{"index":{"_id":"6"}}
{"account_number":6,"balance":5686,"firstname":"Hattie","lastname":"Bond","age":36,"gender":"M","address":"671 Bristol Street","employer":"Netagy","email":"hattiebond@netagy.com","city":"Dante","state":"TN"}
...
```

### match 全文检索

match 全文检索会将 Mill lane 进行分词，查出 address 中包含 Mill lane 的所有记录，并给出相关性得分

```java
GET bank/_search
{
  "query": {
    "match": {
      "address": "Mill lane"
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
      "value" : 19,
      "relation" : "eq"
    },
    "max_score" : 9.507477,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "136",
        "_score" : 9.507477,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "970",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      }
      ......
    ]
  }
}

```

### match_all 查询所有

```java
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [  // 排序
    {
      "balance": {
        "order": "desc"
      }
    },
    {
      "age": {
        "order": "asc"
      }
    }
  ],
  "from": 0,  // 分页 （页码-1）* size
  "size": 3,
  "_source": ["firstname", "balance", "age"] // 指定返回字段
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
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "248",
        "_score" : null,
        "_source" : {
          "firstname" : "West",
          "balance" : 49989,
          "age" : 36
        },
        "sort" : [
          49989,
          36
        ]
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "854",
        "_score" : null,
        "_source" : {
          "firstname" : "Jimenez",
          "balance" : 49795,
          "age" : 25
        },
        "sort" : [
          49795,
          25
        ]
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "240",
        "_score" : null,
        "_source" : {
          "firstname" : "Oconnor",
          "balance" : 49741,
          "age" : 35
        },
        "sort" : [
          49741,
          35
        ]
      }
    ]
  }
}

```

### match_phrase 短语匹配

match_phrase 将整个条件当成一个短语匹配，不会进行分词，返回 address 字段中包含此短语的所有结果

```java
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address": "Mill lane"
    }
  }
}
```

```java
{
  "took" : 5,
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
    "max_score" : 9.507477,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "136",
        "_score" : 9.507477,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      }
    ]
  }
}

```

### multi_match 多字段匹配

multi_match 会尽可能的多地段匹配，同样也会分词

```java
GET bank/_search
{
  "query": {
    "multi_match": {
      "query": "mill Lopezo",
      "fields": ["address", "city"]
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
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 6.5059485,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "970",
        "_score" : 6.5059485,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "136",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "345",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "472",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 472,
          "balance" : 25571,
          "firstname" : "Lee",
          "lastname" : "Long",
          "age" : 32,
          "gender" : "F",
          "address" : "288 Mill Street",
          "employer" : "Comverges",
          "email" : "leelong@comverges.com",
          "city" : "Movico",
          "state" : "MT"
        }
      }
    ]
  }
}

```

### term 精确匹配

非 text 字段使用 term

```java
GET bank/_search
{
  "query": {
    "term": {
      "account_number": {
        "value": "1"
      }
    }
  }
}
```

```java
{
  "took" : 3,
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
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 1,
          "balance" : 39225,
          "firstname" : "Amber",
          "lastname" : "Duke",
          "age" : 32,
          "gender" : "M",
          "address" : "880 Holmes Lane",
          "employer" : "Pyrami",
          "email" : "amberduke@pyrami.com",
          "city" : "Brogan",
          "state" : "IL"
        }
      }
    ]
  }
}
```

match 中的 text字段 使用 FIELD.keyword 与 term 的效果一致，会精确匹配

```java
GET bank/_search
{
  "query": {
    "match": {
      "address.keyword": "789 Madison"
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
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  }
}
```

## 复合查询

`bool`: 布尔查询有一个或者多个布尔子句组成

- `must`: 文档必须符合must中所有的条件，会影响相关性得分
- `must_not`: 文档必须不符合must_not 中的所有条件
- `should`: 文档可以符合should中的条件，会影响相关性得分
- `filter`: 只过滤符合条件的文档，不影响相关性得分

```java
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "gender": "M"
          }
        },
        {
          "match": {
            "address": "lane"
          }
        }
      ],
      "must_not": [
        {
          "range": {
            "age": {
              "gte": 10,
              "lte": 20
            }
          }
        }
      ],
      "should": [
        {
          "match": {
            "firstname": "Tammi"
          }
        }
      ],
      "filter": [
        {
          "range": {
            "account_number": {
              "gte": 900,
              "lte": 1000
            }
          }
        }
      ]
    }
  }
}
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
    "max_score" : 4.783532,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "908",
        "_score" : 4.783532,
        "_source" : {
          "account_number" : 908,
          "balance" : 45975,
          "firstname" : "Mosley",
          "lastname" : "Holloway",
          "age" : 31,
          "gender" : "M",
          "address" : "929 Eldert Lane",
          "employer" : "Anivet",
          "email" : "mosleyholloway@anivet.com",
          "city" : "Biehle",
          "state" : "MS"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "934",
        "_score" : 4.783532,
        "_source" : {
          "account_number" : 934,
          "balance" : 43987,
          "firstname" : "Freida",
          "lastname" : "Daniels",
          "age" : 34,
          "gender" : "M",
          "address" : "448 Cove Lane",
          "employer" : "Vurbo",
          "email" : "freidadaniels@vurbo.com",
          "city" : "Snelling",
          "state" : "NJ"
        }
      },
      {
        "_index" : "bank",
        "_type" : "_doc",
        "_id" : "921",
        "_score" : 4.783532,
        "_source" : {
          "account_number" : 921,
          "balance" : 49119,
          "firstname" : "Barbara",
          "lastname" : "Wade",
          "age" : 29,
          "gender" : "M",
          "address" : "687 Hoyts Lane",
          "employer" : "Roughies",
          "email" : "barbarawade@roughies.com",
          "city" : "Sattley",
          "state" : "CO"
        }
      }
    ]
  }
}


```

## 聚合查询

搜索 address 中包含 mill 的所有人的年龄分布和平均年龄

```java
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address": "mill"
    }
  },
  "aggs": {
    "age_agg": {
      "terms": {  // terms 多值聚合，按照字段的值来动态构建桶
        "field": "age"
      }
    },
    "age_avg": {
      "avg": {
        "field": "age"
      }
    },
    "balance_stats": {
      "stats": {
        "field": "balance"
      }
    }
  },
  "size": 0
}
```

```java
{
  "took" : 14,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "balance_avg" : {
      "count" : 4,
      "min" : 9812.0,
      "max" : 45801.0,
      "avg" : 25208.0,
      "sum" : 100832.0
    },
    "age_avg" : {
      "value" : 34.0
    },
    "age_agg" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : 38,
          "doc_count" : 2
        },
        {
          "key" : 28,
          "doc_count" : 1
        },
        {
          "key" : 32,
          "doc_count" : 1
        }
      ]
    }
  }
}
```

按照年龄聚合，并且请求这些年龄段的这些人的平均薪资

```java
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "size": 0, 
  "aggs": {
    "age_agg": {
      "terms": {
        "field": "age",
        "size": 5
      },
      "aggs": {
        "balance_avg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

```java
{
  "took" : 5,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "age_agg" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 716,
      "buckets" : [
        {
          "key" : 31,
          "doc_count" : 61,
          "balance_avg" : {
            "value" : 28312.918032786885
          }
        },
        {
          "key" : 39,
          "doc_count" : 60,
          "balance_avg" : {
            "value" : 25269.583333333332
          }
        },
        {
          "key" : 26,
          "doc_count" : 59,
          "balance_avg" : {
            "value" : 23194.813559322032
          }
        },
        {
          "key" : 32,
          "doc_count" : 52,
          "balance_avg" : {
            "value" : 23951.346153846152
          }
        },
        {
          "key" : 35,
          "doc_count" : 52,
          "balance_avg" : {
            "value" : 22136.69230769231
          }
        }
      ]
    }
  }
}

```

查出所有年龄分布，并且这些年龄段中 M 的平均薪资和 F 的平均薪资以及这个年龄段的总体年龄薪资

```java
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "size": 0, 
  "aggs": {
    "age_terms": {
      "terms": {
        "field": "age",
        "size": 3
      },
      "aggs": {
        "gender_terms": {
          "terms": {
            "field": "gender.keyword"
          },
          "aggs": {
            "balance_avg": {
              "avg": {
                "field": "balance"
              }
            }
          }
        },
        "balance_avg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

```java
{
  "took" : 14,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "age_terms" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 820,
      "buckets" : [
        {
          "key" : 31,
          "doc_count" : 61,
          "gender_terms" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "M",
                "doc_count" : 35,
                "balance_avg" : {
                  "value" : 29565.628571428573
                }
              },
              {
                "key" : "F",
                "doc_count" : 26,
                "balance_avg" : {
                  "value" : 26626.576923076922
                }
              }
            ]
          },
          "balance_avg" : {
            "value" : 28312.918032786885
          }
        },
        {
          "key" : 39,
          "doc_count" : 60,
          "gender_terms" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "F",
                "doc_count" : 38,
                "balance_avg" : {
                  "value" : 26348.684210526317
                }
              },
              {
                "key" : "M",
                "doc_count" : 22,
                "balance_avg" : {
                  "value" : 23405.68181818182
                }
              }
            ]
          },
          "balance_avg" : {
            "value" : 25269.583333333332
          }
        },
        {
          "key" : 26,
          "doc_count" : 59,
          "gender_terms" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : "M",
                "doc_count" : 32,
                "balance_avg" : {
                  "value" : 25094.78125
                }
              },
              {
                "key" : "F",
                "doc_count" : 27,
                "balance_avg" : {
                  "value" : 20943.0
                }
              }
            ]
          },
          "balance_avg" : {
            "value" : 23194.813559322032
          }
        }
      ]
    }
  }
}

```

## 映射关系

### 创建映射关系

```java
PUT newbank/_mapping
{
  "properties" : {
        "account_number" : {
          "type" : "long"
        },
        "address" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "age" : {
          "type" : "integer"
        },
        "balance" : {
          "type" : "long"
        },
        "city" : {
          "type" : "keyword"
        },
        "email" : {
          "type" : "keyword"
        },
        "employer" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "firstname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "gender" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "lastname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "state" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
}
```

### 获取映射关系

```java
GET newbank/_mapping
```

### 更新映射，数据迁移

```java
POST _reindex
{
  "source": {
    "index": "bank"
  },
  "dest": {
    "index": "newbank"
  }
}

// 删除原索引
DELETE product

// 设置别名
POST /_aliases
{
  "actions": [
    {
      "add": {
        "index": "product_",
        "alias": "product"
      }
    }
  ]
}
```
