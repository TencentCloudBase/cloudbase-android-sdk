package com.tencent.tcb.database.Utils;

import android.icu.text.Edits;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Commands.LogicCommand;
import com.tencent.tcb.database.Geos.LineString;
import com.tencent.tcb.database.Geos.MultiLineString;
import com.tencent.tcb.database.Geos.MultiPoint;
import com.tencent.tcb.database.Geos.MultiPolygon;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Geos.Polygon;
import com.tencent.tcb.database.ServerDate.ServerDate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Format {

    public static Object dataFormat(@NonNull Object data) throws JSONException {
        if (data instanceof LogicCommand) {
            return ((LogicCommand)data).toJSON();
        }

        if (data instanceof ServerDate) {
            return ((ServerDate)data).parse();
        }

        if (data instanceof Point) {
            return ((Point)data).toJSON();
        }

        if (data instanceof LineString) {
            return ((LineString)data).toJSON();
        }

        if (data instanceof Polygon) {
            return ((Polygon)data).toJSON();
        }

        if (data instanceof MultiPoint) {
            return ((MultiPoint)data).toJSON();
        }

        if (data instanceof MultiLineString) {
            return ((MultiLineString)data).toJSON();
        }

        if (data instanceof MultiPolygon) {
            return ((MultiPolygon)data).toJSON();
        }

        if (data instanceof JSONObject) {
            return Format.dataFormat((JSONObject)data);
        }

        return data;
    }

    public static JSONObject dataFormat(@NonNull JSONObject data) throws JSONException {
        JSONObject cloneData = new JSONObject();
        Iterator iterator = data.keys();
        while (iterator.hasNext()) {
            String key = (String)iterator.next();
            Object value = data.get(key);
            cloneData.put(key, Format.dataFormat(value));
        }
        return cloneData;
    }

    public static ArrayList<JSONObject> dataFormat(@NonNull ArrayList<LogicCommand> data) throws JSONException {
        ArrayList<JSONObject> cloneData = new ArrayList<>();
        for (LogicCommand cmd : data) {
            cloneData.add(cmd.toJSON());
        }
        return cloneData;
    }
}
