# Tencent Cloud Base(TCB) Android SDK

## 目录

- [介绍](#介绍)
- [安装](#安装)
- [文档](#文档)

## 介绍

TCB 提供开发应用所需服务和基础设施。TCB Android SDK 让你可以在 Android APP 中访问 TCB 的服务。

## 安装

通过 maven 进行安装。

## 快速上手

### 登录授权

为了保证开发者资源的安全性，在使用 TCB Android SDK 服务时需要进行登录授权。目前 TCB Android SDK 仅支持通过微信登录授权，微信登录计入详情参见文档：[登录授权](docs/authorization.md)

### 使用

```java
import com.tencent.tcb.TCB;

TCB tcb = new TCB("envName", context);

// 调用云函数
tcb.function.callFunction("test");

// 上传文件
tcb.storage.uploadFile("fileName", "path", new TcbStorageListener() {
    @Override
    public void onProgress(int progress) {
    }
    @Override
    public void onSuccess(JSONObject result) {
    }
    @Override
    public void onFailed(TcbException e) {
    }
});

// 获取数据库记录
tcb.db.collection("user").doc("docId").get();
```

除了上面通过 TCB 类使用全部服务以外，TCB Android SDK 还支持引入独立的模块，单独使用，如：

```java
import com.tencent.tcb.function.FunctionService;

FunctionService functionService = new FunctionService("envName", context);
JSONObject res = functionService.callFunction("test", data);
```

## 文档

- [登录授权](docs/authorization.md)
- [存储](docs/storage.md)
- [数据库](docs/database.md)
- [云函数](docs/function.md)

## 更新日志

查看[更新日志](./changelog.md)

