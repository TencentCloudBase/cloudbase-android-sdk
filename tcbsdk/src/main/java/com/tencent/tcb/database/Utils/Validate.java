package com.tencent.tcb.database.Utils;

import com.tencent.tcb.constants.Code;
import com.tencent.tcb.database.Constants;
import com.tencent.tcb.utils.TcbException;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Validate {

    public static boolean isFieldPath(String path) throws TcbException {
        if(!Pattern.matches("^[a-zA-Z0-9-_\\.]+", path)) {
            throw new TcbException(Code.INVALID_FIELD_PATH, "字段地址不合法");
        }
        return true;
    }

    public static boolean isFieldOrder(String direction) throws TcbException {
        if(!Arrays.asList(Constants.ORDER_DIRECTION_LIST).contains(direction.toLowerCase())) {
            throw new TcbException(Code.INVALID_FIELD_PATH, "排序字符不合法");
        }
        return true;
    }

    public static boolean isGeopoint(String type, double degree) throws TcbException {
        double degreeAbs = Math.abs(degree);

        if (type.equals("latitude") && degree > 90.0) {
            throw new TcbException(Code.INVALID_PARAM, "latitude should be a number ranges from -90 to 90");
        } else if (type.equals("longitude") && degreeAbs > 180.0) {
            throw new TcbException(Code.INVALID_PARAM, "longitude should be a number ranges from -180 to 180");
        }

        return true;
    }
}
