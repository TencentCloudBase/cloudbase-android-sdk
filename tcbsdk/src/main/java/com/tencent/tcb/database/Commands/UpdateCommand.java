package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateCommand {
    private ArrayList<ArrayList<Object>> actions;

    public JSONObject toJSON() throws JSONException {
        JSONArray actionArr = new JSONArray(this.actions);
        JSONObject actionMap = new JSONObject();
        actionMap.put("_actions", actionArr);

        return actionMap;
    }

    public UpdateCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        this.actions = actions;
        if (step.size() > 0) {
            this.actions.add(step);
        }
    }
}
