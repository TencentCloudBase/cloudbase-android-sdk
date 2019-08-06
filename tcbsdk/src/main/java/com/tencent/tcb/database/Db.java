package com.tencent.tcb.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Regexp.RegExp;
import com.tencent.tcb.database.ServerDate.ServerDate;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

public class Db {
    public Config config;
    public Context context;
    public Command command;
    public Geo geo;

    public Db(String envName, Context context) {
        this.config = new Config(envName);
        this.context = context;
        this.command = new Command();
        this.geo = new Geo();
    }

    public ServerDate serverDate() {
        return serverData(0);
    }

    public ServerDate serverData(int offset) {
        return new ServerDate(offset);
    }

    public RegExp regExp(String regexp) throws TcbException {
        return regExp(regexp, "");
    }

    public RegExp regExp(String regexp, String options) throws TcbException {
        return new RegExp(regexp, options);
    }

    public Collection collection(@NonNull String collName) throws TcbException {
        if (collName.isEmpty()) {
            throw new TcbException(Code.EMPTY_PARAM, "Collection name is required");
        }

        return new Collection(this, collName);
    }
}
