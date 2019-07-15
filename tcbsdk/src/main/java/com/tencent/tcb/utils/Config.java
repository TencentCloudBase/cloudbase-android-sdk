package com.tencent.tcb.utils;

public class Config {
    public String envName = "";
    public String appId = "";
    public String domain = "";
    public String scope = "";
    public String proxy = "";
    public int timeout = 0;

    public Config(String envName, String appId, String domain) {
        this.envName = envName;
        this.appId = appId;
        this.domain = domain;
    }
}
