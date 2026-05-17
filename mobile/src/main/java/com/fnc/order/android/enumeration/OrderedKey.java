package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum OrderedKey {
    CUSTOMER_INTEG_RECID("customer_integ_recid", DataType.INTEGER),
    CUSTOMER_RECID("customer_recid", DataType.INTEGER),
    CUSTOMER_NAME("customer_name", DataType.TEXT),
    DELIVERY_DATE("deliver_date", DataType.TEXT),
    CREATED_BY("createdby", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    REF_EMPLOYEE_NO("reference_employee_no", DataType.TEXT),
    JSON("json", DataType.TEXT),
    JSON_COMPLETE("json_complete", DataType.TEXT),
    GRAND_TOTAL("grandtotal", DataType.TEXT),
    GRAND_TOTAL_CNT("grandtotalcount", DataType.TEXT),
    DATETIME("datetime", DataType.DATETIME),
    STATUS("status", DataType.INTEGER),
    REF_RECID("reference_recid", DataType.INTEGER),
    DELIVERY_DATE_DEFAULT("deliver_date_default", DataType.TEXT);

    private String key;
    private String dataType;

    OrderedKey(String key,String dataType) {
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
