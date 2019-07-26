package com.tencent.tcb.demo;

import com.tencent.tcb.utils.Config;

public class Constants {
    public static String appId = "wx9c4c30a432a38ebc";
    public static String envName = "dev-97eb6c";
    public static Config config() {
        // 请使用微信开放平台移动应用 appId
        // 并在云开发 Web 控制台：用户管理/登陆设置中绑定你的 AppID 和 AppSecret
        String appId = "wx9c4c30a432a38ebc";
        String envName = "dev-97eb6c";

        return new Config(envName, appId);
    }
}
