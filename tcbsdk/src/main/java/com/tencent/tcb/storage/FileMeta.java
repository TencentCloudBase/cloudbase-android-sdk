package com.tencent.tcb.storage;

public class FileMeta {
    public String fileID;
    public String maxAge;
    public FileMeta(String fileID, String maxAge) {
        this.fileID = fileID;
        this.maxAge = maxAge;
    }
}
