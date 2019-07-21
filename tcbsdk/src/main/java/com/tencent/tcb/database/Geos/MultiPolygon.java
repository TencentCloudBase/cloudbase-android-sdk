package com.tencent.tcb.database.Geos;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiPolygon {
    public ArrayList<Polygon> polygons;

    public MultiPolygon(@NonNull ArrayList<Polygon> polygons) throws TcbException{
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
                    JSONArray clonePointCordinates = new JSONArray();
                    clonePointCordinates.put(point.longitude).put(point.latitude);
                    cloneLineCoordinates.put(clonePointCordinates);
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
}
