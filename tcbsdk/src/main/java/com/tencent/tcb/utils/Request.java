package com.tencent.tcb.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.constants.Code;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class Request extends BaseRequest {
    private WeixinAuth weixinAuth;

    public Request(@NonNull Config config, Context context) {
        super(config);
        weixinAuth = new WeixinAuth(context, config);
    }

    public JSONObject send(
            @NonNull String action,
            @NonNull HashMap<String, Object> params
    ) throws TcbException {
        return send(action, params, new HashMap<String, String>(), 0);
    }

    public JSONObject send(
            @NonNull String action,
            @NonNull HashMap<String, Object> params,
            HashMap<String, String> headers,
            int timeout
    ) throws TcbException {
        try {
            return authSend(action, params, headers, timeout);
        } catch (IOException e) {
            throw new TcbException(Code.NETWORK_ERR, e.getMessage());
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
    }

    public JSONObject sendMidData(
            @NonNull String action,
            @NonNull HashMap<String, Object> params
    ) throws TcbException{
        try {
            params.put("databaseMidTran", true);
            return authSend(action, params, new HashMap<String, String>(), 0);
        } catch (IOException e) {
            throw new TcbException(Code.NETWORK_ERR, e.getMessage());
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
    }

    private JSONObject authSend(
            @NonNull String action,
            @NonNull HashMap<String, Object> params,
            @NonNull HashMap<String, String> headers,
            int timeout
    ) throws JSONException, IOException, TcbException {
        String accessToken = getAccessToken();
        params.put("access_token", accessToken);
        return super.internalSend(action, params, headers, timeout);
    }

    private String getAccessToken() throws TcbException {
        weixinAuth.getAuth();
        return weixinAuth.accessToken;
    }
}