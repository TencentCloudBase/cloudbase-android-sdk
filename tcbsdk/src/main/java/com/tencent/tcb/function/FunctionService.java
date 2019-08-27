package com.tencent.tcb.function;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbListener;

import java.util.HashMap;

public class FunctionService {
    private final static String action = "functions.invokeFunction";
    private Config config;
    private Context context;

    public FunctionService(String envName, Context context) {
        this.config = new Config(envName);
        this.context = context;
    }

    public JSONObject callFunction(String name) throws TcbException {
        if (name == null || name.length() < 1) {
            throw new TcbException("INVALID_PARAM", "function name must not be empty");
        }
        JSONObject data = new JSONObject();
        return internalCallFunction(name, data);
    }

    public JSONObject callFunction(String name, JSONObject data) throws TcbException {
        if (name == null || name.length() < 1) {
            throw new TcbException("INVALID_PARAM", "function name must not be empty");
        }
        return internalCallFunction(name, data);
    }

    public void callFunctionAsync(@NonNull final String name, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = callFunction(name);
                        listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    public void callFunctionAsync(@NonNull final String name, @NonNull final JSONObject data, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = callFunction(name, data);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }

    private JSONObject internalCallFunction(String name, JSONObject requestData) throws TcbException {
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("function_name", name);
            requestParams.put("request_data", requestData.toString());
            Request request = new Request(config, context);
            JSONObject res = request.send(action, requestParams);

            // 异常情况
            if (res == null) {
                throw new TcbException("RES_NULL", "unknown error, res is null");
            }

            // 存在 code，说明返回值存在异常
            if (res.has("code")) {
                throw new TcbException(res.getString("code"), res.getString("message"));
            } else {
                // 尝试解析 response
                JSONObject data = res.getJSONObject("data");
                String result = data.getString("response_data");
                // 返回 requestId 和 result
                JSONObject ret = new JSONObject();
                ret.put("requestId", res.getString("requestId"));
                ret.put("result", result);
                return ret;
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.toString());
        }
    }
}
