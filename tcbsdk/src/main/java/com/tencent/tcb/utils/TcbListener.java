package com.tencent.tcb.utils;

import org.json.JSONObject;

public interface TcbListener{
    // 请求成功
    void onSuccess(JSONObject result);

    // 请求失败
    void onFailed(TcbException e);
}
