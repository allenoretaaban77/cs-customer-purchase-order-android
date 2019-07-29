package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum aChecklistKey {
    RECID("recid", DataType.INTEGER),
    BRANCH_CODE("branchcode", DataType.TEXT),
    CUSTOMER_NAME("customername", DataType.TEXT),
    DELIVERY_DATE("deliveryDate", DataType.TEXT),
    ORDER_TYPE("ordertype", DataType.TEXT),
    CHECKLIST_RECID("ordertype", DataType.TEXT),
    INCLUDED_PO("includedPO", DataType.TEXT),
    DRIVER("driver", DataType.TEXT),
    PLATE_NO("plateno", DataType.TEXT),
    CREATED_BY("createdby", DataType.TEXT),
    DR_RECID("drrecid", DataType.INTEGER),
    DR_NUMBER("drNumber", DataType.INTEGER),
    INVOICE_NUMBER("invoiceNumber", DataType.TEXT),
    PAGE_NO("pageno", DataType.INTEGER),
    STATUS("status", DataType.TEXT),
    STATUS_LBL("status_lbl", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    FOOD_SERVICE("foodservice", DataType.TEXT),
    CREATED_BY_NAME("createdbyName", DataType.TEXT),
    IS_SENT("is_sent", DataType.INTEGER),
    JSON_SENT("jsonSent", DataType.TEXT);

    private String key;
    private String dataType;

    aChecklistKey(String key,String dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public String getDataType() {
        return dataType;
    }
}
