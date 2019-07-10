package com.tencent.tcb.function;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import java.util.HashMap;

public class FunctionService {
    private final static String action = "functions.invokeFunction";
    private Config config;
    private Context context;

    public FunctionService(Config config, Context context) {
        this.config = config;
        this.context = context;
    }

    public JSONObject callFunction(String name) throws JSONException, TcbException {
        if (name == null || name.length() < 1) {
            throw new TcbException("INVALID_PARAM", "function name must not be empty");
        }
        JSONObject data = new JSONObject();
        return internalCallFunction(name, data);
    }

    public JSONObject callFunction(String name, JSONObject data) throws JSONException, TcbException {
        if (name == null || name.length() < 1) {
            throw new TcbException("INVALID_PARAM", "function name must not be empty");
        }
        return internalCallFunction(name, data);
    }

    private JSONObject internalCallFunction(String name, JSONObject requestData) throws JSONException, TcbException {
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
                JSONObject result;
                try {
                    // response 为 JSON 字符串
                    result = new JSONObject(data.getString("response_data"));
                } catch (JSONException e) {
                    // response 为对象
                    result = data.getJSONObject("response_data");
                }
                // 返回 requestId 和 result
                JSONObject ret = new JSONObject();
                ret.put("requestId", res.getString("requestId"));
                ret.put("result", result);
                return ret;
            }
        } catch (TcbException e) {
            Log.e("JSON Error", e.toString());
            throw e;
        }
    }
}
