package com.tencent.tcb.auth;

public class WeixinAuthData {
    public String accessToken = "";
    public String refreshToken = "";
    public long accessTokenExpired = 0;

    public WeixinAuthData(String accessToken, String refreshToken, long expired) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpired = expired;
    }
}
