package com.tencent.tcb.auth;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.BaseRequest;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TCBStore;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class WeixinAuth {
    private BaseRequest request = null;
    // appId 替换为你的应用从官方网站申请到的合法 appID
    private String appId = "";
    // IWXAPI 是第三方app和微信通信的 openApi 接口
    private IWXAPI api = null;
    private TCBStore tcbStore = null;
    private String code = "";
    private String refreshToken = "";
    private long accessTokenExpired = 0;
    private static WeixinAuth instance;
    public LoginListener loginListener = null;
    public String accessToken = "";

    private enum GetAccessTokenType {Code, RefreshToken}

    // 当出现 login failed 错误时，尝试重新获取 accessToken，标志是否重试
    private boolean hashCheckLogin = false;

    public WeixinAuth(Context context, Config config) {
        request = new BaseRequest(config);
        this.appId = config.appId;
        tcbStore = new TCBStore(context);
        registerToWx(context);
    }

    public static WeixinAuth getInstance(Context context, Config config) {
        if (instance == null) {
            instance = new WeixinAuth(context, config);
            return instance;
        }
        return instance;
    }

    public IWXAPI getWxAPI() {
        return this.api;
    }

    // 注册到微信
    // 要使你的程序启动后微信终端能响应你的程序，必须在代码中向微信终端注册你的 id
    public void registerToWx(Context context) {
        // 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(context, appId, true);

        // 将应用的 appId 注册到微信
        api.registerApp(appId);
    }

    /**
     * 发送请求，通过 code 或 refreshToken 获取 accessToken
     */
    private void getAccessToken(Enum type) throws TcbException, JSONException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("appid", appId);
        if (type == GetAccessTokenType.RefreshToken) {
            param.put("refresh_token", refreshToken);
        }
        if (type == GetAccessTokenType.Code) {
            param.put("code", code);
        }

        String authAction = "auth.getJwt";
        JSONObject res = request.send(authAction, param);
        String code = res.optString("code");

        // 存在异常
        if (!code.isEmpty()) {
            // 读取错误信息
            String message = res.getString("message");

            if (code.equals("REFRESH_TOKEN_EXPIRED")) {
                hashCheckLogin = false;
                throw new TcbException(Code.REFRESH_TOKEN_EXPIRED, message);
            }

            if (code.equals("CHECK_LOGIN_FAILED")) {
                // check login failed 时，尝试
                // 防止陷入死循环
                if (hashCheckLogin) {
                    throw new TcbException(Code.CHECK_LOGIN_FAILED, message);
                } else {
                    //
                    hashCheckLogin = true;
                    if (refreshToken != null && !refreshToken.isEmpty()) {
                        getAccessToken(GetAccessTokenType.RefreshToken);
                    } else {
                        throw new TcbException(Code.CHECK_LOGIN_FAILED, message);
                    }
                }
            }

            throw new TcbException(code, message);
        }

        // 正常响应，存储信息
        // 通过 code 获取 accessToken，响应有 refresh_token
        if (type == GetAccessTokenType.Code) {
            accessToken = res.optString("access_token");
            refreshToken = res.getString("refresh_token");
            accessTokenExpired = res.getLong("access_token_expire") + new Date().getTime();
        } else {
            // 通过 refreshToken 获取，响应无 refresh_token
            accessToken = res.optString("access_token");
            accessTokenExpired = res.getLong("access_token_expire") + new Date().getTime();
        }
    }


    /**
     * 检查、处理、获取 accessToken
     * 内部请求使用
     */
    public void getAuth() throws TcbException {
        // 不存在 code，再从存储中读取数据
        WeixinAuthData authData = tcbStore.get();
        accessToken = authData.accessToken;
        refreshToken = authData.refreshToken;
        accessTokenExpired = authData.accessTokenExpired;

        if (accessToken != null && !accessToken.isEmpty()) {
            long now = new Date().getTime();
            // 临时 token 有效
            if (accessTokenExpired != 0 && accessTokenExpired > now) {
                return;
            }
            // accessToken 无效，移除
            tcbStore.remove(tcbStore.ACCESS_TOKEN_KEY);
            tcbStore.remove(tcbStore.ACCESS_TOKEN_EXPIRED_KEY);
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            // refreshToken 有效
            try {
                getAccessToken(GetAccessTokenType.RefreshToken);
                tcbStore.set(tcbStore.ACCESS_TOKEN_KEY, accessToken);
                tcbStore.set(tcbStore.ACCESS_TOKEN_EXPIRED_KEY, accessTokenExpired);
                return;
            } catch (TcbException e) {
                // 服务端返回 refreshToken 无效
                tcbStore.remove(tcbStore.REFRESH_TOKEN_KEY);
                throw e;
            } catch (JSONException e) {
                throw new TcbException(Code.JSON_ERR, e.toString());
            }
        }

        // 无身份信息
        throw new TcbException(Code.REFRESH_TOKEN_EXPIRED, "");
    }

    /**
     * 拉起微信，获取登录授权
     * 开发者调用
     */
    public void login(LoginListener listener) {
        loginListener = listener;
        // 先检查是否存在 code
        if (code != null && !code.isEmpty()) {
            try {
                getAccessToken(GetAccessTokenType.Code);

                tcbStore.set(tcbStore.ACCESS_TOKEN_KEY, accessToken);
                tcbStore.set(tcbStore.REFRESH_TOKEN_KEY, refreshToken);
                tcbStore.set(tcbStore.ACCESS_TOKEN_EXPIRED_KEY, accessTokenExpired);
                listener.onSuccess();
                return;
            } catch (TcbException e) {
                loginListener.onFailed(e);
                return;
            } catch (JSONException e) {
                loginListener.onFailed(new TcbException(Code.JSON_ERR, e.toString()));
                return;
            }
        }

        // 未授权
        // 未安装微信
        if (!api.isWXAppInstalled()) {
            loginListener.onFailed(new TcbException(Code.AUTH_FAILED, "未安装微信"));
            return;
        }

        final SendAuth.Req req = new SendAuth.Req();
        // 获取用户信息
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        api.sendReq(req);
    }

    // 清空存储数据，登出
    public void logout() {
        tcbStore.clear();
    }

    // WXEntryActivity 获取 code 后回调传回 code
    public void callback(final String code) {
        this.code = code;
        login(loginListener);
    }
}
