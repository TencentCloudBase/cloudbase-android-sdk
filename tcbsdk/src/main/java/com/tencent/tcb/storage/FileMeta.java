package com.tencent.tcb.storage;

public class FileMeta {
    public String fileID;
    public int maxAge;
    public FileMeta(String fileID, int maxAge) {
        this.fileID = fileID;
        this.maxAge = maxAge;
    }
}
