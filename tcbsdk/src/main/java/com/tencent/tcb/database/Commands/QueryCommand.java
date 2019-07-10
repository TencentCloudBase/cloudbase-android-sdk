package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QueryCommand extends LogicCommand{
    public QueryCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        super(actions, step);
    }

    public LogicCommand eq(Object val) {
        return this.queryOp("$eq", val);
    }

    public LogicCommand neq(Object val) {
        return this.queryOp("$neq", val);
    }

    public LogicCommand gt(Number val) {
        return this.queryOp("$gt", val);
    }

    public LogicCommand gte(Number val) {
        return this.queryOp("$gte", val);
    }

    public LogicCommand lt(Number val) {
        return this.queryOp("$lt", val);
    }

    public LogicCommand lte(Number val) {
        return this.queryOp("$lte", val);
    }

    public LogicCommand in(ArrayList<String> val) {
        return this.queryOp("$in", val);
    }

    public LogicCommand nin(ArrayList<Number> val) {
        return this.queryOp("$nin", val);
    }

    private LogicCommand queryOp(String operation, Object val) {
        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.add(val);
        return this.and(new QueryCommand(new ArrayList<ArrayList<Object>>(), step));
    }


}
