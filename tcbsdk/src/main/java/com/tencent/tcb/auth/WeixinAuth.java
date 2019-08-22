package com.tencent.tcb.auth;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class WeixinAuth extends BaseAuth {
    // IWXAPI 是第三方 app 和微信通信的 openApi 接口
    private IWXAPI api = null;
    private String code = "";
    private static WeixinAuth instance;
    public LoginListener loginListener = null;

    public WeixinAuth(Context context, Config config) {
        super(context, config);
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
    public void registerToWx(Context context) {
        // 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(context, appId, true);

        // 将应用的 appId 注册到微信
        api.registerApp(appId);
    }

    /**
     * 发送请求，通过 code 获取 accessToken
     */
    private void getAccessTokenByCode() throws TcbException, JSONException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("appid", appId);
        param.put("code", code);

        String authAction = "auth.getJwt";
        JSONObject res = request.send(authAction, param);
        String code = res.optString("code");

        // 存在异常
        if (!code.isEmpty()) {
            // 读取错误信息
            String message = res.getString("message");

            if (code.equals("REFRESH_TOKEN_EXPIRED")) {
                throw new TcbException(Code.REFRESH_TOKEN_EXPIRED, message);
            }

            throw new TcbException(code, message);
        }

        accessToken = res.optString("access_token");
        refreshToken = res.getString("refresh_token");
        accessTokenExpired = res.getLong("access_token_expire") + new Date().getTime();
        tcbStore.set(tcbStore.REFRESH_TOKEN_KEY, refreshToken);
        tcbStore.set(tcbStore.ACCESS_TOKEN_KEY, accessToken);
        tcbStore.set(tcbStore.ACCESS_TOKEN_EXPIRED_KEY, accessTokenExpired);
    }


    /**
     * 拉起微信，获取登录授权，开发者调用
     */
    public void login(LoginListener listener) {
        loginListener = listener;
        // 先检查是否存在 code
        if (code != null && !code.isEmpty()) {
            try {
                getAccessTokenByCode();
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

        if (appId.isEmpty()) {
            loginListener.onFailed(new TcbException(Code.INVALID_PARAM, "AppId 不能为空"));
        }

        final SendAuth.Req req = new SendAuth.Req();
        // 获取用户信息
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login_xxx";
        api.sendReq(req);
    }

    public void loginWithCode(String code) {
        loginWithCode(code, this.loginListener);
    }

    /**
     * 微信登录，开发者调用
     */
    public void loginWithCode(String code, LoginListener listener) {
        if (listener != null) {
            this.loginListener = listener;
        }

        if (code.isEmpty()) {
            loginListener.onFailed(new TcbException(Code.INVALID_PARAM, "微信 Code 不能为空"));
        }
        this.code = code;
        login(loginListener);
    }

    /**
     * 开发者换回微信 refreshToken 后手动设置
     */
    public void setRefreshToken(String refreshToken) throws TcbException {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TcbException(Code.INVALID_PARAM, "refreshToken 不能为空");
        }
        this.refreshToken = refreshToken;
        // 校验 refreshToken 合法性
        refreshAccessToken();
        tcbStore.set(tcbStore.REFRESH_TOKEN_KEY, refreshToken);
    }

    /**
     * 清空存储数据，登出
     */
    public void logout() {
        tcbStore.clear();
    }
}
