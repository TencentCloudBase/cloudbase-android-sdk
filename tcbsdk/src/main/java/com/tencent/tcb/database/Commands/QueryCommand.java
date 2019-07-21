package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Utils.Format;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;

import java.util.ArrayList;

public class QueryCommand extends LogicCommand{
    public QueryCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        super(actions, step);
    }

    public LogicCommand eq(Object val) throws TcbException {
        return this.queryOp("$eq", val);
    }

    public LogicCommand neq(Object val) throws TcbException {
        return this.queryOp("$neq", val);
    }

    public LogicCommand gt(Number val) throws TcbException {
        return this.queryOp("$gt", val);
    }

    public LogicCommand gte(Number val) throws TcbException {
        return this.queryOp("$gte", val);
    }

    public LogicCommand lt(Number val) throws TcbException {
        return this.queryOp("$lt", val);
    }

    public LogicCommand lte(Number val) throws TcbException {
        return this.queryOp("$lte", val);
    }

    public LogicCommand in(ArrayList<String> val) throws TcbException {
        return this.queryOp("$in", val);
    }

    public LogicCommand nin(ArrayList<Number> val) throws TcbException {
        return this.queryOp("$nin", val);
    }

    private LogicCommand queryOp(String operation, Object val) throws TcbException {
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
        step.add(val);
        return this.and(new QueryCommand(new ArrayList<ArrayList<Object>>(), step));
    }


}
