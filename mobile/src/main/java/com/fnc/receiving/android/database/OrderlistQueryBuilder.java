package com.fnc.receiving.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.receiving.android.enumeration.OrderKey;
import com.fnc.receiving.android.model.Order;

public class OrderlistQueryBuilder {
    public static ContentValues prepareOrderlistInsertValues(Order orders, Context context){
        ContentValues values = new ContentValues();
        values.put(OrderKey.QUANTITY.getKey(), orders.getQuantity());
        values.put(OrderKey.ITEM_RECID.getKey(), orders.getItemRecid());
        values.put(OrderKey.ITEM_NAME.getKey(), orders.getItemName());
        values.put(OrderKey.UNIT_NAME.getKey(), orders.getUnitName());
        values.put(OrderKey.REMARKS.getKey(), orders.getRemarks());
        values.put(OrderKey.OLD_SKU.getKey(), orders.getOldSku());
        values.put(OrderKey.SELLING_PRICE.getKey(), orders.getSellingPrice());
        return values;
    }
}

