package com.tencent.tcb.database.Regexp;


import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

public class RegExp {
    private String regexp;
    private String options;

    public RegExp(String regexp, String options) throws TcbException {
        if (regexp.isEmpty()) {
            throw new TcbException(Code.INVALID_PARAM, "regexp must be a string");
        }

        this.regexp = regexp;
        this.options = options;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("$regex", regexp);
        data.put("$options", options);
        return data;
    }
}
