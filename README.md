# Tencent CloudBase(TCB) Android SDK

- [介绍](#%e4%bb%8b%e7%bb%8d)
- [安装](#%e5%ae%89%e8%a3%85)
- [快速上手](#%e5%bf%ab%e9%80%9f%e4%b8%8a%e6%89%8b)
  - [登录授权](#%e7%99%bb%e5%bd%95%e6%8e%88%e6%9d%83)
  - [使用](#%e4%bd%bf%e7%94%a8)
- [文档](#%e6%96%87%e6%a1%a3)
- [更新日志](#%e6%9b%b4%e6%96%b0%e6%97%a5%e5%bf%97)

## 介绍

CloudBase 提供开发应用所需服务和基础设施。TCB Android SDK 让您可以在 Android APP 中访问 TCB 的服务。

## 安装

1. 在您的项目根目录下的 build.gradle 文件中添加 maven 仓库

```
allprojects {
  repositories {
    ...
    // 添加 maven
    maven {
        url "https://dl.bintray.com/tencentcloudbase/maven"
    }
  }
}
```

2. 在应用的根目录下的 build.gradle 中添加依赖

```
dependencies {
  ...
  // 增加这行
  implementation 'com.tencent.tcb:cloudbase-android-sdk:1.0.0'
}
```

## 快速上手

### 登录授权

为了保证开发者资源的安全性，在使用 TCB Android SDK 服务时需要进行登录授权。目前 Android SDK 支持微信开放平台授权以及自定义登录，不支持匿名访问。因此在初始化资源后请立即调用登录接口做登录授权，登录成功前其它的数据请求将不能成功发出，详情参见文档：[登录授权](docs/authorization.md)

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
