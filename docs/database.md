# 数据库

- [简单用法](#%e7%ae%80%e5%8d%95%e7%94%a8%e6%b3%95)
- [获取数据库的引用](#%e8%8e%b7%e5%8f%96%e6%95%b0%e6%8d%ae%e5%ba%93%e7%9a%84%e5%bc%95%e7%94%a8)
- [获取集合的引用](#%e8%8e%b7%e5%8f%96%e9%9b%86%e5%90%88%e7%9a%84%e5%bc%95%e7%94%a8)
  - [集合 Collection](#%e9%9b%86%e5%90%88-collection)
  - [记录 Record / Document](#%e8%ae%b0%e5%bd%95-record--document)
  - [查询筛选指令 Query Command](#%e6%9f%a5%e8%af%a2%e7%ad%9b%e9%80%89%e6%8c%87%e4%bb%a4-query-command)
  - [字段更新指令 Update Command](#%e5%ad%97%e6%ae%b5%e6%9b%b4%e6%96%b0%e6%8c%87%e4%bb%a4-update-command)
- [支持的数据类型](#%e6%94%af%e6%8c%81%e7%9a%84%e6%95%b0%e6%8d%ae%e7%b1%bb%e5%9e%8b)
- [新增文档](#%e6%96%b0%e5%a2%9e%e6%96%87%e6%a1%a3)
- [查询文档](#%e6%9f%a5%e8%af%a2%e6%96%87%e6%a1%a3)
  - [添加查询条件](#%e6%b7%bb%e5%8a%a0%e6%9f%a5%e8%af%a2%e6%9d%a1%e4%bb%b6)
  - [获取查询数量](#%e8%8e%b7%e5%8f%96%e6%9f%a5%e8%af%a2%e6%95%b0%e9%87%8f)
  - [设置获取文档数量](#%e8%ae%be%e7%bd%ae%e8%8e%b7%e5%8f%96%e6%96%87%e6%a1%a3%e6%95%b0%e9%87%8f)
  - [设置起始位置](#%e8%ae%be%e7%bd%ae%e8%b5%b7%e5%a7%8b%e4%bd%8d%e7%bd%ae)
  - [对结果排序](#%e5%af%b9%e7%bb%93%e6%9e%9c%e6%8e%92%e5%ba%8f)
  - [指定返回字段](#%e6%8c%87%e5%ae%9a%e8%bf%94%e5%9b%9e%e5%ad%97%e6%ae%b5)
  - [查询指令](#%e6%9f%a5%e8%af%a2%e6%8c%87%e4%bb%a4)
    - [eq](#eq)
    - [neq](#neq)
    - [gt](#gt)
    - [gte](#gte)
    - [lt](#lt)
    - [lte](#lte)
    - [in](#in)
    - [nin](#nin)
    - [and](#and)
    - [or](#or)
  - [正则表达式查询](#%e6%ad%a3%e5%88%99%e8%a1%a8%e8%be%be%e5%bc%8f%e6%9f%a5%e8%af%a2)
    - [db.regExp](#dbregexp)
- [删除文档](#%e5%88%a0%e9%99%a4%e6%96%87%e6%a1%a3)
- [更新文档](#%e6%9b%b4%e6%96%b0%e6%96%87%e6%a1%a3)
  - [更新指定文档](#%e6%9b%b4%e6%96%b0%e6%8c%87%e5%ae%9a%e6%96%87%e6%a1%a3)
  - [更新文档，如果不存在则创建](#%e6%9b%b4%e6%96%b0%e6%96%87%e6%a1%a3%e5%a6%82%e6%9e%9c%e4%b8%8d%e5%ad%98%e5%9c%a8%e5%88%99%e5%88%9b%e5%bb%ba)
  - [批量更新文档](#%e6%89%b9%e9%87%8f%e6%9b%b4%e6%96%b0%e6%96%87%e6%a1%a3)
  - [更新指令](#%e6%9b%b4%e6%96%b0%e6%8c%87%e4%bb%a4)
    - [set](#set)
    - [inc](#inc)
    - [mul](#mul)
    - [remove](#remove)
    - [push](#push)
    - [pop](#pop)
    - [unshift](#unshift)
    - [shift](#shift)
- [GEO 地理位置](#geo-%e5%9c%b0%e7%90%86%e4%bd%8d%e7%bd%ae)
  - [GEO 数据类型](#geo-%e6%95%b0%e6%8d%ae%e7%b1%bb%e5%9e%8b)
    - [Point](#point)
    - [LineString](#linestring)
    - [Polygon](#polygon)
    - [MultiPoint](#multipoint)
    - [MultiLineString](#multilinestring)
    - [MultiPolygon](#multipolygon)
  - [GEO 操作符](#geo-%e6%93%8d%e4%bd%9c%e7%ac%a6)
    - [geoNear](#geonear)
    - [geoWithin](#geowithin)
    - [geoIntersects](#geointersects)

## 简单用法

下面所有的方法均已挂载到 TCB 类，可以直接使用，如：

```java
import com.tencent.tcb.TCB;

TCB tcb = new TCB("envName", context);
tcb.db.collection("user").doc("docId").get();
```

等价于

```java
import com.tencent.tcb.database.Db;

Db db = new Db("envName", context);
db.collection("user").doc("docId").get();
```

## 获取数据库的引用

```java
Context context = this;
Db db = new Db("envName", context);
```

## 获取集合的引用

```java
// 获取 `user` 集合的引用
Collection collection = db.collection("user");
```

### 集合 Collection

通过 `db.collection(name)` 可以获取指定集合的引用，在集合上可以进行以下操作

| 类型     | 接口    | 说明                                                                               |
| -------- | ------- | ---------------------------------------------------------------------------------- |
| 写       | add     | 新增记录（触发请求）                                                               |
| 计数     | count   | 获取复合条件的记录条数                                                             |
| 读       | get     | 获取集合中的记录，如果有使用 where 语句定义查询条件，则会返回匹配结果集 (触发请求) |
| 引用     | doc     | 获取对该集合中指定 id 的记录的引用                                                 |
| 查询条件 | where   | 通过指定条件筛选出匹配的记录，可搭配查询指令（eq, gt, in, ...）使用                |
|          | skip    | 跳过指定数量的文档，常用于分页，传入 offset                                        |
|          | orderBy | 排序方式                                                                           |
|          | limit   | 返回的结果集 (文档数量) 的限制，有默认值和上限值                                     |
|          | field   | 指定需要返回的字段                                                                 |


查询及更新指令用于在 `where` 中指定字段需满足的条件，指令可通过 `db.command` 对象取得。


### 记录 Record / Document

通过 `db.collection(collectionName).doc(docId)` 可以获取指定集合上指定 id 的记录的引用，在记录上可以进行以下操作

| 接口 | 说明   |
| ---- | ------ |
| 写   | set    | 覆写记录               |
|      | update | 局部更新记录(触发请求) |
|      | remove | 删除记录(触发请求)     |
| 读   | get    | 获取记录(触发请求)     |


### 查询筛选指令 Query Command

以下指令挂载在 `db.command` 下

| 类型     | 接口 | 说明                               |
| -------- | ---- | ---------------------------------- |
| 比较运算 | eq   | 字段 ==                            |
|          | neq  | 字段 !=                            |
|          | gt   | 字段 >                             |
|          | gte  | 字段 >=                            |
|          | lt   | 字段 <                             |
|          | lte  | 字段 <=                            |
|          | in   | 字段值在数组里                     |
|          | nin  | 字段值不在数组里                   |
| 逻辑运算 | and  | 表示需同时满足指定的所有条件       |
|          | or   | 表示需同时满足指定条件中的至少一个 |


### 字段更新指令 Update Command

以下指令挂载在 `db.command` 下

| 类型 | 接口    | 说明                             |
| ---- | ------- | -------------------------------- |
| 字段 | set     | 设置字段值                       |
|      | remove  | 删除字段                         |
|      | inc     | 加一个数值，原子自增             |
|      | mul     | 乘一个数值，原子自乘             |
|      | push    | 数组类型字段追加尾元素，支持数组 |
|      | pop     | 数组类型字段删除尾元素，支持数组 |
|      | shift   | 数组类型字段删除头元素，支持数组 |
|      | unshift | 数组类型字段追加头元素，支持数组 |


## 支持的数据类型

数据库提供以下几种数据类型：
* String：字符串
* Number：数字
* Object：对象
* Array：数组
* Bool：布尔值
* GeoPoint：地理位置点
* GeoLineStringL: 地理路径
* GeoPolygon: 地理多边形
* GeoMultiPoint: 多个地理位置点
* GeoMultiLineString: 多个地理路径
* GeoMultiPolygon: 多个地理多边形
* Date：时间
* Null

以下对几个特殊的数据类型做个补充说明

1. 时间 Date

   Date 类型用于表示时间，精确到毫秒，可以用 JavaScript 内置 Date 对象创建。需要特别注意的是，用此方法创建的时间是客户端时间，不是服务端时间。如果需要使用服务端时间，应该用 API 中提供的 serverDate 对象来创建一个服务端当前时间的标记，当使用了 serverDate 对象的请求抵达服务端处理时，该字段会被转换成服务端当前的时间，更棒的是，我们在构造 serverDate 对象时还可通过传入一个有 offset 字段的对象来标记一个与当前服务端时间偏移 offset 毫秒的时间，这样我们就可以达到比如如下效果：指定一个字段为服务端时间往后一个小时。

   那么当我们需要使用客户端时间时，存放 Date 对象和存放毫秒数是否是一样的效果呢？不是的，我们的数据库有针对日期类型的优化，建议大家使用时都用 Date 或 serverDate 构造时间对象。

   ```java
   // 服务端当前时间
   ServerDate date = db.serverDate()
   ```

   ```java
   // 服务端当前时间加 1S
   ServerDate date = db.serverDate(1000)
   ```

2. 地理位置

   参考：[GEO 地理位置](#GEO 地理位置)

3. Null

   Null 相当于一个占位符，表示一个字段存在但是值为空。

## 新增文档

`JSONObject collection.add(JSONObject data)`

```java
JSONObject data = new JSONObject();
data.put("name", "xxxx");
JSONObject result = db.collection("user").add(data);
```

响应参数

| 参数 | 类型   | 说明                                     |
| ---- | ------ | ---------------------------------------- |
| requestId | String |  请求序列号 |
| data | JSONObject | `{ "id": "xxxxxx" } ` 包含创建文档 id 的 JSON 数据 |

## 查询文档

支持 `where()`、`limit()`、`skip()`、`orderBy()`、`get()`、`update()`、`field()`、`count()` 等操作。

只有当调用 `get()` `update()` 时才会真正发送请求。
注：默认取前 100 条数据，最大取前 100 条数据。

响应参数

| 参数 | 类型   | 说明                                     |
| ---- | ------ | ---------------------------------------- |
| requestId | String |  请求序列号 |
| data | JSONArray | 符合查询条件的文档数组 |

### 添加查询条件

`JSONObject collection.where(JSONObject data)`

参数

设置过滤条件
where 可接收对象作为参数，表示筛选出拥有和传入对象相同的 key-value 的文档。比如筛选出所有类型为计算机的、内存为 8g 的商品：

```java
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("type", new JSONObject("{\"memory\":8}"));
db.collection("goods").where(data).get();
```

如果要表达更复杂的查询，可使用高级查询指令，比如筛选出所有内存大于 8g 的计算机商品：

```java
Command cmd = db.command; // 取指令
JSONObject type = new JSONObject();
type.put("memory", cmd.gt(8)); // 内存大于 8g
JSONObject data = new JSONObject();
data.put("type", type);
db.collection("goods").where(data).get();
```

### 获取查询数量

`collection.count()`

参数
```java
// promise
JSONObject data = new JSONObject();
db.collection("goods").where(data).count();
```

响应参数

| 字段      | 类型    | 必填 | 说明                     |
| --------- | ------- | ---- | ------------------------ |
| code      | String  | 否   | 状态码，操作成功则不返回 |
| message   | String  | 否   | 错误描述                 |
| total     | Integer | 否   | 计数结果                 |
| requestId | String  | 否   | 请求序列号，用于错误排查 |

### 设置获取文档数量

`collection.limit(int num)`

参数说明

| 参数  | 类型    | 必填 | 说明           |
| ----- | ------- | ---- | -------------- |
| value | Integer | 是   | 限制展示的数值 |

使用示例

```java
// 从集合中取
collection.limit(10).get();
// 从查询条件中取
collection.where(data).limit(10).get();
```

### 设置起始位置

`collection.skip(int num)`

参数说明

| 参数  | 类型    | 必填 | 说明           |
| ----- | ------- | ---- | -------------- |
| value | Integer | 是   | 跳过数据的条数 |

使用示例

```java
collection.skip(4).get();
```

### 对结果排序

`collection.orderBy(String fieldPath, String directionStr)`

参数说明

| 参数      | 类型   | 必填 | 说明                                |
| --------- | ------ | ---- | ----------------------------------- |
| fieldPath    | String | 是   | 排序的字段                          |
| directionStr | String | 是   | 排序的顺序，`升序(asc)`或`降序(desc)` |

使用示例

```java
collection.orderBy("name", "asc").get();
```

### 指定返回字段

`collection.field(HashMap<String, Boolean> projection)`

参数说明

| 参数 | 类型   | 必填 | 说明                                    |
| ---- | ------ | ---- | --------------------------------------- |
| projection | HashMap | 是  | 要过滤的字段，不返回传 false，返回传 true |

使用示例

```java
HashMap<String, Boolean> field = new HashMap<>();
field.put("age", true);
collection.field(field).get();
```

**备注：只能指定要返回的字段或者不要返回的字段。即 `{ "a": true, "b": false }` 是一种错误的参数格式。**

### 查询指令

#### eq

表示字段等于某个值。`eq` 指令接受一个字面量 (literal)，可以是 `Number(int, long, float...)`, `Boolean`, `String`, `Object`, `Array`。

比如筛选出所有自己发表的文章，除了用传对象的方式：

```java
JSONObject data = new JSONObject();
data.put("cmdopenid", "xxx");
db.collection("articles").where(data).get();
```

还可以用指令：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("cmdopenid", cmd.eq("xxx"));
db.collection("articles").where(data).get();
```

注意 `eq` 指令比对象的方式有更大的灵活性，可以用于表示字段等于某个对象的情况，比如：

```java
// 这种写法表示匹配 stat.publishYear == 2018 且 stat.language == "zh-CN"
JSONObject data = new JSONObject();
JSONObject stat = new JSONObject();
stat.put("publishYear", 2019);
stat.put("language", "zh-CN");
data.put("stat", stat);
db.collection("articles").where(data).get();

// 这种写法表示 stat 对象等于 { publishYear: 2018, language: "zh-CN" }
JSONObject data = new JSONObject();
JSONObject stat = new JSONObject();
stat.put("publishYear", 2019);
stat.put("language", "zh-CN");
data.put("stat", cmd.eq(stat));
Command cmd = db.command
db.collection("articles").where(data).get();
```

#### neq

字段不等于。`neq` 指令接受一个字面量 (literal)，可以是 `Number(int, long, float...)`, `Boolean`, `String`, `Object`, `Array`。

如筛选出品牌不为 X 的计算机：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("type", cmd.neq("X"));
db.collection("goods").where(data).get();
```

#### gt

字段大于指定值。

如筛选出价格大于 2000 的计算机：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("price", cmd.gt(2000));
db.collection("goods").where(data).get();
```

#### gte

字段大于或等于指定值。

#### lt

字段小于指定值。

#### lte

字段小于或等于指定值。

#### in

字段值在给定的数组中。

筛选出内存为 8g 或 16g 的计算机商品：

```java
Command cmd = db.command;
ArrayList<String> memory = new ArrayList<>();
memory.add(8);
memory.add(16);
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("memory", cmd.in(memory));
db.collection("goods").where(data).get();
```

#### nin

字段值不在给定的数组中。

筛选出内存不是 8g 或 16g 的计算机商品：

```java
Command cmd = db.command;
ArrayList<String> memory = new ArrayList<>();
memory.add(8);
memory.add(16);
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("memory", cmd.nin(memory));
db.collection("goods").where(data).get();
```

#### and

表示需同时满足指定的两个或以上的条件。

如筛选出内存大于 4g 小于 32g 的计算机商品：

流式写法：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("memory", cmd.gt(4).and(cmd.lt(32)));
db.collection("goods").where(data).get();
```

前置写法：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("memory", cmd.and(cmd.gt(4), cmd.lt(32)));
db.collection("goods").where(data).get();
```

#### or

表示需满足所有指定条件中的至少一个。如筛选出价格小于 4000 或在 6000-8000 之间的计算机：

流式写法：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("price", cmd.lt(4000).or(cmd.gt(6000).and(cmd.lt(8000))));
db.collection("goods").where(data).get();
```

前置写法：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("category", "computer");
data.put("price",  cmd.or(cmd.lt(4000), cmd.and(cmd.gt(6000), cmd.lt(8000))));
db.collection("goods").where(data).get();
```

如果要跨字段 “或” 操作：(如筛选出内存 8g 或 cpu 3.2 ghz 的计算机)

```java
Command cmd = db.command;
JSONObject cpu = new JSONObject();
cpu.put("type.cpu", 3.2);
JSONObject memory = new JSONObject();
memory.put("type.memory", cmd.gt(8));
db.collection("goods").where(cmd.or(
    cpu,
    memory
)).get();
```

### 正则表达式查询

#### db.regExp

根据正则表达式进行筛选

例如下面可以筛选出 `version` 字段开头是 "数字 + s" 的记录，并且忽略大小写：

```java
JSONObject query = new JSONObject();
RegExp regExp = db.regExp("^\\ds", "i");
query.put("version", regExp);
db.collection("articles").where(query).get();
```

## 删除文档

方式 1 通过指定文档 ID

`collection.doc(_id).remove()`

```java
collection.doc("xxxx").remove();
```

方式 2 条件查找文档然后直接批量删除

`collection.where().remove()`

```java
// 删除字段 a 的值大于 2 的文档
Command cmd = db.command;
JSONObject query = new JSONObject();
query.put("a", cmd.gt(2));
collection.where(query).remove();
```

## 更新文档

### 更新指定文档

`collection.doc().update()`

```java
JSONObject data = new JSONObject();
data.put("name", "hey");
collection.doc("doc-id").update(data);
```

### 更新文档，如果不存在则创建

`collection.doc().set()`

```java
JSONObject data = new JSONObject();
data.put("name", "hey");
collection.doc("doc-id").set(data);
```

### 批量更新文档

`collection.update()`

```java
JSONObject query = new JSONObject();
query.put("name", "hey");
JSONObject data = new JSONObject();
data.put("age", 18);
collection.where(query).update(data);
```

### 更新指令

#### set

更新指令。用于设定字段等于指定值。这种方法相比传入纯 JS 对象的好处是能够指定字段等于一个对象：

```java
// 以下方法只会更新 style.color 为 red，而不是将 style 更新为 { color: 'red' }，即不影响 style 中的其他字段
JSONObject data = new JSONObject();
data.put("data.style.color", "red");
collection.doc("doc-id").update(data);

// 以下方法更新 style 为 { color: 'red', size: 'large' }
Command cmd = db.command;
JSONObject data = new JSONObject();
JSONObject updateData = new JSONObject();
updateData.put("color", "red");
updateData.put("size", "large");
data.put("data.style", cmd.set(updateData));
collection.doc("doc-id").update(data);
```

#### inc

更新指令。用于指示字段自增某个值，这是个原子操作，使用这个操作指令而不是先读数据、再加、再写回的好处是：

1. 原子性：多个用户同时写，对数据库来说都是将字段加一，不会有后来者覆写前者的情况
2. 减少一次网络请求：不需先读再写

之后的 mul 指令同理。

如给收藏的商品数量加一：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("collect.count", cmd.inc(1));
db.collection("user").where(query).update(data);
```

#### mul

更新指令。用于指示字段自乘某个值。

#### remove

更新指令。用于表示删除某个字段。如某人删除了自己一条商品评价中的评分：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("rating", cmd.remove());
db.collection("comments").doc("comment-id").update(data);
```

#### push

向数组尾部追加元素，支持传入单个元素或数组

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
ArrayList<String> users = new ArrayList<>();
users.add("aaa");
users.add("bbb");
data.put("users", cmd.push(users));
db.collection("comments").doc("comment-id").update(data);
```

#### pop

删除数组尾部元素

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("users", cmd.pop());
db.collection("comments").doc("comment-id").update(data);
```

#### unshift

向数组头部添加元素，支持传入单个元素或数组。使用同 push

#### shift

删除数组头部元素。使用同 pop

## GEO 地理位置

注意：**如果需要对类型为地理位置的字段进行搜索，一定要建立地理位置索引**。

### GEO 数据类型

#### Point

用于表示地理位置点，用经纬度唯一标记一个点，这是一个特殊的数据存储类型。

签名：`Point(double longitude, double latitude)`

示例：

```java
Point point = db.geo.point(longitude, latitude);
```

#### LineString

用于表示地理路径，是由两个或者更多的 `Point` 组成的线段。

签名：`LineString(ArrayList<Point> points)`

示例：

```java
ArrayList<Point> points = new ArrayList<>();
points.add(db.geo.point(lngA, latA));
points.add(db.geo.point(lngB, latB));
LineString line = db.geo.lineString(points)
```

#### Polygon

用于表示地理上的一个多边形（有洞或无洞均可），它是由一个或多个 **闭环** `LineString` 组成的几何图形。

由一个环组成的 `Polygon` 是没有洞的多边形，由多个环组成的是有洞的多边形。对由多个环（`LineString`）组成的多边形（`Polygon`），第一个环是外环，所有其他环是内环（洞）。

签名：`ArrayList<LineString> lines`

示例：

```java
// 创建线
ArrayList<Point> points = new ArrayList<>();
points.add(db.geo.point(lngA, latA));
points.add(db.geo.point(lngB, latB));
LineStrign line1 = db.geo.lineString(points);
// 创建多边形
ArrayList<LineString> lines = new ArrayList<>();
lines.add(line1);
lines.add(line2);
lines.add(line3);
Polygon polygon = db.geo.polygon(lines);
```

#### MultiPoint

用于表示多个点 `Point` 的集合。

签名：`MultiPoint(ArrayList<Point> points)`

示例：

```java
ArrayList<Point> points = new ArrayList<>();
points.add(db.geo.point(lngA, latA));
points.add(db.geo.point(lngB, latB));
MultiPoint multiPoint = db.geo.multiPoint(points);
```

#### MultiLineString

用于表示多个地理路径 `LineString` 的集合。

签名：`MultiLineString(ArrayList<LineString> lines)`

示例：

```java
// 线
ArrayList<Point> points = new ArrayList<>();
points.add(db.geo.point(lngA, latA));
points.add(db.geo.point(lngB, latB));
LineStrign line1 = db.geo.lineString(points);

ArrayList<LineString> lines = new ArrayList<>();
lines.add(line1);

MultiLineString multiLineString = db.geo.multiLineString(lines)
```

#### MultiPolygon

用于表示多个地理多边形 `Polygon` 的集合。

签名：`MultiPolygon(ArrayList<Polygon> polygons)`

示例：

```java
ArrayList<Polygon> polygons = new ArrayList<>();
polygons.add(polygon);
MultiPolygon multiPolygon = db.geo.multiPolygon(polygons);
```

### GEO 操作符

#### geoNear

按从近到远的顺序，找出字段值在给定点的附近的记录。

签名：

```java
db.command.geoNear(Point point, Number maxDistance, Number minDistance)
```

示例：

```java
Command cmd = db.command;
JSONObject data = new JSONObject();
Point point = db.geo.point(lngA, latA);
data.put("location", db.command.geoNear(point, 1000, 0));
db.collection("user").where(query).get();
```

#### geoWithin

找出字段值在指定 Polygon / MultiPolygon 内的记录，无排序

签名：

```java
// geometry: Polygon | MultiPolygon // 地理位置
db.command.geoWithin(Object geometry);
```

示例：

```java
// 一个闭合的区域
Polygon area = db.geo.polygon(lines);

Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("location", db.command.geoWithin(area));
// 搜索 location 字段在这个区域中的 user
db.collection("user").where(data).get();
```

#### geoIntersects

找出字段值和给定的地理位置图形相交的记录

签名：

```java
// geometry: Point | LineString | MultiPoint | MultiLineString | Polygon | MultiPolygon // 地理位置
db.command.geoIntersects(Object geometry)
```

示例：

```java
ArrayList<Point> points = new ArrayList<>();
points.add(db.geo.point(lngA, latA));
points.add(db.geo.point(lngB, latB));
// 一条路径
LineString line = db.geo.lineString(points);

Command cmd = db.command;
JSONObject data = new JSONObject();
data.put("location", db.command.geoIntersects(line));
// 搜索 location 与这条路径相交的 user
db.collection("user").where(data).get();
```