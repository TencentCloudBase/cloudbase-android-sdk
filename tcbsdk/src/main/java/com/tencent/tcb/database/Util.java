package com.tencent.tcb.database;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Util {
    public static ArrayList<JSONObject> formatResDocumentData(@NonNull JSONArray documents) throws TcbException {
        ArrayList<JSONObject> formatDocuments = new ArrayList<>();
        try {
            for (int i = 0; i < documents.length(); i++) {
                JSONObject formatDocument = formatField((JSONObject) documents.get(i));
                formatDocuments.add(formatDocument);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }
        return formatDocuments;
    }

    private static JSONObject formatField(JSONObject document) {
        return  document;
    }
}
