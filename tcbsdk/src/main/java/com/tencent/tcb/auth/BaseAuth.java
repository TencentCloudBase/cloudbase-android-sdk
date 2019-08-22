package com.tencent.tcb.auth;

import android.content.Context;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.BaseRequest;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TCBStore;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class BaseAuth {
    public BaseRequest request = null;
    public TCBStore tcbStore = null;
    public String accessToken = null;
    public String refreshToken = null;
    public String appId = null;
    public long accessTokenExpired = 0;

    public BaseAuth(Context context, String envName) {
        tcbStore = new TCBStore(context);
        Config config = new Config(envName);
        request = new BaseRequest(config);
    }

    public BaseAuth(Context context, Config config) {
        this.appId = config.appId;
        tcbStore = new TCBStore(context);
        request = new BaseRequest(config);
    }

    /**
     * 刷新 accessToken
     */
    public void refreshAccessToken() throws TcbException {
        HashMap<String, Object> param = new HashMap<>();
        if (appId != null && !appId.isEmpty()) {
            param.put("appid", appId);
        }
        param.put("refresh_token", refreshToken);

        String authAction = "auth.getJwt";
        JSONObject res = request.send(authAction, param);
        String code = res.optString("code");

        // 存在异常
        if (!code.isEmpty()) {
            // 读取错误信息
            String message = res.optString("message");

            if (code.equals("REFRESH_TOKEN_EXPIRED")) {
                throw new TcbException(Code.REFRESH_TOKEN_EXPIRED, "Refresh Token 失效：" + message);
            }

            if (code.equals("SIGN_PARAM_INVALID")) {
                throw new TcbException(Code.INVALID_REFRESH_TOKEN, "无效的 Refresh Token" + message);
            }

            throw new TcbException(code, message);
        }
        // 读取 accessToken，校验是否为空
        accessToken = res.optString("access_token");
        accessTokenExpired = res.optLong("access_token_expire") + new Date().getTime();
        if (accessToken.isEmpty() || accessTokenExpired == 0) {
            throw new TcbException(Code.CHECK_LOGIN_FAILED, "身份续期失效");
        }
        // 正常响应，存储信息
        tcbStore.set(tcbStore.ACCESS_TOKEN_KEY, accessToken);
        tcbStore.set(tcbStore.ACCESS_TOKEN_EXPIRED_KEY, accessTokenExpired);
    }


    /**
     * 获取 accessToken
     */
    public String getAccessToken() throws TcbException {
        // accessToken 内存缓存
        if (accessToken != null && !accessToken.isEmpty()) {
            long now = new Date().getTime();
            // 临时 token 有效
            if (accessTokenExpired != 0 && accessTokenExpired > now) {
                return accessToken;
            }
        }

        // 从本地存储中读取 auth 信息
        AuthData authData = tcbStore.get();
        accessToken = authData.accessToken;
        refreshToken = authData.refreshToken;
        accessTokenExpired = authData.accessTokenExpired;

        if (accessToken != null && !accessToken.isEmpty()) {
            long now = new Date().getTime();
            // 临时 token 有效
            if (accessTokenExpired != 0 && accessTokenExpired > now) {
                return accessToken;
            }
            // accessToken 无效，移除
            accessToken = null;
            accessTokenExpired = 0;
            tcbStore.remove(tcbStore.ACCESS_TOKEN_KEY);
            tcbStore.remove(tcbStore.ACCESS_TOKEN_EXPIRED_KEY);
        }

        // accessToken 无效，刷新
        if (refreshToken != null && !refreshToken.isEmpty()) {
            // 刷新 accessToken
            // refreshToken 可能失效，直接抛出错误，不删除本地存储的 refreshToken
            refreshAccessToken();
            return accessToken;
        }

        // 无身份信息
        throw new TcbException(Code.NOT_LOGIN, "未找到身份信息，请重新登录");
    }

}
