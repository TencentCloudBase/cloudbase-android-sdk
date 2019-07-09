package com.tencent.tcb.auth;

import com.tencent.tcb.utils.TcbException;

public interface LoginListener {
    void onSuccess();
    void onFailed(TcbException e);
}
