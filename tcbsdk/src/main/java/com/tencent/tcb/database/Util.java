package com.tencent.tcb.database;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Geos.LineString;
import com.tencent.tcb.database.Geos.MultiLineString;
import com.tencent.tcb.database.Geos.MultiPoint;
import com.tencent.tcb.database.Geos.MultiPolygon;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Geos.Polygon;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

public class Util {
    public static JSONArray formatResDocumentData(@NonNull JSONArray documents) throws TcbException {
        JSONArray formatDocuments = new JSONArray();
        try {
            for (int i = 0; i < documents.length(); i++) {
                JSONObject formatDocument = formatField((JSONObject) documents.get(i));
                formatDocuments.put(formatDocument);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
        return formatDocuments;
    }

    private static JSONArray formatField(JSONArray document) throws TcbException {
        JSONArray protoField = new JSONArray();
        try {
            for (int i = 0; i < document.length(); i++) {
                Object value = document.get(i);
                Object realValue = null;

                String type = whichType(value);

                switch (type) {
                    case "GeoPoint": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = Point.fromJson(data);
                        break;
                    }
                    case "GeoLineString": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = LineString.fromJson(data);
                        break;
                    }
                    case "GeoPolygon": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = Polygon.fromJson(data);
                        break;
                    }
                    case "GeoMultiPoint": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiPoint.fromJson(data);
                        break;
                    }
                    case "GeoMultiLineString": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiLineString.fromJson(data);
                        break;
                    }
                    case "GeoMultiPolygon": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiPolygon.fromJson(data);
                        break;
                    }
                    case "Timestamp": {
                        JSONObject jData = (JSONObject) value;
                        realValue = new Date(jData.getLong("$timestamp") * 1000);
                        break;
                    }
                    case "ServerDate": {
                        JSONObject jData = (JSONObject) value;
                        realValue = new Date(jData.getLong("$date"));
                        break;
                    }
                    case "JSONArray":
                        realValue = formatField((JSONArray) value);
                        break;
                    case "JSONObject":
                        realValue = formatField((JSONObject) value);
                        break;
                    default:
                        realValue = value;
                }

                protoField.put(i, realValue);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, "parse res data error");
        }

        return protoField;
    }

    private static JSONObject formatField(JSONObject document) throws TcbException {
        JSONObject protoField = new JSONObject();
        try {

            Iterator iterator = document.keys();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = document.get(key);
                Object realValue = null;

                String type = whichType(value);

                switch (type) {
                    case "GeoPoint": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = Point.fromJson(data);
                        break;
                    }
                    case "GeoLineString": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = LineString.fromJson(data);
                        break;
                    }
                    case "GeoPolygon": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = Polygon.fromJson(data);
                        break;
                    }
                    case "GeoMultiPoint": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiPoint.fromJson(data);
                        break;
                    }
                    case "GeoMultiLineString": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiLineString.fromJson(data);
                        break;
                    }
                    case "GeoMultiPolygon": {
                        JSONArray data = ((JSONObject) value).getJSONArray("coordinates");
                        realValue = MultiPolygon.fromJson(data);
                        break;
                    }
                    case "Timestamp": {
                        JSONObject jData = (JSONObject) value;
                        realValue = new Date(jData.getLong("$timestamp") * 1000);
                        break;
                    }
                    case "ServerDate": {
                        JSONObject jData = (JSONObject) value;
                        realValue = new Date(jData.getLong("$date"));
                        break;
                    }
                    case "JSONArray":
                        realValue = formatField((JSONArray) value);
                        break;
                    case "JSONObject":
                        realValue = formatField((JSONObject) value);
                        break;
                    default:
                        realValue = value;
                }

                protoField.put(key, realValue);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, "parse res data error");
        }

        return protoField;
    }

    public static String whichType(Object data) throws TcbException {
        if (data instanceof String) {
            return "String";
        }

        if (data instanceof Number) {
            return "Number";
        }

        if (data instanceof JSONArray) {
            return "JSONArray";
        }

        if (data instanceof JSONObject) {
            JSONObject jData = (JSONObject) data;

            if (!jData.optString("$timestamp").isEmpty()) {
                return "Date";
            }

            if (!jData.optString("$date").isEmpty()) {
                return "ServerDate";
            }

            if (Point.validate(jData)) {
                return "GeoPoint";
            }

            if (LineString.validate(jData)) {
                return "GeoLineString";
            }

            if (Polygon.validate(jData)) {
                return "GeoPolygon";
            }


            if (MultiPoint.validate(jData)) {
                return "GeoMultiPoint";
            }

            if (MultiLineString.validate(jData)) {
                return "GeoMultiLineString";
            }

            if (MultiPolygon.validate(jData)) {
                return "GeoMultiPolygon";
            }

            return "JSONObject";
        }

        return data.getClass().getName();
    }
}
