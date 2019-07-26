package com.tencent.tcb;

import android.content.Context;

import com.tencent.tcb.database.Db;
import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.storage.StorageService;

public class TCB {
    public FunctionService function;
    public StorageService storage;
    public Db db;

    public TCB(String envName, Context context) {
        this.function = new FunctionService(envName, context);
        this.storage = new StorageService(envName, context);
        this.db = new Db(envName, context);
    }
}
