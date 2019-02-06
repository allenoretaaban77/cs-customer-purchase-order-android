package com.fnc.order.android.model;

import java.io.Serializable;

public class Checklist implements Serializable {
    private int recid;
    private int checklist_recid;
    private String tally_string;
    private String tally;
    private String tally_in_quantity;
    private String po_quantity;
    private String unit;
    private String allowdecimal;
    private String itemName_wUnit;
    private String dept;
    private int inorder;
    private String remarks;
    private int tablerow_id;
    private int page_number;

    public int getRecid() {
        return recid;
    }
    public void setRecid(int recid) {
        this.recid = recid;
    }

    public int getChecklistRecid() {
        return checklist_recid;
    }
    public void setChecklistRecid(int checklist_recid) {
        this.checklist_recid = checklist_recid;
    }

    public String getTallyString() {
        return tally_string;
    }
    public void setTallyString(String tally_string) {
        this.tally_string = tally_string;
    }

    public String getTally() {
        return tally;
    }
    public void setTally(String tally) {
        this.tally = tally;
    }

    public String getTallyInQuantity() {
        return tally_in_quantity;
    }
    public void setTallyInQuantity(String tally_in_quantity) {
        this.tally_in_quantity = tally_in_quantity;
    }

    public String getPoQuantity() {
        return po_quantity;
    }
    public void setPoQuantity(String po_quantity) {
        this.po_quantity = po_quantity;
    }

    public String getAllowDecimal() {
        return allowdecimal;
    }
    public void setAllowDecimal(String allowdecimal) {
        this.allowdecimal = allowdecimal;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemname() {
        return itemName_wUnit;
    }
    public void setItemname(String itemName_wUnit) {
        this.itemName_wUnit = itemName_wUnit;
    }

    public String getDept() {
        return dept;
    }
    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getInorder() {
        return inorder;
    }
    public void setInorder(int inorder) {
        this.inorder = inorder;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getTableRowId() {
        return tablerow_id;
    }
    public void setTableRowId(Integer tablerow_id) {
        this.tablerow_id = tablerow_id;
    }

    public Integer getPageNumber() { return page_number; }
    public void setPageNumber(Integer page_number) {
        this.page_number = page_number;
    }
}
