package com.tencent.tcb;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.Config;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        Config config = new Config();
        JSONObject params = new JSONObject();
        try {
            params.put("path", "jimmyjzhang/wxf5132aa5236cbef7.o6zAJs6Ww9c8IdpuAjeD9JTHyuqg.7RWbrbsJ8fNab37154c5773759d9dd0ca4c0288ab100.jpeg");
        } catch (JSONException e) {

        }
        JSONObject headers = new JSONObject();
        try {
            Request.send("storage.getUploadMetadata", params, "post", headers, 3000, config);
        } catch (Exception e) {

        }
        assertEquals(4, 2 + 2);
    }
}