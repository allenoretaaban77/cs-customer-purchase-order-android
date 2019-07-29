package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum aItemsKey {
    RECID("recid", DataType.INTEGER),
    CHECKLIST_RECID("checklist_recid", DataType.INTEGER),
    REC_TALLY("rec_tally", DataType.TEXT),
    REC_QUANTITY("rec_qty", DataType.TEXT),
    TALLY("tally", DataType.TEXT),
    QUANTITY("quantity", DataType.NUMERIC),
    PO_QUANTITY("po_quantity", DataType.NUMERIC),
    NUNIT("nunit", DataType.TEXT),
    UNIT("unit", DataType.TEXT),
    ITEMNAME_WUNIT("itemName_wUnit", DataType.TEXT),
    ALLOW_DECIMAL("allowdecimal", DataType.INTEGER),
    OLD_SKU("old_sku", DataType.INTEGER),
    OLD_BARCODE("old_barcode", DataType.TEXT),
    ITEM_RECID("item_recid", DataType.TEXT),
    SELLING_PRICE("selling_price", DataType.TEXT);

    private String key;
    private String dataType;

    aItemsKey(String key,String dataType) {
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
