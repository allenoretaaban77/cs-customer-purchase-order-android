package com.fnc.receiving.android.model;

import java.io.Serializable;

public class Releasing implements Serializable {
    private String quantity;
    private String item_recid;
    private String itemname;
    private String unitName;
    private String remarks;

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getItemRecid() {
        return item_recid;
    }
    public void setItemRecid(String item_recid) { this.item_recid = item_recid; }

    public String getItemName() { return itemname; }
    public void setItemName(String itemname) { this.itemname = itemname; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) { this.remarks = remarks; }

}
