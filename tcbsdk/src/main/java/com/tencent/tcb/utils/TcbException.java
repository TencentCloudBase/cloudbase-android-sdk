package com.tencent.tcb.utils;

import androidx.annotation.NonNull;

public class TcbException extends Exception{
    private String errorCode = "";
    private String requestId = "";
    private String message = "";

    public TcbException(String errorCode, String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    public TcbException(String errorCode, String message, String requestId) {
        super(message);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getRequestId() {
        return this.requestId;
    }

    @NonNull
    public String toString() {
        return "Code: " + errorCode + " " + message;
    }
}
