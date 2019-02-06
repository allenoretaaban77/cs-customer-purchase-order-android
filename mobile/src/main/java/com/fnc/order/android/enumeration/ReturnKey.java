package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum ReturnKey {
    QUANTITY("quantity", DataType.INTEGER),
    ITEM_RECID("item_recid", DataType.INTEGER),
    ITEM_NAME("itemname", DataType.TEXT),
    UNIT_NAME("unitName", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT);

    private String key;
    private String dataType;

    ReturnKey(String key,String dataType) {
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
