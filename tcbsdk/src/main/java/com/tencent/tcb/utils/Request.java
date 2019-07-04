package com.tencent.tcb.utils;


import com.tencent.tcb.constants.Code;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    private static final String TCB_ADMIN_URL = "http://tcb-admin.tencentcloudapi.com/admin";
    private static final String TCB_OPEN_URL = "https://tcb-open.tencentcloudapi.com/admin";
    private static final int TCB_DEFAULT_TIMEOUT = 15000;
    private static final String VERSION = "beta";

    private Config config;

    public Request(Config config) {
        this.config = config;
    }

    public JSONObject send(String action, HashMap<String, String> params) throws TcbException {
        return send(action, params, "POST", new HashMap<String, String>(), 0);
    }

    public JSONObject send(String action, HashMap<String, String> params, String method) throws TcbException {
        return send(action, params, method, new HashMap<String, String>(), 0);
    }

    public JSONObject send(String action, HashMap<String, String> params, String method, HashMap<String, String> headers, int timeout) throws TcbException {
        try {
            return internalSend(action, params, method, headers, timeout);
        } catch (IOException e) {
            throw new TcbException(Code.NETWORK_ERR, e.getMessage());
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
    }

    private JSONObject internalSend(String action, HashMap<String, String> params, String method, HashMap<String, String> headers, int timeout) throws JSONException, IOException {
        headers.put("user-agent","tcb-php-sdk/beta");

        // 补充必要参数
        params.put("action", action);
        params.put("envName", config.envName);
        params.put("timestamp", "1562134915996");
        params.put("eventId", "1562134915996_88019");


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
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        // 设置body参数
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        // 开始请求
        connection.connect();
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(new JSONObject(params).toString());
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

    private String getAuth(String secretId, String secretKey, String method, String pathname, HashMap<String, String> queryParams, HashMap<String, String> headers) {

//        // 签名有效起止时间
//        long now = (new Date().getTime() / 1000) - 1;
//        long exp = now + 900;
//
//        // 要用到的 Authorization 参数列表
//        String qSignAlgorithm = "sha1";
//        String qAk = secretId;
//        String qSignTime = now + ";" + exp;
//        String qKeyTime = now + ";" + exp;
//        String qHeaderList = getObjectKeys(headers);
//        String qUrlParamList = getObjectKeys(queryParams);
//
//        // 签名算法说明文档：https://www.qcloud.com/document/product/436/7778
//        // 步骤一：计算 SignKey
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//        md.update(secretKey.getBytes());
//        md.update(qKeyTime.getBytes());
//        String signKey = bytesToHex(md.digest());
//        // 步骤二：构成 FormatString

        // 暂时构造一个月的验证码，后续再补上具体逻辑
        return "q-sign-algorithm=sha1&q-ak=AKIDpGg1BBrgrsjYDgoWr384qcGj7KMEMQXU&q-sign-time=1562135724;1564727724&q-key-time=1562135724;1564727724&q-header-list=user-agent&q-url-param-list=action;envname;eventid;path;timestamp&q-signature=7713cd265e5a8d67de2430b570c2fc4f41331beb";


    }

//    private static String getObjectKeys(JSONObject obj) {
//        String result = "";
//        Iterator<String> it = obj.keys();
//        while (it.hasNext()) {
//            String key = it.next();
//            result += (key + ";");
//        }
//        if (result.length() > 0) {
//            result = result.substring(0, result.length()-1);
//        }
//        return result.toLowerCase();
//    }
//
//    private static String bytesToHex(byte[] bytes) {
//        StringBuffer sb = new StringBuffer();
//        for(int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(bytes[i] & 0xFF);
//            if(hex.length() < 2){
//                sb.append(0);
//            }
//            sb.append(hex);
//        }
//        return sb.toString();
//    }
}