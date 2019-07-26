package com.tencent.tcb.demo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.database.Db;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DbDocTest {
    private static Db db;
    private static String docId;

    @BeforeClass
    public static void prepareTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new Db(Constants.envName, context);
        addDoc(100);
    }

    public static void addDoc(int age) {
        JSONObject result;
        try {
            JSONObject data = new JSONObject();
            data.put("name", "older");
            data.put("age", age);
            result = db.collection("user").add(data);
            docId = result.optString("id");
            assertFalse(docId.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    // 获取文档测试
    @Test
    public void docTest() {
        JSONObject result;
        JSONObject data = new JSONObject();
        JSONObject res;

        try {
            data.put("name", "update-older");
            // 获取文档
            result = db.collection("user").doc(docId).get();
            JSONArray dataArray = result.optJSONArray("data");
            assertNotNull(dataArray);
            assertTrue(dataArray.length() > 0);

            // 更新文档
            result = db.collection("user").doc(docId).update(data);
            int count = result.optInt("updated");
            assertTrue(count > 0);


            // 替换文档测试
            result = db.collection("user").doc(docId).set(data);
            count = result.optInt("updated");
            assertTrue(count > 0);

            // 删除文档测试
            result = db.collection("user").doc(docId).remove();
            count = result.optInt("deleted");
            assertTrue(count > 0);

        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }
}
