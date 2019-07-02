package com.tencent.tcb.utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;

public class Request {
    private static final String TCB_ADMIN_URL = "http://tcb-admin.tencentcloudapi.com/admin";
    private static final String TCB_OPEN_URL = "https://tcb-open.tencentcloudapi.com/admin";
    private static final int TCB_DEFAULT_TIMEOUT = 15000;
    private static final String VERSION = "beta";

    public static JSONObject send(String action, JSONObject params, String method, JSONObject headers, int timeout, Config config) throws JSONException, IOException {
        // 补充必要参数
        params.put("action", action);
        params.put("envName", config.envName);
        params.put("timestamp", "1562077122205");
        params.put("eventId", "1562077122205_88019");


        // 处理参数
        timeout = timeout != 0 ? timeout : ( config.timeout != 0 ? config.timeout : TCB_DEFAULT_TIMEOUT);

        // 签名
        String authorization = getAuth(config.secretId, config.secretKey, method, "/admin", params, headers);
        params.put("authorization", authorization);
        // 补充其他参数
        if (!"".equals(config.sessionToken)) {
            params.put("sessionToken", config.sessionToken);
        }
        params.put("sdk_version", VERSION);


        // 初始化请求
        URL url = new URL(TCB_ADMIN_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(timeout);
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 设置headers
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        Iterator<String> it = headers.keys();
        while (it.hasNext()) {
            String key = it.next();
            String value = headers.getString(key);
            connection.setRequestProperty(key, value);
        }
        // 设置body参数
        byte[] data = (params.toString()).getBytes();
        connection.setRequestProperty("Content-Length", String.valueOf(data.length));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        // 开始请求
        connection.connect();
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(params.toString());
        wr.flush();

        // 处理回包
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String lines = br.readLine();
            br.close();
            JSONObject js = new JSONObject(lines);
            return js;
        }
        else {
            return null;
        }
    }

    private static String getAuth(String secretId, String secretKey, String method, String pathname, JSONObject queryParams, JSONObject headers) {
        // 使用tcb-admin-node计算出来的
        return "q-sign-algorithm=sha1&q-ak=AKIDpGg1BBrgrsjYDgoWr384qcGj7KMEMQXU&q-sign-time=1562078160;1562079060&q-key-time=1562078160;1562079060&q-header-list=user-agent&q-url-param-list=action;eventid;path;timestamp&q-signature=8aaf31de20eeed2ad00e75e5a4008791acb021ae";
    }
}