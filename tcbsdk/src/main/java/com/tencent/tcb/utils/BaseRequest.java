package com.tencent.tcb.utils;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BaseRequest {
    private static final String TCB_WEB_URL = "https://tcb-api.tencentcloudapi.com/web";
    private static final int TCB_DEFAULT_TIMEOUT = 15000;
    private static final String VERSION = "beta";
    private static final String DATA_VERSION = "2019-06-01";

    private Config config;

    public BaseRequest(@NonNull Config config) {
        this.config = config;
    }

    public JSONObject send(
            @NonNull String action,
            @NonNull HashMap<String, Object> params
    ) throws TcbException {
        return send(action, params, new HashMap<String, String>(), 0);
    }

    public JSONObject send(
            @NonNull String action, @NonNull HashMap<String, Object> params,
            HashMap<String, String> headers, int timeout
    ) throws TcbException {
        try {
            return internalSend(action, params, headers, timeout);
        } catch (IOException e) {
            throw new TcbException(Code.NETWORK_ERR, e.getMessage());
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
    }

    public JSONObject internalSend(
            @NonNull String action,
            @NonNull HashMap<String, Object> params,
            @NonNull HashMap<String, String> headers,
            int timeout
    ) throws JSONException, IOException {
        headers.put("user-agent", "tcb-php-sdk/beta");

        // 补充必要参数
        params.put("action", action);
        params.put("env", config.envName);
        params.put("sdk_version", VERSION);
        params.put("dataVersion", DATA_VERSION);
        params.put("loginType", "WECHAT-OPEN");
        String str = new JSONObject(params).toString();

        // 处理参数
        timeout = timeout != 0 ? timeout : (config.timeout != 0 ? config.timeout :
                TCB_DEFAULT_TIMEOUT);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(timeout,
                TimeUnit.MILLISECONDS).build();

        // 数据类型为 JSON 格式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, new JSONObject(params).toString());
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();

        requestBuilder.addHeader("Connection", "Keep-Alive");
        requestBuilder.addHeader("Charset", "UTF8");
        requestBuilder.addHeader("Content-Type", "application/json");
        requestBuilder.addHeader("Accept", "application/json");
        requestBuilder.addHeader("Referer", "http://jimmytest-088bef.tcb.qcloud.la");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        // 发送 POST 请求
        okhttp3.Request request = requestBuilder.url(TCB_WEB_URL)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            if (response.body() == null) {
                return null;
            }
            String resBody = response.body().string();
            return new JSONObject(resBody);
        } else {
            return null;
        }
    }
}
