package com.tencent.tcb.database;

import androidx.annotation.NonNull;

import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbListener;

import org.json.JSONObject;

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

    public JSONObject add(@NonNull JSONObject data) throws TcbException {
        Document document = this.doc();
        return document.create(data);
    }

    public void addAsync(@NonNull final JSONObject data, @NonNull final TcbListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = doc().create(data);
                    listener.onSuccess(res);
                } catch (TcbException e) {
                    listener.onFailed(e);
                }
            }
        }).start();
    }
}
