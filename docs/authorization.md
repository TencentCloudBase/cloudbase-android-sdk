# 授权登录

为了保证开发者资源的安全性，在调用 TCB 服务时需要进行登录授权。

## 微信授权

目前 Android SDK 支持微信开放平台授权，不支持匿名访问。因此在初始化资源后请立即调用登录接口做登录授权，登录成功前其它的数据请求将不能成功发出。

微信登录成功后，相关的授权信息会存储在 SharedPreference 中做持久化处理。

### 微信登录配置流程

#### 1. 申请微信开放平台 APP

你需要到[微信开放平台](https://open.weixin.qq.com/) 申请接入移动应用，获取 AppID 和 AppSecret，并获取微信登录接口能力。

#### 2. 云开发绑定微信开放平台

获取移动应用的 AppID 和 AppSecret 之后，你需要到[云开发 Web 控制台](https://console.cloud.tencent.com/tcb/user) => 用户管理 => 登录设置中绑定微信开放平台。

#### 3. 添加微信登录 SDK

在你的 Android 项目的 build.gradle 文件中，添加如下依赖：

```
dependencies {
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.4.3'
}
```

#### 4. 新建 WXEntryActivity 类

调用微信登录后，SDK 会唤起微信，用户同意后登录授权后，微信会回调授权信息给对应的应用，为了能接收微信的返回值，需要遵守微信的 SDK 接入规范，按如下说明进行：

在你的包名相应目录下新建一个 wxapi 目录，并在该 wxapi 目录下新增一个 WXEntryActivity 类，该类继承自 Activity，WXEntryActivity 类的内容如下：

```java
package com.tencent.tcb.demo.wxapi;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private WeixinAuth weixinAuth = null;
    public String envName = "test-a8e99b";
    // 请使用微信开放平台移动应用 appId
    // 并在云开发 Web 控制台：用户管理/登陆设置中绑定你的 AppID 和 AppSecret
    public String appId = "wx9c4cxxxxx";
    public String domain = "https://youdomain.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化配置
        Config config = new Config(envName, appId, domain);
        weixinAuth = WeixinAuth.getInstance(this, config);
        IWXAPI wxAPI = weixinAuth.getWxAPI();

        // 处理微信回调数据
        wxAPI.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    // app 发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                weixinAuth.loginListener.onFailed(
                        new TcbException("ERR_AUTH_DENIED", "user auth denied")
                );
                break;
            case BaseResp.ErrCode.ERR_OK:
                final String code = ((SendAuth.Resp) resp).code;
                if (code != null) {
                    // 获取微信返回的 code，调用 callback 接口，换取 accessToken
                    // 注意网络请求线程安全
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            weixinAuth.callback(code);
                        }
                    }).start();
                }
            default:
                weixinAuth.loginListener.onFailed(
                        new TcbException("ERR_AUTH_DENIED", "user auth denied")
                );
        }

        // 关闭空白页面
        finish();
    }
}
```

并在 manifest 文件里面加上 exported 属性，设置为 true，例如：

```xml
<activity
    android:name=".wxapi.WXEntryActivity"
    android:exported="true">
</activity>
```

#### 5. 配置 SDK 权限

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### 6. 微信登录

最后，在需要唤起微信登录的地方调用 `weixinAuth.login` 方法。

```java
import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.auth.LoginListener;

String appId = "wx9c4c30a432a3xxxx"; // 微信开放平台 appId
String envName = "envid"; // 云开发环境 Id
String domain = "http://yourdomian.com";

// 初始化
Context context = this; // 安卓 Context
final Config config = new Config(envName, appId, domain);
WeixinAuth weixinAuth = WeixinAuth.getInstance(context, config);

private void weixinLogin() {
    // 注意网络请求线程安全
    new Thread(new Runnable() {
        @Override
        public void run() {
            final String LogTag = "WeixinLogin";
            // 调用微信登录
            weixinAuth.login(new LoginListener() {
                @Override
                public void onSuccess() {
                    // 登录成功
                }
                @Override
                public void onFailed(TcbException e) {
                    // 登录失败
                }
            });
        }
    }).start();
}
```

### 微信登录兼容

如果你的应用已经接入了微信登录，请在 WXEntryActivity 类中初始化 weixinAuth 实例，并在获取登录的 code 之后调用 `weixinAuth.callback(code)` 方法。
