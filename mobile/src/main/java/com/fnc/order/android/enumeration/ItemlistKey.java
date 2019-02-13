package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum ItemlistKey {
    ITEM_RECORD("item_records", DataType.TEXT),
    RECID_OLD("recid_old", DataType.INTEGER),
    RECID("recid",DataType.INTEGER),
    ITEMNO("itemNo",DataType.INTEGER),
    ITEM_CARDCODE_NO("itemBarcodeNo",DataType.INTEGER),
    PARENT_BASEITEM_RECID("parent_baseitem_recid",DataType.INTEGER),
    CHILD_GROUPO_RECID("child_group_recid",DataType.INTEGER),
    PARENT_CHILD("parent_child",DataType.TEXT),
    TAX_CODE("a_taxcode_code",DataType.TEXT),
    ACCOUNT_CODE("a_account_code",DataType.TEXT),
    ITEMDEPARTMENT_CODE("d_itemdepartment_code",DataType.TEXT),
    ITEM_TYPE("itemtype",DataType.TEXT),
    ITEM_NAME("itemname",DataType.TEXT),
    ITEM_NAME_WITH_UNIT("itemName_wUnit",DataType.TEXT),
    DESCRIPTION("description",DataType.TEXT),
    ONHAND("onhand",DataType.TEXT),
    ON_HAND_LASTUPDATE("onhand_lastupdate",DataType.TEXT),
    UNIT_MEASUREMENT("unit_measurement",DataType.TEXT),
    DEPT("dept",DataType.TEXT),
    INVENTORY_TYPE("itype",DataType.TEXT),
    PARENT_NAME("parentName",DataType.TEXT),
    UNIT("unit",DataType.TEXT),
    UNIT_QTY_MEASUREMENT("unit_qty_measurement",DataType.TEXT),
    UNIT_MESUREMENT_DESC("unitMeasurementDesc",DataType.TEXT),
    UNIT_MEASUREMENT_QTY("unitMeasurementQty",DataType.TEXT),
    UNIT_NAME("unitName",DataType.TEXT),
    OLD_SKU("old_sku",DataType.TEXT),
    SELLING_PRICE("selling_price",DataType.TEXT);

    private String key;
    private String dataType;

    ItemlistKey(String key,String dataType) {
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
