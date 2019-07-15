package com.tencent.tcb.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tcb.utils.TcbException;

import org.json.JSONObject;

import java.util.HashMap;

public class Collection extends Query {

    /**
     * 初始化
     *
     * @param db
     * @param collName
     */
    public Collection(Db db, String collName) {
        super(db, collName);
    }

    public Document doc() {
        return new Document(this.db, this.collName, null);
    }

    public Document doc(@NonNull String docID) {
        return new Document(this.db, this.collName, docID);
    }

    public JSONObject add(JSONObject data) throws TcbException {
        Document document = this.doc();
        return document.create(data);
    }
}
