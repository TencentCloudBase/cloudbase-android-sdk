package com.tencent.tcb.demo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.database.Command;
import com.tencent.tcb.database.Db;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DbCommandTest {
    private static Db db;
    private static String docId;

    @BeforeClass
    public static void prepareTest() {
        Config config = Constants.config();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new Db(config, context);
        addDoc(100);
        addDoc(80);
    }

    public static void addDoc(int age) {
        JSONObject result;
        JSONArray list = new JSONArray();
        list.put("one");
        try {
            JSONObject data = new JSONObject();
            data.put("name", "older");
            data.put("age", age);
            data.put("count", 1);
            data.put("list", list);
            result = db.collection("user").add(data);
            docId = result.optString("id");
            assertFalse(docId.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // eq command 测试
    @Test
    public void eqTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.eq(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // neq command 测试
    @Test
    public void neqTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.neq(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }


    // lt command 测试
    @Test
    public void ltTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.lt(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // lte command 测试
    @Test
    public void lteTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.lte(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // gt command 测试
    @Test
    public void gtTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.gt(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertEquals(data.length(), 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // gte command 测试
    @Test
    public void gteTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 等于 100
            query.put("age", cmd.gte(100));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // in command 测试
    @Test
    public void inTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            ArrayList<Object> ages = new ArrayList<>();
            ages.add(80);
            ages.add(100);
            // age 在 80, 100
            query.put("age", cmd.in(ages));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // nin command 测试
    @Test
    public void ninTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            ArrayList<Object> ages = new ArrayList<>();
            ages.add(80);
            ages.add(100);
            // age 不在 25, 80, 100
            query.put("age", cmd.nin(ages));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // and command preset 测试
    @Test
    public void andPresetTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 大于 80 且小于 100
            query.put("age", cmd.and(cmd.gt(80), cmd.lt(100)));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertEquals(data.length(), 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // and command flow 测试
    @Test
    public void andTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 大于 80 且小于 100
            query.put("age", cmd.gt(80).and(cmd.lt(100)));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertEquals(data.length(), 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }


    // or command preset 测试
    @Test
    public void orPresetTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 小于 80 或大于 100
            query.put("age", cmd.or(cmd.lt(80), cmd.gt(100)));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // or command flow 测试
    @Test
    public void orTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject query = new JSONObject();
            // age 小于 25 或大于 100
            query.put("age", cmd.lt(25).or(cmd.gt(100)));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertFalse(requestId.isEmpty());
            assertEquals(data.length(), 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // set command 测试
    @Test
    public void setTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            JSONObject info = new JSONObject("{\"data\":{\"a\":1,\"b\":2}}");
            data.put("info", cmd.set(info));
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // remove command 测试
    @Test
    public void removeTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            data.put("info", cmd.remove());
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // inc command 测试
    @Test
    public void incTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            data.put("count", cmd.inc(1));
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // mul command 测试
    @Test
    public void mulTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            data.put("count", cmd.mul(2));
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // push command 测试
    @Test
    public void pushTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            ArrayList<Object> list = new ArrayList<>();
            list.add("two");
            data.put("list", cmd.push(list));
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // pop command 测试
    @Test
    public void popTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            data.put("list", cmd.pop());
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // unshift command 测试
    @Test
    public void unshiftTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            ArrayList<Object> list = new ArrayList<>();
            list.add("zero");
            data.put("list", cmd.unshift(list));
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // shift command 测试
    @Test
    public void shiftTest() {
        Command cmd = db.command;
        JSONObject result;

        try {
            JSONObject data = new JSONObject();
            data.put("list", cmd.shift());
            result = db.collection("user").doc(docId).update(data);
            assertNotNull(result);
            int count = result.optInt("updated");
            assertTrue(count > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }
}
