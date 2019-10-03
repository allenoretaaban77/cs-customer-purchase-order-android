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
        values.put(aBranchlistKey.DEVICEID1.getKey(), abl.getDeviceID1());
        values.put(aBranchlistKey.ACTIVE.getKey(), abl.getActive());
        values.put(aBranchlistKey.CUSTOMERID.getKey(), abl.getCustomerID());
        values.put(aBranchlistKey.OLDBRANCHID.getKey(), abl.getOld_branchid());
        values.put(aBranchlistKey.OLDCUSTOMERID.getKey(), abl.getOld_customerid());
        return values;
    }
}
