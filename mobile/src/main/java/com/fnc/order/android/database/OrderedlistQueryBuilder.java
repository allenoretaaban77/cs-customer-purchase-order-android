package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.model.Ordered;

public class OrderedlistQueryBuilder {
    public static ContentValues prepareOrderedlistInsertValues(Ordered ordered, Context context){
        ContentValues values = new ContentValues();
        values.put(OrderedKey.CUSTOMER_INTEG_RECID.getKey(), ordered.getCustomerIntegRecid());
        values.put(OrderedKey.CUSTOMER_RECID.getKey(), ordered.getCustomerRecid());
        values.put(OrderedKey.CUSTOMER_NAME.getKey(), ordered.getCustomerName());
        values.put(OrderedKey.DELIVERY_DATE.getKey(), ordered.getDeliveryDate());
        values.put(OrderedKey.CREATED_BY.getKey(), ordered.getCreatedBy());
        values.put(OrderedKey.REMARKS.getKey(), ordered.getRemarks());
        values.put(OrderedKey.REF_EMPLOYEE_NO.getKey(), ordered.getReferenceEmployeeNo());
        values.put(OrderedKey.JSON.getKey(), ordered.getJson());
        values.put(OrderedKey.GRAND_TOTAL.getKey(), ordered.getGrandtotal());
        values.put(OrderedKey.DATETIME.getKey(), ordered.getDateTime());
        values.put(OrderedKey.STATUS.getKey(), ordered.getStatus());
        return values;
    }
}