package com.fnc.order.android.database;


import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.PreparedlistKey;
import com.fnc.order.android.model.Preparedlist;

public class PreparedlistQueryBuilder {

    public static ContentValues preparePreparedlistInsertValues(Preparedlist checklist, Context context){
        ContentValues values = new ContentValues();
        values.put(PreparedlistKey.RECID.getKey(), checklist.getRecid());
        values.put(PreparedlistKey.CHECKLIST_RECID.getKey(), checklist.getChecklistRecid());
        values.put(PreparedlistKey.CUSTOMER_ID.getKey(), checklist.getCustomerId());
        values.put(PreparedlistKey.CUSTOMER_NAME.getKey(), checklist.getCustomerName());
        values.put(PreparedlistKey.DEPT.getKey(), checklist.getDept());
        values.put(PreparedlistKey.REMARKS.getKey(), checklist.getRemarks());
        values.put(PreparedlistKey.INORDER.getKey(), checklist.getInorder());
        values.put(PreparedlistKey.STATUS.getKey(), checklist.getStatus());
        values.put(PreparedlistKey.JSON.getKey(), checklist.getJson());
        return values;
    }
}
