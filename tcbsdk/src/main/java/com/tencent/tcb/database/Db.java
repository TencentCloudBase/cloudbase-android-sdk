package com.tencent.tcb.database;

import android.content.Context;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Geo.LineString;
import com.tencent.tcb.database.Geo.MultiLineString;
import com.tencent.tcb.database.Geo.MultiPoint;
import com.tencent.tcb.database.Geo.MultiPolygon;
import com.tencent.tcb.database.Geo.Point;
import com.tencent.tcb.database.Geo.Polygon;
import com.tencent.tcb.database.Regexp.RegExp;
import com.tencent.tcb.database.ServerDate.ServerDate;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Db {
    public Config config;
    public Context context;
    public Command command;

    public Db(Context context, Config config) {
        this.config = config;
        this.context = context;
        this.command = new Command();
    }

    public ServerDate serverDate() {
        return serverData(0);
    }

    public ServerDate serverData(int offset) {
        return new ServerDate(offset);
    }

    public RegExp regExp(String regexp) {
        return regExp(regexp, "");
    }

    public RegExp regExp(String regexp, String options) {
        return new RegExp(regexp, options);
    }

    public Point point(double longitude, long latitude) throws TcbException {
        return new Point(longitude, latitude);
    }

    public MultiPoint multiPoint(ArrayList<Point> points) throws TcbException {
        return new MultiPoint(points);
    }

    public LineString lineString(ArrayList<Point> points) throws TcbException {
        return new LineString(points);
    }

    public MultiLineString multiLineString(ArrayList<LineString> lines) throws TcbException {
        return new MultiLineString(lines);
    }

    public Polygon polygon (ArrayList<LineString> lines) throws TcbException {
        return new Polygon(lines);
    }

    public MultiPolygon multiPolygon (ArrayList<Polygon> polygons) throws TcbException {
        return new MultiPolygon(polygons);
    }

    public Collection collection(String collName) throws TcbException {
        if (collName == null || collName.isEmpty()) {
            throw new TcbException(Code.EMPTY_PARAM, "Collection name is required");
        }

        return  new Collection(this, collName);
    }

    public JSONObject createCollection(String collName) throws TcbException {
        if (collName == null || collName.isEmpty()) {
            throw new TcbException(Code.EMPTY_PARAM, "Collection name is required");
        }

        Request request = new Request(this.config, this.context);
        HashMap<String, Object> params = new HashMap<>();
        params.put("collectionName", collName);
        JSONObject result = request.send("database.addCollection", params);

        return result;
    }


}
