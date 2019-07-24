package com.tencent.tcb.demo;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tencent.tcb.database.Db;
import com.tencent.tcb.database.Geos.LineString;
import com.tencent.tcb.database.Geos.MultiLineString;
import com.tencent.tcb.database.Geos.MultiPoint;
import com.tencent.tcb.database.Geos.MultiPolygon;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Geos.Polygon;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DbGeoTest {
    private static Db db;
    private static String docId;
    private static JSONObject doc;

    @BeforeClass
    public static void prepareTest() {
        Config config = Constants.config();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new Db(config, context);
        doc = genDoc();
    }

    public static JSONObject genDoc() {
        try {
            Point geoNearPoint = db.geo.point(0, 0);
            LineString line = db.geo.lineString(new ArrayList<Point>(Arrays.asList(randomPoint(),
                    randomPoint())));

            // “回“ 字外环
            Point point1 = db.geo.point(-2, -2);
            Point point2 = db.geo.point(2, -2);
            Point point3 = db.geo.point(2, 2);
            Point point4 = db.geo.point(-2, 2);

            // “回” 字的内环
            Point point5 = db.geo.point(-1, -1);
            Point point6 = db.geo.point(1, -1);
            Point point7 = db.geo.point(1, 1);
            Point point8 = db.geo.point(-1, 1);

            ArrayList<Point> pointsList1 = new ArrayList<>();
            pointsList1.add(point1);
            pointsList1.add(point2);
            pointsList1.add(point3);
            pointsList1.add(point4);
            pointsList1.add(point1);

            ArrayList<Point> pointsList2 = new ArrayList<>();
            pointsList2.add(point5);
            pointsList2.add(point6);
            pointsList2.add(point7);
            pointsList2.add(point8);
            pointsList2.add(point5);

            LineString line1 = db.geo.lineString(pointsList1);
            LineString line2 = db.geo.lineString(pointsList2);
            ArrayList<LineString> lineList = new ArrayList<>();
            lineList.add(line1);
            lineList.add(line2);

            // “回” 字
            Polygon polygon = db.geo.polygon(lineList);

            ArrayList<Point> multiPointList = new ArrayList<>();
            multiPointList.add(randomPoint());
            multiPointList.add(randomPoint());
            multiPointList.add(randomPoint());
            MultiPoint multiPoint = db.geo.multiPoint(multiPointList);

            // multiLine
            ArrayList<LineString> multiLineStringList = new ArrayList<>();
            multiLineStringList.add(db.geo.lineString(new ArrayList<Point>(Arrays.asList(randomPoint(), randomPoint()))));
            multiLineStringList.add(db.geo.lineString(new ArrayList<Point>(Arrays.asList(randomPoint(), randomPoint()))));
            MultiLineString multiLineString = new MultiLineString(multiLineStringList);

            // multiPolygon
            ArrayList<Polygon> multiPolygonList = new ArrayList<>();
            // polygon1
            ArrayList<LineString> lineList1 = new ArrayList<LineString>();
            lineList1.add(db.geo.lineString(new ArrayList<Point>(Arrays.asList(point1, point2,
                    point3, point4, point1))));
            multiPolygonList.add(db.geo.polygon(lineList1));
            // polygon2
            ArrayList<LineString> lineList2 = new ArrayList<LineString>();
            lineList2.add(db.geo.lineString(new ArrayList<Point>(Arrays.asList(point5, point6,
                    point7, point8, point5))));
            multiPolygonList.add(db.geo.polygon(lineList2));
            MultiPolygon multiPolygon = db.geo.multiPolygon(multiPolygonList);

            JSONObject doc = new JSONObject();
            doc.put("point", randomPoint());
            doc.put("geoNearPoint", geoNearPoint);
            doc.put("line", line);
            doc.put("polygon", polygon);
            doc.put("multiPoint", multiPoint);
            doc.put("multiLineString", multiLineString);
            doc.put("multiPolygon", multiPolygon);
            return doc;
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
        return null;
    }

    public static Point randomPoint() throws TcbException {
        return db.geo.point(180 - 360 * Math.random(), 90 - 180 * Math.random());
    }

    @Test
    public void geoCURD() {
        JSONObject result;
        try {
            // 添加文档
            result = db.collection("user").add(doc);
            docId = result.optString("id");
            assertFalse(docId.isEmpty());

            // 查询文档
            JSONObject query = new JSONObject();
            query.put("_id", docId);
            result = db.collection("user").where(query).get();
            Log.d("测试", result.toString());
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertFalse(requestId.isEmpty());
            assertNotNull(data);
            assertTrue(data.length() > 0);

            // 更新文档
            JSONObject updateData = new JSONObject();
            result = db.collection("user").doc(docId).set(doc);
            requestId = result.optString("requestId");
            int updated = result.optInt("updated");
            assertFalse(requestId.isEmpty());
            assertTrue(updated > 0);

            // 删除文档
            result = db.collection("user").doc(docId).remove();
            requestId = result.optString("requestId");
            int deleted = result.optInt("deleted");
            assertFalse(requestId.isEmpty());
            assertTrue(deleted > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    @Test
    public void geoNear() {
        JSONObject result;
        try {
            Point geoPoint = db.geo.point(22, 23);
            doc.put("geo", geoPoint);

            // 创建文档
            result = db.collection("user").add(doc);
            String id = result.optString("id");
            assertFalse(id.isEmpty());

            // 查询文档
            JSONObject query = new JSONObject();
            query.put("geo", db.command.geoNear(geoPoint, 1, 0));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertFalse(requestId.isEmpty());
            assertNotNull(data);
            assertTrue(data.length() > 0);

            // 删除文档
            result = db.collection("user").doc(id).remove();
            requestId = result.optString("requestId");
            int deleted = result.optInt("deleted");
            assertFalse(requestId.isEmpty());
            assertTrue(deleted > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }

    @Test
    public void geoWithin() {
        JSONObject result;

        try {
            Point geoPoint = db.geo.point(0, 0);
            doc.put("geo", geoPoint);

            // 创建文档
            result = db.collection("user").add(doc);
            String id = result.optString("id");
            assertFalse(id.isEmpty());

            // 查询文档
            // 创建一个范围
            Point point1 = db.geo.point(-2, -2);
            Point point2 = db.geo.point(2, -2);
            Point point3 = db.geo.point(2, 2);
            Point point4 = db.geo.point(-2, 2);
            ArrayList<LineString> lineList = new ArrayList<LineString>();
            lineList.add(db.geo.lineString(
                    new ArrayList<Point>(
                            Arrays.asList(
                                    point1, point2, point3, point4, point1
                            )
                    ))
            );
            // 查询条件
            JSONObject query = new JSONObject();
            query.put("geo", db.command.geoWithin(db.geo.polygon(lineList)));
            result = db.collection("user").where(query).get();
            String requestId = result.optString("requestId");
            JSONArray data = result.optJSONArray("data");
            assertFalse(requestId.isEmpty());
            assertNotNull(data);
            assertTrue(data.length() > 0);

            // 删除文档
            result = db.collection("user").doc(id).remove();
            requestId = result.optString("requestId");
            int deleted = result.optInt("deleted");
            assertFalse(requestId.isEmpty());
            assertTrue(deleted > 0);
        } catch (TcbException e) {
            fail(e.toString());
        } catch (JSONException e) {
            fail(e.toString());
        }
    }
}
