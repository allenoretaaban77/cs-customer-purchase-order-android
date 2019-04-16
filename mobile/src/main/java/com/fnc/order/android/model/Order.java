package com.fnc.order.android.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String quantity;
    private String item_recid;
    private String itemname;
    private String unitName;
    private String remarks;
    private String old_sku;
    private String selling_price;
    private String total;
    private Boolean is_checked;
    private Boolean is_error;

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

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getOldSku() { return old_sku; }
    public void setOldSku(String old_sku) { this.old_sku = old_sku; }

    public String getSellingPrice() { return selling_price; }
    public void setSellingPrice(String selling_price) { this.selling_price = selling_price; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public Boolean getIsChecked() {
        return is_checked;
    }
    public void setIsChecked(Boolean is_checked) { this.is_checked = is_checked; }

    public Boolean getIsError() {
        return is_error;
    }
    public void setIsError(Boolean is_error) { this.is_error = is_error; }
}
