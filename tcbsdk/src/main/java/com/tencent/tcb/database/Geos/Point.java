package com.tencent.tcb.database.Geos;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 地理位置
 */
public class Point {
    /**
     * 纬度
     * [-90, 90]
     */
    public double longitude;

    /**
     * 经度
     * [-180, 180]
     */
    public double latitude;

    public Point(double longitude, double latitude) throws TcbException {
        Validate.isGeopoint("longitude", longitude);
        Validate.isGeopoint("latitude", latitude);

        this.longitude = longitude;
        this.latitude = latitude;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();
        coordinates.put(this.longitude);
        coordinates.put(this.latitude);

        JSONObject result = new JSONObject();
        result.put("type", "Point");
        result.put("coordinates", coordinates);

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return this.toJSON().toString();
        } catch (JSONException e) {
            return "Invalid Geo Data";
        }
    }

    public String toReadableString() {
        return "[ " + this.longitude + " " + this.latitude + " ]";
    }


    public static Point fromJson(JSONArray coordinates) throws TcbException {
        return new Point(coordinates.optDouble(0), coordinates.optDouble(1));
    }

    public static boolean validate(JSONObject pointJson) throws TcbException {
        if (!pointJson.has("type") || !pointJson.has("coordinates")) {
            return false;
        }

        try {
            if (!pointJson.get("type").equals("Point")) {
                return false;
            }

            JSONArray coordinates = pointJson.getJSONArray("coordinates");
            if (!Validate.isGeopoint("longitude", coordinates.getDouble(0))
                    || !Validate.isGeopoint("latitude", coordinates.getDouble(1))) {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

}
