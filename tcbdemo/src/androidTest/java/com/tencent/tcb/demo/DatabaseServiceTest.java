package com.tencent.tcb.demo;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.database.Command;
import com.tencent.tcb.database.Db;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class DatabaseServiceTest {
    private static Db db;

    @BeforeClass
    public static void prepareTest() {
        Config config = Constants.config();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new Db(config, context);
    }

    @Test
    public void dbAddTest() {
        JSONObject result;
        try {
            JSONObject data = new JSONObject();
            data.put("name", "jimmytest");
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

    @Test
    public void dbCountTest() {
        JSONObject result;
        try {
            result = db.collection("user").count();
            String requestId = result.optString("requestId");
            String total = result.optString("total");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(total.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        }
    }

    @Test
    public void dbQueryTest() {
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            query.put("name", "jimmytest");
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            String data = result.optString("data");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(data.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    @Test
    public void dbQueryCommandTest() {
        Command cmd = db.command;
        JSONObject result;
        try {
            JSONObject query = new JSONObject();
            // age大于18并且小于30
             query.put("age", cmd.and(cmd.gt(18), cmd.lt(30)));

            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            String data = result.optString("data");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(data.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    @Test
    public void dbOrderTest() {
        JSONObject result;
        try {
            result = db.collection("user").orderBy("age", "asc").get();
            String requestId = result.optString("requestId");
            String data = result.optString("data");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(data.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        }

    }

    @Test
    public void dbFieldTest() {
        JSONObject result;
        try {
            HashMap<String, Boolean> fieldMap = new HashMap<>();
            fieldMap.put("age", true);

            result = db.collection("user").field(fieldMap).get();
            String requestId = result.optString("requestId");
            String data = result.optString("data");
            assertNotNull(result);
            assertFalse(requestId.isEmpty());
            assertFalse(data.isEmpty());
        } catch (TcbException e) {
            fail(e.toString());
        }

    }
//
//    /**
//     * 文档操作
//     *
//     * @param context
//     */
//    public void dbDocTest() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Db db = new Db(config, context);
//                JSONObject result;
//                try {
//                    // 创建文档
//                    JSONObject data = new JSONObject();
//                    data.put("name", "jimmytest2");
//                    result = db.collection("user").add(data);
//                    Log.d("DbDoc", "Db doc create success: " + result.toString());
//
//                    // 解析docId
//                    String docId = result.getString("id");
//
//                    // 替换文档数据
//                    data.put("age", 28);
//                    result = db.collection("user").doc(docId).set(data);
//                    Log.d("DbDoc", "Db doc set success: " + result.toString());
//
//                    // 更新文档数据
//                    JSONObject data2 = new JSONObject();
//                    data2.put("age", 23);
//                    result = db.collection("user").doc(docId).update(data2);
//                    Log.d("DbDoc", "Db doc update success: " + result.toString());
//
//                    // 删除文档
//                    result = db.collection("user").doc(docId).remove();
//                    Log.d("DbDoc", "Db doc remove success: " + result.toString());
//                } catch (TcbException e) {
//                    Log.e("DbDoc", e.toString());
//                } catch (JSONException e) {
//                    Log.e("DbDoc", e.toString());
//                }
//            }
//        }).start();
//    }
//
    @Test
    public void dbServerDateTest() {
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
//
//    public void dbGeoTest() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Db db = new Db(config, context);
//                JSONObject result;
//                try {
//                    // 创建带有地理位置的数据
//                    JSONObject data = new JSONObject();
//                    data.put("description", "eat an apple");
//                    data.put("location", db.geo.point(113.323809, 23.097732));
//                    result = db.collection("tcb_android").add(data);
//                    Log.d("DbServerDateTest", "Db server data success:" + result.toString());
//
//                    // 按地理位置寻找
//                    JSONObject query = new JSONObject();
//                    query.put("location", db.command.geoNear(
//                            db.geo.point(113, 23),
//                            1000,
//                            5000));
//                    result = db.collection("tcb_android").where(query).get();
//                    Log.d("DbServerDateTest", "Db server data success:" + result.toString());
//
//                } catch (TcbException e) {
//                    Log.e("DbServerDateTest", e.toString());
//                } catch (JSONException e) {
//                    Log.e("DbServerDateTest", e.toString());
//                }
//            }
//        }).start();
//    }
}
