package com.tencent.tcb.function;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.Config;

import java.io.IOException;

public class FunctionService {
    private final static String action = "functions.invokeFunction";

    public static JSONObject callFunction(String name) throws FunctionException, IOException, JSONException {
        if (name == null || name.length() < 1) {
            throw new FunctionException("INVALID_PARAM", "function name must not be empty");
        }
        JSONObject data = new JSONObject();
        return innerCallFunction(name, data);
    }

    public static JSONObject callFunction(String name, JSONObject data) throws FunctionException, IOException, JSONException {
        if (name == null || name.length() < 1) {
            throw new FunctionException("INVALID_PARAM", "function name must not be empty");
        }
        return innerCallFunction(name, data);
    }

    private static JSONObject innerCallFunction(String name, JSONObject requestData) throws FunctionException, IOException, JSONException {
        try {
            Config config = new Config();
            JSONObject requestParams = new JSONObject();
            requestParams.put("function_nae", name);
            requestParams.put("request_data", requestData.toString());
            Request request = new Request(config);
            JSONObject res = request.send(action, requestParams, "POST");

            // 异常情况
            if (res == null) {
                throw new FunctionException("RES_NULL", "unknown error, res is null");
            }

            // 存在 code，说明返回值存在异常
            if (res.has("code")) {
                throw new FunctionException(res.getString("code"), res.getString("message"));
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
        } catch (JSONException e) {
            Log.e("JSON Error", e.toString());
            throw e;
        } catch (IOException e) {
            Log.e("IO Error", e.toString());
            throw e;
        }
    }
}
