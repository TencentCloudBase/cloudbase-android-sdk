# 云函数

## 执行函数

`public JSONObject callFunction(String name, JSONObject data)`

请求参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | ---
| name | string | 是 | 云函数名称
| data | object | 否 | 云函数参数

响应参数

| 字段 | 类型 | 必填 | 说明
| --- | --- | --- | ---
| code | string | 否 | 状态码，操作成功则不返回
| message | string | 否 | 错误描述
| result | object | 否 | 云函数执行结果
| requestId | string | 否 | 请求序列号，用于错误排查

示例代码

```java
FunctionService functionService = new FunctionService(config, context);
try {
    JSONObject data = new JSONObject("{\"key\":\"test\"}");
    JSONObject res = functionService.callFunction("test", data);
    String requestId = res.getString("requestId");
    JSONObject result = res.getJSONObject("result");
} catch (TcbException e) {
    fail(e.toString());
}
```
