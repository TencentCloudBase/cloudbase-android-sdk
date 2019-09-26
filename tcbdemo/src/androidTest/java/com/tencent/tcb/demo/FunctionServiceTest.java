package com.tencent.tcb.demo;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionServiceTest {
    private static FunctionService functionService;

    @BeforeClass
    public static void prepare() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        functionService = new FunctionService(Constants.envName, context);
    }

    @Test(expected = TcbException.class)
    public void testEmptyParam() throws TcbException {
        functionService.callFunction("");
    }

    @Test
    public void callFunction() {
        try {
            JSONObject res = functionService.callFunction("test-scf");
            String requestId = res.getString("requestId");
            String resultStr = res.getString("result");
            JSONObject result = new JSONObject(resultStr);
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
        try {
            JSONObject data = new JSONObject("{\"key\":\"test\"}");
            JSONObject res = functionService.callFunction("test-scf", data);
            String requestId = res.getString("requestId");
            String resultStr = res.getString("result");
            JSONObject result = new JSONObject(resultStr);
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