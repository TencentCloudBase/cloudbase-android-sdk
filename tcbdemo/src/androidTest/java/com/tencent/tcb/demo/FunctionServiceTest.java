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

    @BeforeClass
    public static void prepare() {
        config = Constants.config();
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