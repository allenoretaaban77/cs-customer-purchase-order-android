package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.StoreListKey;
import com.fnc.order.android.model.MenuList;

public class MenulistQueryBuilder {
    public static ContentValues prepareMenulistInsertValues(MenuList menulist, Context context){
        ContentValues values = new ContentValues();
        values.put(MenulistKey.CUSTOMER_ID.getKey(), menulist.getCustomerID());
        values.put(MenulistKey.CUSTOMER_NAME.getKey(), menulist.getCustomerName());
        values.put(MenulistKey.REMARKS.getKey(), menulist.getRemarks());
        values.put(MenulistKey.RECORD_COUNT.getKey(), menulist.getRemarks());
        return values;
    }
}
