package com.tencent.tcb.database.ServerDate;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ServerDate {
    public int offset;

    public ServerDate(int offset) {
        this.offset = offset;
    }

    public JSONObject parse() throws JSONException {
        JSONObject date = new JSONObject();
        date.put("offset", this.offset);

        JSONObject result = new JSONObject();
        result.put("date", date);

        return result;
    }
}
