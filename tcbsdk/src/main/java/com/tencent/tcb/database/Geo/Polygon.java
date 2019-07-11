package com.tencent.tcb.database.Geo;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 地理位置
 */
public class Polygon {
    /**
     * 多个Point
     */
    public ArrayList<LineString> lines;

    public Polygon(@NonNull ArrayList<LineString> lines) throws TcbException {
        if (lines.size() == 0) {
            throw new TcbException(Code.INVALID_PARAM, "Polygon must contain 1 linestring at least");
        }

        for (LineString line : lines) {
            if (!LineString.isClosed(line)) {
                StringBuilder sb = new StringBuilder();
                for (Point point : line.points) {
                    sb.append(point.toReadableString()).append(" ");
                }
                throw new TcbException(Code.INVALID_PARAM, "LineString " + sb.toString() + "is not a closed cycle");
            }
        }

        this.lines = lines;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();
        // 深拷贝
        for (LineString line : this.lines) {
            JSONArray lineCoordinates = line.toJSON().getJSONArray("coordinates");
            JSONArray cloneLineCoordinates = new JSONArray();
            for(int i = 0; i < lineCoordinates.length(); i++) {
                JSONArray pointCoordinates = lineCoordinates.getJSONArray(i);
                JSONArray clonePointCordinates = new JSONArray();
                clonePointCordinates.put(pointCoordinates.getDouble(0)).put(pointCoordinates.getDouble(1));
                cloneLineCoordinates.put(clonePointCordinates);
            }
            coordinates.put(cloneLineCoordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "Polygon");
        result.put("coordinates", coordinates);

        return result;
    }

    public static boolean validate(JSONObject polygonJson) throws TcbException{
        if (!polygonJson.has("type") || !polygonJson.has("coordinates")) {
            return false;
        }

        try {
            if (!polygonJson.get("type").equals("Polygon")) {
                return false;
            }

            JSONArray coordinates = polygonJson.getJSONArray("coordinates");
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray lineCoordinates = coordinates.getJSONArray(i);
                for (int j = 0; j < lineCoordinates.length(); j++) {
                    JSONArray pointCoordinates = lineCoordinates.getJSONArray(j);
                    if (!Validate.isGeopoint("longitude", pointCoordinates.getDouble(0))
                            || !Validate.isGeopoint("latitude", pointCoordinates.getDouble(1))) {
                        return false;
                    }
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public static boolean isCloseLineString(LineString lineString) {
        Point firstPoint = lineString.points.get(0);
        Point lastPoint = lineString.points.get(lineString.points.size() - 1);

        return  (firstPoint.latitude == lastPoint.latitude && firstPoint.longitude == lastPoint.longitude);
    }
}
