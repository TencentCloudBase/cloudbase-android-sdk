package com.tencent.tcb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.tcb.auth.AuthData;

public class TCBStore {
    private final String STORE_NAME = "tcb_store";
    public final String ACCESS_TOKEN_KEY = "accessToken";
    public final String ACCESS_TOKEN_EXPIRED_KEY = "expired";
    public final String REFRESH_TOKEN_KEY = "refreshToken";

    private Context context;
    private SharedPreferences tcbStore = null;

    public TCBStore(Context context) {
        this.context = context;
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void set(String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setAll(AuthData data) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(ACCESS_TOKEN_KEY, data.accessToken);
        editor.putString(REFRESH_TOKEN_KEY, data.refreshToken);
        editor.putLong(ACCESS_TOKEN_EXPIRED_KEY, data.accessTokenExpired);
        editor.apply();
    }

    public AuthData get() {
        SharedPreferences pref = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        String accessToken = pref.getString(ACCESS_TOKEN_KEY, "");
        String refreshToken = pref.getString(REFRESH_TOKEN_KEY, "");
        long accessTokenExpired = pref.getLong(ACCESS_TOKEN_EXPIRED_KEY, 0);
        return new AuthData(accessToken, refreshToken, accessTokenExpired);
    }

    public void remove(String item) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(item);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}