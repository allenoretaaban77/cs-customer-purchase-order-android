package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.ReturnKey;
import com.fnc.order.android.model.Return;

public class ReturnlistQueryBuilder {
    public static ContentValues prepareReturnlistInsertValues(Return returns, Context context){
        ContentValues values = new ContentValues();
        values.put(ReturnKey.QUANTITY.getKey(), returns.getQuantity());
        values.put(ReturnKey.ITEM_RECID.getKey(), returns.getItemRecid());
        values.put(ReturnKey.ITEM_NAME.getKey(), returns.getItemName());
        values.put(ReturnKey.UNIT_NAME.getKey(), returns.getUnitName());
        values.put(ReturnKey.REMARKS.getKey(), returns.getRemarks());
        return values;
    }
}

