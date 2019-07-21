package com.tencent.tcb.database.Geos;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiPoint {

    public ArrayList<Point> points;

    public MultiPoint(ArrayList<Point> points) throws TcbException{
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
            JSONArray clonePointCordinates = new JSONArray();
            clonePointCordinates.put(pointCoordinates.getDouble(0)).put(pointCoordinates.getDouble(1));
            coordinates.put(clonePointCordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "LineString");
        result.put("coordinates", coordinates);

        return result;
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
