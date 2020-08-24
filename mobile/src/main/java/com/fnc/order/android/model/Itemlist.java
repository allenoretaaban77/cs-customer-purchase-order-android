package com.fnc.order.android.model;

import java.io.Serializable;

public class Itemlist implements Serializable {
    private Long recid_old;
    private Long recid;
    private String old_sku;
    private String selling_price;
    private Long itemNo;
    private Long itemBarcodeNo;
    private Long parent_baseitem_recid;
    private Long child_group_recid;
    private int itemtype;
    private String itemname;
    private String dept;
    private int itype;
    private String parentName;
    private String unitMeasurementDesc;
    private String unitMeasurementQty;
    private String unitName;
    private Boolean is_checked;

    public Long getRecidOld() {
        return recid_old;
    }
    public void setRecidOld(Long recid_old) { this.recid_old = recid_old; }

    public Long getRecid() { return recid; }
    public void setRecid(Long recid) { this.recid = recid; }

    public String getOldSku() { return old_sku; }
    public void setOldSku(String old_sku) { this.old_sku = old_sku; }

    public String getSellingPrice() { return selling_price; }
    public void setSellingPrice(String selling_price) { this.selling_price = selling_price; }

    public Long getItemNo() {
        return itemNo;
    }
    public void setItemNo(Long itemNo) { this.itemNo = itemNo; }

    public Long getItemBarcodeNo() { return itemBarcodeNo; }
    public void setItemBarcodeNo(Long itemBarcodeNo) { this.itemBarcodeNo = itemBarcodeNo; }

    public Long getParentBaseitemRecid() {
        return parent_baseitem_recid;
    }
    public void setParentBaseitemRecid(Long parent_baseitem_recid) { this.parent_baseitem_recid = parent_baseitem_recid; }

    public Long getChildGroupRecid() {
        return child_group_recid;
    }
    public void setChildGroupRecid(Long child_group_recid) { this.child_group_recid = child_group_recid; }

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
