package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum aItemlistKey {
    INTEGRATION_RECID("integration_recid", DataType.TEXT),
    RECID("recid", DataType.TEXT),
    OLD_SKU("old_sku", DataType.TEXT),
    BASEUNIT_RECID("baseUnit_recid", DataType.TEXT),
    BASEUNIT_QTY("baseUnit_qty", DataType.TEXT),
    ITEMNO("itemNo", DataType.TEXT),
    ITEMNAME("itemname", DataType.TEXT),
    ITEMNAME_WUNIT("itemName_wUnit", DataType.TEXT),
    QUANTITY_INUNIT("quantity_inUnit", DataType.TEXT),
    DEPT("dept", DataType.TEXT),
    UNIT("unit", DataType.TEXT),
    TBLUNIT_RECID("tblUnit_recid", DataType.TEXT),
    UNIT_TOCONVERT("unit_toconvert", DataType.TEXT),
    BARCODENO("barcodeNo", DataType.TEXT),
    F_BASE("f_base", DataType.TEXT),
    D_ITEMDEPARTMENT_CODE("d_itemdepartment_code", DataType.TEXT),
    SELLING_PRICE("selling_price", DataType.TEXT),
    COST_PRICE("cost_price", DataType.TEXT),
    TAXCODE("taxcode", DataType.TEXT),
    EXPENSE_ACCT("expense_acct", DataType.TEXT),
    INCOME_ACCT("income_acct", DataType.TEXT),
    DATA_VISIBILITY("data_visibility", DataType.TEXT),
    BARCODENO1("barcodeNo1", DataType.TEXT);

    private String key;
    private String dataType;

    aItemlistKey(String key,String dataType) {
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
