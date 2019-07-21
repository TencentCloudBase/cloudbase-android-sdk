package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LogicCommand {
    public ArrayList<ArrayList<Object>> actions;

    public LogicCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        this.actions = actions;
        if (step.size() > 0) {
            this.actions.add(step);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONArray actionArr = new JSONArray(this.actions);
        JSONObject actionMap = new JSONObject();
        actionMap.put("_actions", actionArr);

        return actionMap;
    }

    public LogicCommand or (@NonNull LogicCommand... args) throws TcbException {
        return this.logicOp("$or", new ArrayList<LogicCommand>(Arrays.asList(args)));
    }

    public LogicCommand or (@NonNull ArrayList<LogicCommand> args) throws TcbException {
        return this.logicOp("$or", args);
    }

    public LogicCommand and(@NonNull LogicCommand... args) throws TcbException {
        return this.logicOp("$and", new ArrayList<LogicCommand>(Arrays.asList(args)));
    }

    public LogicCommand and (@NonNull ArrayList<LogicCommand> args) throws TcbException {
        return this.logicOp("$and", args);
    }

    private LogicCommand logicOp(@NonNull String operation, @NonNull ArrayList<LogicCommand> commands) throws TcbException{
        // 格式化
        ArrayList<JSONObject> formatCommands = new ArrayList<>();
        try {
            formatCommands = Format.dataFormat(commands);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.addAll(formatCommands);
        return new LogicCommand(this.actions, step);
    }
}
