package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum PreparedlistKey {
    RECID("recid", DataType.INTEGER),
    CHECKLIST_RECID("checklist_recid", DataType.TEXT),
    CUSTOMER_ID("customer_id", DataType.TEXT),
    CUSTOMER_NAME("customer_name", DataType.TEXT),
    DEPT("dept",DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    INORDER("inorder", DataType.INTEGER),
    STATUS("status", DataType.INTEGER), // 0=failed, 1=posted
    JSON("json", DataType.TEXT);

    private String key;
    private String dataType;

    PreparedlistKey(String key,String dataType) {
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
