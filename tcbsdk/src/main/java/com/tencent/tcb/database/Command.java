package com.tencent.tcb.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Commands.LogicCommand;
import com.tencent.tcb.database.Commands.QueryCommand;
import com.tencent.tcb.database.Commands.UpdateCommand;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Command {
    public QueryCommand eq(@NonNull Object val) throws TcbException {
        return this.queryOp("$eq", val);
    }

    public QueryCommand neq(@NonNull Object val) throws TcbException {
        return this.queryOp("$neq", val);
    }

    public QueryCommand gt(@NonNull Number val) throws TcbException {
        return this.queryOp("$gt", val);
    }

    public QueryCommand gte(@NonNull Number val) throws TcbException {
        return this.queryOp("$gte", val);
    }

    public QueryCommand lt(@NonNull Number val) throws TcbException {
        return this.queryOp("$lt", val);
    }

    public QueryCommand lte(@NonNull Number val) throws TcbException {
        return this.queryOp("$lte", val);
    }

    public QueryCommand in(@NonNull ArrayList<Object> val) throws TcbException {
        return this.queryOp("$in", val);
    }

    public QueryCommand nin(@NonNull ArrayList<Object> val) throws TcbException {
        return this.queryOp("$nin", val);
    }

    public QueryCommand geoNear(
            @NonNull Point point,
            @Nullable Number maxDistance,
            @Nullable Number minDistance
    ) throws TcbException {
        JSONObject resultGeometry = new JSONObject();
        try {
            resultGeometry.put("geometry", point);
            if (maxDistance != null) {
                resultGeometry.put("maxDistance", maxDistance);
            }
            if (minDistance != null) {
                resultGeometry.put("minDistance", minDistance);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        return this.queryOp("$geoNear", resultGeometry);
    }

    public QueryCommand geoWithin(@Nullable Object geometry) throws TcbException {
        JSONObject resultGeometry = new JSONObject();
        try {
            resultGeometry.put("geometry", geometry);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        return this.queryOp("$geoWithin", resultGeometry);
    }

    private QueryCommand geoIntersects(@Nullable Object geometry) throws TcbException {
        JSONObject resultGeometry = new JSONObject();
        try {
            resultGeometry.put("geometry", geometry);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        return this.queryOp("$geoIntersects", resultGeometry);
    }

    public LogicCommand or(@NonNull Object... args) throws TcbException {
        Object firstArg = args[0];
        if (firstArg instanceof ArrayList) {
            ArrayList<Object> newArgs = new ArrayList<>();
            for (Object obj : (ArrayList) firstArg) {
                newArgs.add(obj);
            }
            return this.logicOp("$or", newArgs);
        }
        return this.logicOp("$or", new ArrayList<>(Arrays.asList(args)));
    }

    public LogicCommand and(@NonNull Object... args) throws TcbException {
        Object firstArg = args[0];
        if (firstArg instanceof ArrayList) {
            ArrayList<Object> newArgs = new ArrayList<>();
            for (Object obj : (ArrayList) firstArg) {
                newArgs.add(obj);
            }
            return this.logicOp("$and", newArgs);
        }
        return this.logicOp("$and", new ArrayList<>(Arrays.asList(args)));
    }

    public UpdateCommand set(@NonNull Object val) throws TcbException {
        return this.updateOp("$set", val);
    }

    public UpdateCommand remove() throws TcbException {
        return this.updateOp("$remove", null);
    }

    public UpdateCommand inc(@NonNull Object val) throws TcbException {
        return this.updateOp("$inc", val);
    }

    public UpdateCommand mul(@NonNull Object val) throws TcbException {
        return this.updateOp("$mul", val);
    }

    public UpdateCommand push(@NonNull ArrayList<Object> val) throws TcbException {
        return this.updateOp("$push", val);
    }

    public UpdateCommand pop() throws TcbException {
        return this.updateOp("$pop", null);
    }

    public UpdateCommand shift() throws TcbException {
        return this.updateOp("$shift", null);
    }

    public UpdateCommand unshift(@NonNull ArrayList<Object> val) throws TcbException {
        return this.updateOp("$unshift", val);
    }

    private QueryCommand queryOp(@NonNull String operation, @NonNull Object val) throws TcbException {
        // 格式化
        try {
            val = Format.dataFormat(val);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.add(val);
        return new QueryCommand(new ArrayList<ArrayList<Object>>(), step);
    }

    // commands 为 JSONObject 或 LogicCommand
    private LogicCommand logicOp(@NonNull String operation, @NonNull ArrayList<Object> commands) throws TcbException {
        // 格式化
        ArrayList<Object> formatCommands = new ArrayList<>();
        try {
            formatCommands = Format.dataFormat(commands);
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.addAll(formatCommands);
        return new LogicCommand(new ArrayList<ArrayList<Object>>(), step);
    }

    private UpdateCommand updateOp(@NonNull String operation, @Nullable Object val) throws TcbException {
        // 格式化
        try {
            if (val != null) {
                val = Format.dataFormat(val);
            }
        } catch (JSONException e) {
            throw new TcbException(Code.JSON_ERR, e.getMessage());
        }

        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        if (val != null) {
            step.add(val);
        }
        return new UpdateCommand(new ArrayList<ArrayList<Object>>(), step);
    }

}
