package com.tencent.tcb.demo;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.database.Db;
import com.tencent.tcb.database.Regexp.RegExp;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class DbCollectionTest {
    private static Db db;

    @BeforeClass
    public static void prepareTest() {
        Config config = Constants.config();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new Db(config, context);
        // 添加一个 doc
        addDoc(80);
    }

    public static void addDoc(int age) {
        JSONObject result;
        try {
            JSONObject data = new JSONObject();
            data.put("name", "older");
            data.put("age", age);
            result = db.collection("user").add(data);
            String id = result.optString("id");
            assertFalse(id.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 添加文档
    @Test
    public void db1AddTest() {
        JSONObject result;
        try {
            JSONObject data = new JSONObject();
            data.put("name", "younger");
            data.put("age", 25);
            result = db.collection("user").add(data);
            String requestId = result.optString("requestId");
            String id = result.optString("id");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(id.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 获取文档数量
    @Test
    public void db1CountTest() {
        JSONObject result;
        try {
            result = db.collection("user").count();
            String requestId = result.optString("requestId");
            int total = result.optInt("total");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertTrue(total > 0);
        } catch (TcbException e) {
            fail(e.toString());
        }
    }

    // 查询文档
    @Test
    public void db1QueryTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "older");
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

    // 指定获取文档的字段
    @Test
    public void db2FieldTest() {
        JSONObject result;
        try {
            HashMap<String, Boolean> field = new HashMap<>();
            field.put("name", true);
            JSONObject query = new JSONObject();
            query.put("name", "older");
            result = db.collection("user").where(query).field(field).get();
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

    // 指定获取文档的数量
    @Test
    public void db2LimitTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "older");
            result = db.collection("user").where(query).limit(1).get();
            String requestId = result.optString("requestId");
            int limit = result.optInt("limit");
            JSONArray data = result.optJSONArray("data");
            assertNotNull(result);
            assertNotNull(data);
            assertEquals(limit, 1);
            assertFalse(requestId.isEmpty());
            assertTrue(data.length() > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 指定排序
    @Test
    public void db2OrderByTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "older");
            result = db.collection("user").where(query).orderBy("age", "asc").get();
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

    // 指定起始位置
    @Test
    public void db2SkipTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "older");
            result = db.collection("user").where(query).skip(1).get();
            Log.d("测试", result.toString());
            String requestId = result.optString("requestId");
            int offset = result.optInt("offset");
            assertEquals(offset, 1);
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

    // serverDate 测试
    @Test
    public void db2ServerDateTest() {
        JSONObject result;
        try {
            JSONObject data = new JSONObject();
            data.put("description", "eat an apple");
            data.put("createTime", db.serverDate());

            result = db.collection("user").add(data);
            String requestId = result.optString("requestId");
            String id = result.optString("id");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(id.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }

    }

    // 正则表达式测试
    // TODO: RegExp 逻辑未完成，暂时不测试 @Test
    public void db2RegExpDateTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            RegExp regExp = db.regExp("^old", "i");
            query.put("name", regExp);
            db.collection("user").where(query).get();

            result = db.collection("user").where(query).get();
            Log.d("测试", result.toString());
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

    // 删除文档测试
    @Test
    public void db3RemoveTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "older");
            result = db.collection("user").where(query).remove();

            String requestId = result.optString("requestId");
            int deleted = result.optInt("deleted");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertTrue(deleted > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

}
