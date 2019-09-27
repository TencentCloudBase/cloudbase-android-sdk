package com.tencent.tcb.database.Geos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiLineString {
    public ArrayList<LineString> lines;

    public MultiLineString(ArrayList<LineString> lines) throws TcbException {
        if (lines.size() == 0) {
            throw new TcbException(Code.INVALID_PARAM, "Polygon must contain 1 linestring at least");
        }

        this.lines = lines;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();
        // 深拷贝
        for (LineString line : this.lines) {
            JSONArray lineCoordinates = line.toJSON().getJSONArray("coordinates");
            JSONArray cloneLineCoordinates = new JSONArray();
            for (int i = 0; i < lineCoordinates.length(); i++) {
                JSONArray pointCoordinates = lineCoordinates.getJSONArray(i);
                JSONArray clonePointCoordinates = new JSONArray();
                clonePointCoordinates.put(pointCoordinates.getDouble(0)).put(pointCoordinates.getDouble(1));
                cloneLineCoordinates.put(clonePointCoordinates);
            }
            coordinates.put(cloneLineCoordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "MultiLineString");
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

    public static MultiLineString fromJson(JSONArray coordinates) throws TcbException {
        try {
            ArrayList<LineString> lines = new ArrayList<>();
            for (int i = 0; i < coordinates.length(); i++) {
                lines.add(LineString.fromJson(coordinates.getJSONArray(i)));
            }
            return new MultiLineString(lines);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, "Parse MultiLineString Error. Invalid Data");
        }
    }

    public static boolean validate(JSONObject multiLineJson) throws TcbException {
        if (!multiLineJson.has("type") || !multiLineJson.has("coordinates")) {
            return false;
        }

        try {
            if (!multiLineJson.get("type").equals("MultiLineString")) {
                return false;
            }

            JSONArray coordinates = multiLineJson.getJSONArray("coordinates");
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
}
