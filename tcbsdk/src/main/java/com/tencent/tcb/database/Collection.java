package com.tencent.tcb.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Collection extends Query{

    /**
     * 初始化
     *
     * @param db
     * @param collName
     */
    public Collection(Db db, String collName) {
        super(db, collName);
    }

    public Document doc(@Nullable String docID) {
        return new Document(this.db, this.collName, docID);
    }

    public Document add() {
        Document document = this.doc();
        document.create();
    }
}
