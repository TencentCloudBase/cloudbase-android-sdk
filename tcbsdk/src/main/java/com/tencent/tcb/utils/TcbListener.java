package com.tencent.tcb.utils;

import org.json.JSONObject;

public interface TcbListener{
    // 传输成功
    void onSuccess(JSONObject result);

    // 传输失败
    void onFailed(TcbException e);
}
