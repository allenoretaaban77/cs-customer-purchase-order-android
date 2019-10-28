package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum OrderKey {
    QUANTITY("quantity", DataType.INTEGER),
    ITEM_RECID("item_recid", DataType.INTEGER),
    ITEM_NAME("itemname", DataType.TEXT),
    UNIT_NAME("unitName", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    OLD_SKU("old_sku", DataType.TEXT),
    SELLING_PRICE("selling_price", DataType.TEXT),
    TOTAL("total", DataType.TEXT),
    IS_CHECKED("is_checked", DataType.INTEGER),
    IS_ERROR("is_error", DataType.INTEGER),
    IS_LOCKED("is_locked", DataType.INTEGER);

    private String key;
    private String dataType;

    OrderKey(String key,String dataType) {
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
