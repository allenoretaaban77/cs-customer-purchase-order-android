package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum ChecklistKey {
    RECID("recid", DataType.INTEGER),
    CHECKLIST_RECID("checklist_recid", DataType.TEXT),
    TALLY_STRING("tally_string", DataType.TEXT),
    TALLY("tally", DataType.TEXT),
    TALLY_QUANTITY("tally_in_quantity", DataType.TEXT),
    PO_QUANTITY("po_quantity", DataType.TEXT),
    UNIT("unit", DataType.TEXT),
    ALLOW_DECIMAL("allowdecimal", DataType.TEXT),
    ITEM_NAME("itemName_wUnit",DataType.TEXT),
    DEPT("dept",DataType.TEXT),
    INORDER("inorder",DataType.INTEGER),
    REMARKS("remarks", DataType.TEXT),
    TABLEROW_ID("tablerow_id", DataType.INTEGER),
    PAGE_NUMBER("page_number", DataType.INTEGER);

    private String key;
    private String dataType;

    ChecklistKey(String key,String dataType) {
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
