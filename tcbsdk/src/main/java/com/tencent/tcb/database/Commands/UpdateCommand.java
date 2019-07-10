package com.tencent.tcb.database.Commands;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UpdateCommand {
    private ArrayList<ArrayList<Object>> actions;

    public UpdateCommand(@NonNull ArrayList<ArrayList<Object>> actions, @NonNull ArrayList<Object> step) {
        this.actions = actions;
        if (step.size() > 0) {
            this.actions.add(step);
        }
    }
}
