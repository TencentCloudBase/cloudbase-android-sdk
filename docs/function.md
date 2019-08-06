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

通过 TCB 类使用：

```java
// envName 为环境 Id
import com.tencent.tcb.TCB;

TCB tcb = new TCB("envName", context);

try {
    JSONObject data = new JSONObject();
    data.put("key", "test");
    JSONObject res = tcb.function.callFunction("test", data);
} catch (TcbException e) {
    fail(e.toString());
}
```

通过 FunctionService 类使用：

```java
import com.tencent.tcb.function.FunctionService;

// envName 为环境 Id
FunctionService functionService = new FunctionService("envName", context);

try {
    JSONObject data = new JSONObject();
    data.put("key", "test");
    JSONObject res = functionService.callFunction("test", data);
    String requestId = res.getString("requestId");
    JSONObject result = res.getJSONObject("result");
} catch (TcbException e) {
    fail(e.toString());
}
```
