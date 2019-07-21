package com.tencent.tcb.utils;

public interface TcbStorageListener extends TcbListener{
    // 传输进度
    void onProgress(int progress);
}
