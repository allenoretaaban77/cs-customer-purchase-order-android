package com.fnc.receiving.android.model;

import java.io.Serializable;

public class Itemlist implements Serializable {
    private int recid_old;
    private int recid;
    private String old_sku;
    private String selling_price;
    private int itemNo;
    private int itemBarcodeNo;
    private int parent_baseitem_recid;
    private int child_group_recid;
    private int itemtype;
    private String itemname;
    private String dept;
    private int itype;
    private String parentName;
    private String unitMeasurementDesc;
    private String unitMeasurementQty;
    private String unitName;
    private Boolean is_checked;

    public Integer getRecidOld() {
        return recid_old;
    }
    public void setRecidOld(int recid_old) { this.recid_old = recid_old; }

    public Integer getRecid() { return recid; }
    public void setRecid(int recid) { this.recid = recid; }

    public String getOldSku() { return old_sku; }
    public void setOldSku(String old_sku) { this.old_sku = old_sku; }

    public String getSellingPrice() { return selling_price; }
    public void setSellingPrice(String selling_price) { this.selling_price = selling_price; }

    public Integer getItemNo() {
        return itemNo;
    }
    public void setItemNo(int itemNo) { this.itemNo = itemNo; }

    public Integer getItemBarcodeNo() { return itemBarcodeNo; }
    public void setItemBarcodeNo(int itemBarcodeNo) { this.itemBarcodeNo = itemBarcodeNo; }

    public Integer getParentBaseitemRecid() {
        return parent_baseitem_recid;
    }
    public void setParentBaseitemRecid(int parent_baseitem_recid) { this.parent_baseitem_recid = parent_baseitem_recid; }

    public Integer getChildGroupRecid() {
        return child_group_recid;
    }
    public void setChildGroupRecid(int child_group_recid) { this.child_group_recid = child_group_recid; }

    public String getItemName() { return itemname; }
    public void setItemName(String itemname) { this.itemname = itemname; }

    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public Boolean getIsChecked() {
        return is_checked;
    }
    public void setIsChecked(Boolean is_checked) { this.is_checked = is_checked; }

}
