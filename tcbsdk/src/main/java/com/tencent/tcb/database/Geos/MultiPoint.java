package com.tencent.tcb.database.Geos;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiPoint {

    public ArrayList<Point> points;

    public MultiPoint(ArrayList<Point> points) throws TcbException {
        if (points.size() == 0) {
            throw new TcbException(Code.INVALID_PARAM, "points must contain 1 point at least");
        }

        this.points = points;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();
        // 深拷贝
        for (Point point : this.points) {
            JSONArray pointCoordinates = point.toJSON().getJSONArray("coordinates");
            JSONArray clonePointCoordinates = new JSONArray();
            clonePointCoordinates.put(pointCoordinates.getDouble(0)).put(pointCoordinates.getDouble(1));
            coordinates.put(clonePointCoordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "LineString");
        result.put("coordinates", coordinates);

        return result;
    }

    public static MultiPoint fromJson(JSONArray coordinates) throws TcbException {
        try {
            ArrayList<Point> points = new ArrayList<>();
            for(int i =0;i < coordinates.length(); i ++) {
                points.add(Point.fromJson(coordinates.getJSONArray(i)));
            }
            return new MultiPoint(points);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, "Parse MultiPoint Error. Invalid Data");
        }
    }

    public static boolean validate(JSONObject multiPointJson) throws TcbException {
        if (!multiPointJson.has("type") || !multiPointJson.has("coordinates")) {
            return false;
        }

        try {
            if (!multiPointJson.get("type").equals("MultiPoint")) {
                return false;
            }

            JSONArray coordinates = multiPointJson.getJSONArray("coordinates");
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray pointCoordinates = coordinates.getJSONArray(i);
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
}
