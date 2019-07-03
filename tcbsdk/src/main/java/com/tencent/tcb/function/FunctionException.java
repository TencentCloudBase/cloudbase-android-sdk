package com.tencent.tcb.function;

public class FunctionException extends Exception {
    public String code;
    public String message;

    public FunctionException(String code, String errorMessage) {
        this.code = code;
        this.message = errorMessage;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}
