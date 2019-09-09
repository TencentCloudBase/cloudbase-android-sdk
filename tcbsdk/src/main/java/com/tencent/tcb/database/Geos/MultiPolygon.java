package com.tencent.tcb.database.Geos;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Validate;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiPolygon {
    public ArrayList<Polygon> polygons;

    public MultiPolygon(@NonNull ArrayList<Polygon> polygons) throws TcbException {
        if (polygons.size() == 0) {
            throw new TcbException(Code.INVALID_PARAM, "MultiPolygon must contain 1 polygon at least");
        }

        this.polygons = polygons;
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray coordinates = new JSONArray();
        // 深拷贝
        for (Polygon polygon : this.polygons) {
            JSONArray clonePolygonCoordinates = new JSONArray();
            for (LineString line : polygon.lines) {
                JSONArray cloneLineCoordinates = new JSONArray();
                for (Point point : line.points) {
                    JSONArray clonePointCoordinates = new JSONArray();
                    clonePointCoordinates.put(point.longitude).put(point.latitude);
                    cloneLineCoordinates.put(clonePointCoordinates);
                }
                clonePolygonCoordinates.put(cloneLineCoordinates);
            }
            coordinates.put(clonePolygonCoordinates);
        }

        JSONObject result = new JSONObject();
        result.put("type", "MultiPolygon");
        result.put("coordinates", coordinates);

        return result;
    }

    public static MultiPolygon fromJson(JSONArray coordinates) throws TcbException {
        try {
            ArrayList<Polygon> polygons = new ArrayList<>();
            for (int i = 0; i < coordinates.length(); i++) {
                polygons.add(Polygon.fromJson(coordinates.getJSONArray(i)));
            }
            return new MultiPolygon(polygons);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, "Parse MultiPolygon Error. Invalid Data");
        }
    }


    public static boolean validate(JSONObject multiPolygonJson) throws TcbException {
        if (!multiPolygonJson.has("type") || !multiPolygonJson.has("coordinates")) {
            return false;
        }

        try {
            if (!multiPolygonJson.get("type").equals("MultiPolygon")) {
                return false;
            }

            JSONArray coordinates = multiPolygonJson.getJSONArray("coordinates");

            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray polygonCoordinates = coordinates.getJSONArray(i);
                for (int j = 0; j < polygonCoordinates.length(); j++) {
                    JSONArray lineCoordinates = polygonCoordinates.getJSONArray(j);
                    for (int k = 0; k < lineCoordinates.length(); k++) {
                        JSONArray pointCoordinates = lineCoordinates.getJSONArray(k);
                        if (!Validate.isGeopoint("longitude", pointCoordinates.getDouble(0)) ||
                                !Validate.isGeopoint("latitude", pointCoordinates.getDouble(1))) {
                            return false;
                        }
                    }
                }

            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }
}
