package com.fnc.receiving.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.model.aChecklist;

public class aChecklistQueryBuilder {
    public static ContentValues prepareChecklistInsertValues(aChecklist cl, Context context){
        ContentValues values = new ContentValues();
        values.put(aChecklistKey.RECID.getKey(), cl.getRecid());
        values.put(aChecklistKey.BRANCH_CODE.getKey(), cl.getBranchcode());
        values.put(aChecklistKey.CUSTOMER_NAME.getKey(), cl.getCustomername());
        values.put(aChecklistKey.DELIVERY_DATE.getKey(), cl.getDeliveryDate());
        values.put(aChecklistKey.ORDER_TYPE.getKey(), cl.getOrdertype());
        values.put(aChecklistKey.INCLUDED_PO.getKey(), cl.getIncludedPO());
        values.put(aChecklistKey.DRIVER.getKey(), cl.getDriver());
        values.put(aChecklistKey.PLATE_NO.getKey(), cl.getPlateno());
        values.put(aChecklistKey.CREATED_BY.getKey(), cl.getCreatedby());
        values.put(aChecklistKey.DR_RECID.getKey(), cl.getDrrecid());
        values.put(aChecklistKey.DR_NUMBER.getKey(), cl.getDrNumber());
        values.put(aChecklistKey.INVOICE_NUMBER.getKey(), cl.getInvoiceNumber());
        values.put(aChecklistKey.PAGE_NO.getKey(), cl.getPageno());
        values.put(aChecklistKey.STATUS.getKey(), cl.getStatus());
        values.put(aChecklistKey.STATUS_LBL.getKey(), cl.getStatus_lbl());
        values.put(aChecklistKey.REMARKS.getKey(), cl.getRemarks());
        values.put(aChecklistKey.FOOD_SERVICE.getKey(), cl.getFoodservice());
        values.put(aChecklistKey.CREATED_BY_NAME.getKey(), cl.getCreatedbyName());
        values.put(aChecklistKey.IS_SENT.getKey(), cl.getIs_sent());
        values.put(aChecklistKey.JSON_SENT.getKey(), cl.getJsonSent());
        return values;
    }
}