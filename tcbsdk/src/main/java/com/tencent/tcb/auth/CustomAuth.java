package com.tencent.tcb.auth;

import android.content.Context;

import com.tencent.tcb.utils.TcbException;

import org.json.JSONObject;

import java.util.HashMap;

public class CustomAuth extends BaseAuth {
    public CustomAuth(Context context, String envName) {
        super(context, envName);
    }

    public void loginInWithTicket(String ticket) throws TcbException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("ticket", ticket);
        String authAction = "auth.signInWithTicket";

        JSONObject res = request.send(authAction, param);
        String code = res.optString("code");
        refreshToken = res.optString("refresh_token");
        String message = res.optString("message");
        // ticket 获取 refreshToken 无效
        if (!code.isEmpty() && refreshToken.isEmpty()) {
            throw new TcbException(code, "Ticket 验证失败：" + message);
        }
        // 存储 refreshToken
        tcbStore.set(tcbStore.REFRESH_TOKEN_KEY, refreshToken);
        // 使用 refreshToken 获取 accessToken
        refreshAccessToken();
    }


    public void logout() {
        tcbStore.clear();
    }
}
