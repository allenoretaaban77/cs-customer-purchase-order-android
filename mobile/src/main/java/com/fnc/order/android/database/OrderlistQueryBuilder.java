package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.model.Order;

public class OrderlistQueryBuilder {
    public static ContentValues prepareOrderlistInsertValues(Order orders, Context context){
        ContentValues values = new ContentValues();
        values.put(OrderKey.QUANTITY.getKey(), orders.getQuantity());
        values.put(OrderKey.FREE.getKey(), orders.getFree());
        values.put(OrderKey.ITEM_RECID.getKey(), orders.getItemRecid());
        values.put(OrderKey.ITEM_NAME.getKey(), orders.getItemName());
        values.put(OrderKey.UNIT_NAME.getKey(), orders.getUnitName());
        values.put(OrderKey.REMARKS.getKey(), orders.getRemarks());
        values.put(OrderKey.OLD_SKU.getKey(), orders.getOldSku());
        values.put(OrderKey.SELLING_PRICE.getKey(), orders.getSellingPrice());
        values.put(OrderKey.TOTAL.getKey(), orders.getTotal());
        values.put(OrderKey.IS_CHECKED.getKey(), orders.getIsChecked());
        values.put(OrderKey.IS_ERROR.getKey(), orders.getIsError());
        values.put(OrderKey.IS_LOCKED.getKey(), orders.getIsLocked());
        return values;
    }
}

