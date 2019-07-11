package com.tencent.tcb.database.Geo;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LineString {
    /**
     * 多个Point
     */
    public ArrayList<Point> points;

    public LineString(@NonNull ArrayList<Point> points) throws TcbException{
        if (points.size() < 2) {
            throw new TcbException(Code.INVALID_PARAM, "points must contain 2 points at least");
        }

        this.points = points;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray lineCoordinates = new JSONArray();
        for (Point point : this.points) {
            // 深拷贝
            JSONArray pointCoordinates = point.toJson().getJSONArray("coordinates");
            JSONArray clonePointCordinates = new JSONArray();
            clonePointCordinates.put(pointCoordinates.getDouble(0)).put(pointCoordinates.getDouble(1));
            lineCoordinates.put(clonePointCordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "LineString");
        result.put("coordinates", lineCoordinates);

        return result;
    }

    public static boolean validate(JSONObject lineJson) throws TcbException{
        if (!lineJson.has("type") || !lineJson.has("coordinates")) {
            return false;
        }

        try {
            if (!lineJson.get("type").equals("LineString")) {
                return false;
            }

            JSONArray lineCoordinates = lineJson.getJSONArray("coordinates");
            for (int i = 0; i < lineCoordinates.length(); i++) {
                JSONArray pointCoordinates = lineCoordinates.getJSONArray(i);
                if (!Validate.isGeopoint("longitude", pointCoordinates.getDouble(0))
                        || !Validate.isGeopoint("latitude", pointCoordinates.getDouble(1))) {
                    return false;
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public static boolean isClosed(LineString lineString) {
        Point firstPoint = lineString.points.get(0);
        Point lastPoint = lineString.points.get(lineString.points.size() - 1);

        return  (firstPoint.latitude == lastPoint.latitude && firstPoint.longitude == lastPoint.longitude);
    }
}