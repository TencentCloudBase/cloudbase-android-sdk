package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.database.Utils.Format;

import java.util.ArrayList;
import java.util.Arrays;

public class LogicCommand {
    private ArrayList<ArrayList<Object>> actions;

    public LogicCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        this.actions = actions;
        if (step.size() > 0) {
            this.actions.add(step);
        }
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

    private LogicCommand logicOp(@NonNull String operation, @NonNull ArrayList<LogicCommand> args) {
        ArrayList<Object> step = new ArrayList<>();
        step.add(operation);
        step.addAll(args);
        return new LogicCommand(new ArrayList<ArrayList<Object>>(), step);
    }
}
