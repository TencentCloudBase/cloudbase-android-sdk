package com.tencent.tcb.demo;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionServiceTest {
    private static Config config;
    private static Context context;
    public static String envName = "dev-97eb6c";
    // 请使用微信开放平台移动应用 appId
    // 并在云开发 Web 控制台：用户管理/登陆设置中绑定你的 AppID 和 AppSecret
    public static String appId = "wx9c4c30a432a38ebc";
    public static String domain = "http://jimmytest-088bef.tcb.qcloud.la";

    @BeforeClass
    public static void prepare() {
        config = new Config(envName, appId, domain);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test(expected = TcbException.class)
    public void testEmptyParam() throws TcbException {
        FunctionService functionService = new FunctionService(config, context);
        functionService.callFunction("");
    }

    @Test
    public void callFunction() {
        FunctionService functionService = new FunctionService(config, context);
        try {
            JSONObject res = functionService.callFunction("test-scf");
            String requestId = res.getString("requestId");
            JSONObject result = res.getJSONObject("result");
            Log.d("测试", res.toString());
            assertFalse(requestId.isEmpty());
            assertNotNull(result);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    @Test
    public void callFunctionWithData() {
        FunctionService functionService = new FunctionService(config, context);
        try {
            JSONObject data = new JSONObject("{\"key\":\"test\"}");
            JSONObject res = functionService.callFunction("test-scf", data);
            String requestId = res.getString("requestId");
            JSONObject result = res.getJSONObject("result");
            Log.d("测试", res.toString());
            assertFalse(requestId.isEmpty());
            assertNotNull(result);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }
}