package com.tencent.tcb.database.Utils;

import androidx.annotation.NonNull;

import com.tencent.tcb.database.Commands.LogicCommand;
import com.tencent.tcb.database.Commands.UpdateCommand;
import com.tencent.tcb.database.Geos.LineString;
import com.tencent.tcb.database.Geos.MultiLineString;
import com.tencent.tcb.database.Geos.MultiPoint;
import com.tencent.tcb.database.Geos.MultiPolygon;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Geos.Polygon;
import com.tencent.tcb.database.Regexp.RegExp;
import com.tencent.tcb.database.ServerDate.ServerDate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

public class Format {

    public static Object dataFormat(@NonNull Object data) throws JSONException {
        if (data instanceof LogicCommand) {
            return ((LogicCommand) data).toJSON();
        }

        if (data instanceof UpdateCommand) {
            return ((UpdateCommand) data).toJSON();
        }

        if (data instanceof ServerDate) {
            return ((ServerDate) data).parse();
        }

        if (data instanceof Date) {
            JSONObject result = new JSONObject();
            result.put("$date", ((Date) data).getTime());
            return result;
        }

        if (data instanceof RegExp) {
            return ((RegExp) data).toJSON();
        }

        if (data instanceof Point) {
            return ((Point) data).toJSON();
        }

        if (data instanceof LineString) {
            return ((LineString) data).toJSON();
        }

        if (data instanceof Polygon) {
            return ((Polygon) data).toJSON();
        }

        if (data instanceof MultiPoint) {
            return ((MultiPoint) data).toJSON();
        }

        if (data instanceof MultiLineString) {
            return ((MultiLineString) data).toJSON();
        }

        if (data instanceof MultiPolygon) {
            return ((MultiPolygon) data).toJSON();
        }

        if (data instanceof JSONObject) {
            return Format.dataFormat((JSONObject) data);
        }

        return data;
    }

    public static JSONObject dataFormat(@NonNull JSONObject data) throws JSONException {
        JSONObject cloneData = new JSONObject();
        Iterator iterator = data.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = data.get(key);
            cloneData.put(key, Format.dataFormat(value));
        }
        return cloneData;
    }

    public static ArrayList<JSONObject> dataFormat(@NonNull ArrayList<Object> data) throws JSONException, TcbException {
        ArrayList<JSONObject> cloneData = new ArrayList<>();
        for (Object cmd : data) {
            if (cmd instanceof JSONObject) {
                cloneData.add((JSONObject) dataFormat(cmd));
            } else if (cmd instanceof LogicCommand) {
                cloneData.add((JSONObject) dataFormat(cmd));
            } else {
                throw new TcbException("TYPE_ERROR", "操作符类型只能为 JSONObject 或 LogicCommand");
            }
        }
        return cloneData;
    }
}
