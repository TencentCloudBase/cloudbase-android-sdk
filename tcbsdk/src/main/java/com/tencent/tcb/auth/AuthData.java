package com.tencent.tcb.auth;

public class AuthData {
    public String accessToken = "";
    public String refreshToken = "";
    public long accessTokenExpired = 0;

    public AuthData(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AuthData(String accessToken, String refreshToken, long expired) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpired = expired;
    }
}
