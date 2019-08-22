# 授权登录

为了保证开发者资源的安全性，在调用 Cloudbase 服务时需要进行登录授权。

目前 Android SDK 支持微信开放平台授权以及自定义登录，不支持匿名访问。因此在初始化资源后请立即调用登录接口做登录授权，登录成功前其它的数据请求将不能成功发出。

登录成功后，相关的授权信息会存储在 SharedPreference 中做持久化处理。

## 微信授权

### 1. 申请微信开放平台 APP

你需要到[微信开放平台](https://open.weixin.qq.com/) 申请接入移动应用，获取 AppID 和 AppSecret，并获取微信登录接口能力。

### 2. 云开发绑定微信开放平台

获取移动应用的 AppID 和 AppSecret 之后，你需要到[云开发 Web 控制台](https://console.cloud.tencent.com/tcb/user) => 用户管理 => 登录设置中绑定微信开放平台。

### 3. 添加微信登录 SDK

在你的 Android 项目的 build.gradle 文件中，添加如下依赖：

```
dependencies {
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.4.3'
}
```

### 4. 新建 WXEntryActivity 类

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化配置
        Config config = new Config(envName, appId);
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
                            weixinAuth.loginWithCode(code);
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

### 5. 配置 SDK 权限

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

### 6. 微信登录

最后，在需要唤起微信登录的地方调用 `weixinAuth.login` 方法。

```java
import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.auth.LoginListener;

String appId = "wx9c4c30a432a3xxxx"; // 微信开放平台 appId
String envName = "envid"; // 云开发环境 Id

// 初始化
Context context = this; // 安卓 Context
final Config config = new Config(envName, appId);
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

## 自定义登录

CloudBase 允许开发者使用特定的登录凭据 Ticket 对用户进行身份认证。开发者可以使用服务端 SDK 来创建 Ticket，并且将 JWT 传入到 Android 应用内，然后调用接口登录获取登录授权。

### 1. 获取私钥文件

登录腾讯云云开发控制台，在[用户管理页面](https://console.cloud.tencent.com/tcb/user)中，单击【登录设置】，然后生成并下载私钥。

### 2. 服务端生成 Ticket

自定义登录使用了 [JWT](https://jwt.io/) 规范进行 Ticket 校验。

首先，使用 JWT 库创建一个 Claims(Payload) 包含以下信息的 JWT：

| 字段    | 说明             | 取值                                                |
| ------- | ---------------- | --------------------------------------------------- |
| alg     | 算法             | "RS256"                                             |
| env     | Cloudbase 环境名 | 对应的环境名                                        |
| iat     | Ticket 颁发时间  | 当前时间（Unix 时间戳对应的毫秒数）                 |
| exp     | Ticket 过期时间  | Ticket 过期的时间（Unix 时间戳对应的毫秒数）        |
| uid     | 自定义 uid       | 自定义的用户全局唯一 id （字母或数组组成，4~32 位） |
| refresh | 登录态刷新时间   | 毫秒数，上限为 1 小时（3600000 毫秒）               |
| expire  | 登录态过期时间   | Unix 时间戳对应的毫秒数                             |

JWT 的 Header 包含以下信息：

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

注意，字段顺序要严格相同。

最后，拼接出 Ticket："credentials.private_key_id" + "/@@/" + "JWT 生成的 Token"。

JWT 生成 Token 的例子：

```java
// 生成 token
private String jsonWebToken() {
    // Web 控制台生成的 private_key，需要去除 -----BEGIN RSA PRIVATE KEY----- 和 -----END RSA PRIVATE KEY-----
    String pKey = "MIIxxxxxxgQC4nqdaVKXHC9VEf8zEVJn7OlcFayjmgTJ+1Cb7JSarjL1amLxv\n" +
            "vJ8O0p5So7+/P+LHEPxmtksr4Q2UjkV/zAqE6+kBN8qfwWg3VEu/SCf6ieuIgvbc\n" +
            "6mePFZmylrTfc0NoN8zTnnR7q/HV2FpOKIlt8xvlfnO1tFFN2eUDIxGBVwIDAQAB\n" +
            "AoGAXi6Jn1pZa86MkLJFqs6h8vjTFe+R4O197heafzp3nMJigsoyLyphVjV6ERx6\n" +
            "ID2eGgF/UYKjnJHl1KgzqjtUDZXEJPpsIUzQBpuG1FQueopO+qDFmnwpltpn/SXM\n" +
            "jdfvQQYm3rZAsfL4RL5dHPiZ823LiGUWZ0LFliVl65TThOkCQQD1NJ2ejPjGfzXe\n" +
            "rPC5eAEbm3BtXY3wVPnrCdNKcjtI7l4d/i5w+6rKc7RfnoqpvJbpe4uLVuxyUk1v\n" +
            "NItTthzFAkEAwL9BpnqolqskBu0GeiUVkJMOUs7YUvqKDFU80NR/HdXFYgHB5/kS\n" +
            "Jw5LE1LnmcrDwksG24+MViaY3q28DVM/awJBAMFRkzcOY5BzeLAvXraK0yzF1tSS\n" +
            "nrYs+MCChY+7EdyE+bTh0hGHiPaGVF3Sq/X4Vm6L1c+sX0wecShMn9AG0xUCQQCQ\n" +
            "X3HMQjn/SUeeDHJ6kUZ62Tu0WQz98n3uyPXZsiFY9qN3Sru0hwLK0FD5s3KY5qEE\n" +
            "6m/Di91hNl3xBY9DJ+TrAkEAryjws8ncAVVEIayL2WjfFOhirwL9GcVWRqy8WuUJ\n" +
            "WvgSx9ZGPsecDAzSXnuWUteAygJ+kl8Iltp2kEWuzerv2g==";
    try {
        long now = new Date().getTime();
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("alg", "RS256");
        jsonHeader.put("typ", "JWT");
        String header = jsonHeader.toString();
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("alg", "RS256");
        jsonPayload.put("env", "dev-97eb6c"); // 环境 Id
        jsonPayload.put("iat", now); // 签名时间
        jsonPayload.put("exp", now + 10 * 60 * 1000); // Ticket 过期的时间
        jsonPayload.put("uid", "1024"); // 用户 uin
        jsonPayload.put("refresh", 3600 * 1000); // 每一小时刷新一次登录态
        jsonPayload.put("expire", now + 7 * 24 * 60 * 60 * 1000); // 登录态维持一周有效
        String payload = jsonPayload.toString();
        // 使用 base64UrlEncode 进行编码
        String baseHeader = Base64.encodeToString(header.getBytes(),
                Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP);
        String basePayload = Base64.encodeToString(payload.getBytes(),
                Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP);
        // private_key，需要去除换行符
        String realPK = pKey.replaceAll("\n", "");
        // 拼接 header 和 payload
        String input = baseHeader + "." + basePayload;
        // 使用 SHA256withRSA 算法生成签名
        byte[] b1 = Base64.decode(realPK, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update(input.getBytes());
        byte[] s = privateSignature.sign();
        String sign = Base64.encodeToString(s, Base64.NO_PADDING + Base64.URL_SAFE + Base64.NO_WRAP);
        // 拼接 Token
        String token = baseHeader + "." + basePayload + "." + sign;
        return token;
    } catch (Exception e) {
        Log.d("Error", e.toString());
        return "";
    }
}

// Ticket
String token = jsonWebToken();
String ticket = "661f0d53-dd8f-483b-b032-e9945478ec5b/@@/" + token;
```

### 3. 使用 Ticket 登录

```java
import com.tencent.tcb.auth.CustomAuth;
CustomAuth customAuth = new CustomAuth(context, config.envName);

// 此处替换成您生成的自定义登录 Ticket
final String ticket = "";
// 新建网络请求线程
new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            // 登录
            customAuth.loginInWithTicket(ticket);
            // 登录成功
        } catch (final TcbException e) {
            // 登录失败
            Log.e("登录失败", e.toString());
        }
    }
}).start();
```
