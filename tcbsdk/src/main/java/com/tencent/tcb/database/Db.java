package com.tencent.tcb.database;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Regexp.RegExp;
import com.tencent.tcb.database.ServerDate.ServerDate;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class Db {
    public Config config;
    public Command command;

    public Db(Config config) {
        this.config = config;
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

    // todo: Point, MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon

    public Collection collection(String collName) throws TcbException {
        if (collName == null || collName.isEmpty()) {
            throw new TcbException(Code.EMPTY_PARAM, "Collection name is required");
        }

        return  new Collection(this, collName);
    }

    public JSONObject createCollection(String collName) throws TcbException,JSONException, IOException {
        if (collName == null || collName.isEmpty()) {
            throw new TcbException(Code.EMPTY_PARAM, "Collection name is required");
        }

        Request request = new Request(this.config);
        HashMap<String, Object> params = new HashMap<>();
        params.put("clooectionName", collName);
        JSONObject result = request.send("database.addCollection", params);

        return result;
    }


}
