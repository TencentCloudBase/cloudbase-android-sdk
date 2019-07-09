package com.tencent.tcb.database;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Constants;
import com.tencent.tcb.utils.TcbException;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Validate {

    public static boolean isFieldPath(String path) throws TcbException {
        if(!Pattern.matches("^[a-zA-Z0-9-_\\.]", path)) {
            throw new TcbException(Code.INVALID_FIELD_PATH, "字段地址不合法");
        }
        return true;
    }

    public static boolean isFieldOrder(String direction) throws TcbException {
        if(!Arrays.asList(Constants.ORDER_DIRECTION_LIST).contains(direction)) {
            throw new TcbException(Code.INVALID_FIELD_PATH, "排序字符不合法");
        }
        return true;
    }
}
