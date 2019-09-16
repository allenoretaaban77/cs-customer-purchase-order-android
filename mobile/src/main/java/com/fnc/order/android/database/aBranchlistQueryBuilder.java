package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.model.aBranchlist;

public class aBranchlistQueryBuilder {
    public static ContentValues prepareaBranchlistInsertValues(aBranchlist abl, Context context){
        ContentValues values = new ContentValues();
        values.put(aBranchlistKey.BRANCHID.getKey(), abl.getBranchid());
        values.put(aBranchlistKey.BRANCHCODE.getKey(), abl.getBranchcode());
        values.put(aBranchlistKey.DEVICEID.getKey(), abl.getDeviceid());
        values.put(aBranchlistKey.DESCRIPTION.getKey(), abl.getDescription());
        return values;
    }
}
