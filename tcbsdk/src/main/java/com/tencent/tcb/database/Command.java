package com.tencent.tcb.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.database.Commands.LogicCommand;
import com.tencent.tcb.database.Commands.QueryCommand;
import com.tencent.tcb.database.Commands.UpdateCommand;
import com.tencent.tcb.database.Utils.Format;

import java.util.ArrayList;
import java.util.Arrays;

public class Command {
    public LogicCommand eq(@NonNull Object val) {
        return this.queryOp("$eq", val);
    }

    public LogicCommand neq(@NonNull Object val) {
        return this.queryOp("$neq", val);
    }

    public LogicCommand gt(@NonNull Number val) {
        return this.queryOp("$gt", val);
    }

    public LogicCommand gte(@NonNull Number val) {
        return this.queryOp("$gte", val);
    }

    public LogicCommand lt(@NonNull Number val) {
        return this.queryOp("$lt", val);
    }

    public LogicCommand lte(@NonNull Number val) {
        return this.queryOp("$lte", val);
    }

    public LogicCommand in(@NonNull ArrayList<String> val) {
        return this.queryOp("$in", val);
    }

    public LogicCommand nin(@NonNull ArrayList<Number> val) {
        return this.queryOp("$nin", val);
    }

    public LogicCommand or (@NonNull LogicCommand... args) {
        return this.logicOp("$or", (ArrayList<LogicCommand>)Arrays.asList(args));
    }

    public LogicCommand or (@NonNull ArrayList<LogicCommand> args) {
        return this.logicOp("$or", args);
    }

    public LogicCommand and(@NonNull LogicCommand... args) {
        return this.logicOp("$and", (ArrayList<LogicCommand>)Arrays.asList(args));
    }

    public LogicCommand and (@NonNull ArrayList<LogicCommand> args) {
        return this.logicOp("$and", args);
    }

    public UpdateCommand set (@NonNull Object val) {
        return this.updateOp("$set", val);
    }

    public UpdateCommand remove () {
        return this.updateOp("$remove", null);
    }

    public UpdateCommand mul (@NonNull Object val) {
        return this.updateOp("$mul", val);
    }

    public UpdateCommand push (@NonNull Object val) {
        return this.updateOp("$push", val);
    }

    public UpdateCommand pop () {
        return this.updateOp("$pop", null);
    }

    public UpdateCommand shift () {
        return this.updateOp("$shift", null);
    }

    public UpdateCommand unshift (@NonNull Object val) {
        return this.updateOp("$unshift", val);
    }

    private LogicCommand queryOp(@NonNull String operation, @NonNull Object val) {
        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.add(Format.dataFormat(val));
        return new QueryCommand(new ArrayList<ArrayList<Object>>(), step);
    }

    private LogicCommand logicOp(@NonNull String operation, @NonNull ArrayList<LogicCommand> args) {
        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.addAll(Format.dataFormat(args));
        return new LogicCommand(new ArrayList<ArrayList<Object>>(), step);
    }

    private UpdateCommand updateOp(@NonNull String operation, @Nullable Object val) {
        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        if (val != null) {
            step.add(Format.dataFormat(val));
        }
        return new UpdateCommand(new ArrayList<ArrayList<Object>>(), step);
    }

}
