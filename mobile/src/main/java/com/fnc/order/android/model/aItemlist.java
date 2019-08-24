package com.fnc.order.android.model;

import java.io.Serializable;

public class aItemlist implements Serializable {
    private String integration_recid;
    private int recid;
    private String old_sku;
    private int baseUnit_recid;
    private double baseUnit_qty;
    private String itemNo;
    private String itemname;
    private String itemName_wUnit;
    private double quantity_inUnit;
    private String dept;
    private String unit;
    private int tblUnit_recid;
    private int unit_toconvert;
    private String barcodeNo;
    private int f_base;
    private String d_itemdepartment_code;
    private double selling_price;
    private double cost_price;
    private String taxcode;
    private int expense_acct;
    private int income_acct;
    private String data_visibility;
    private String barcodeNo1;

    public aItemlist ( String integration_recid, int recid, String old_sku, int baseUnit_recid,
        double baseUnit_qty, String itemNo, String itemname, String itemName_wUnit, double quantity_inUnit,
        String dept, String unit, int tblUnit_recid, int unit_toconvert, String barcodeNo,
        int f_base, String d_itemdepartment_code, double selling_price, double cost_price,
        String taxcode, int expense_acct, int income_acct, String data_visibility, String barcodeNo1) {

        this.integration_recid = integration_recid;
        this.recid = recid;
        this.old_sku = old_sku;
        this.baseUnit_recid = baseUnit_recid;
        this.baseUnit_qty = baseUnit_qty;
        this.itemNo = itemNo;
        this.itemname = itemname;
        this.itemName_wUnit = itemName_wUnit;
        this.quantity_inUnit = quantity_inUnit;
        this.dept = dept;
        this.unit = unit;
        this.tblUnit_recid = tblUnit_recid;
        this.unit_toconvert = unit_toconvert;
        this.barcodeNo = barcodeNo;
        this.f_base = f_base;
        this.d_itemdepartment_code = d_itemdepartment_code;
        this.selling_price = selling_price;
        this.cost_price = cost_price;
        this.taxcode = taxcode;
        this.expense_acct = expense_acct;
        this.income_acct = income_acct;
        this.data_visibility = data_visibility;
        this.barcodeNo1 = barcodeNo1;
    }

    public String getIntegration_recid() {
        return integration_recid;
    }
    public void setIntegration_recid(String integration_recid) { this.integration_recid = integration_recid; }

    public int getRecid() {
        return recid;
    }
    public void setRecid(int recid) { this.recid = recid; }

    public String getOld_sku() {
        return old_sku;
    }
    public void setOld_sku(String old_sku) { this.old_sku = old_sku; }

    public int getBaseUnit_recid() {
        return baseUnit_recid;
    }
    public void setBaseUnit_recid(int baseUnit_recid) { this.baseUnit_recid = baseUnit_recid; }

    public double getBaseUnit_qty() {
        return baseUnit_qty;
    }
    public void setBaseUnit_qty(double baseUnit_qty) { this.baseUnit_qty = baseUnit_qty; }

    public String getItemNo() {
        return itemNo;
    }
    public void setItemNo(String itemNo) { this.itemNo = itemNo; }

    public String getItemname() {
        return itemname;
    }
    public void setItemname(String itemname) { this.itemname = itemname; }

    public String getItemName_wUnit() {
        return itemName_wUnit;
    }
    public void setItemName_wUnit(String itemName_wUnit) { this.itemName_wUnit = itemName_wUnit; }

    public double getQuantity_inUnit() {
        return quantity_inUnit;
    }
    public void setQuantity_inUnit(double quantity_inUnit) { this.quantity_inUnit = quantity_inUnit; }

    public String getDept() {
        return dept;
    }
    public void setDept(String dept) { this.dept = dept; }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) { this.unit = unit; }

    public int getTblUnit_recid() {
        return tblUnit_recid;
    }
    public void setTblUnit_recid(int tblUnit_recid) { this.tblUnit_recid = tblUnit_recid; }

    public int getUnit_toconvert() {
        return unit_toconvert;
    }
    public void setUnit_toconvert(int unit_toconvert) { this.unit_toconvert = unit_toconvert; }

    public String getBarcodeNo() {
        return barcodeNo;
    }
    public void setBarcodeNo(String barcodeNo) { this.barcodeNo = barcodeNo; }

    public int getF_base() {
        return f_base;
    }
    public void setF_base(int f_base) { this.f_base = f_base; }

    public String getD_itemdepartment_code() {
        return d_itemdepartment_code;
    }
    public void setD_itemdepartment_code(String d_itemdepartment_code) { this.d_itemdepartment_code = d_itemdepartment_code; }

    public double getSelling_price() {
        return selling_price;
    }
    public void setSelling_price(double selling_price) { this.selling_price = selling_price; }

    public double getCost_price() {
        return cost_price;
    }
    public void setCost_price(double cost_price) { this.cost_price = cost_price; }

    public String getTaxcode() {
        return taxcode;
    }
    public void setTaxcode(String taxcode) { this.taxcode = taxcode; }

    public int getExpense_acct() {
        return expense_acct;
    }
    public void setExpense_acct(int expense_acct) { this.expense_acct = expense_acct; }

    public int getIncome_acct() {
        return income_acct;
    }
    public void setIncome_acct(int income_acct) { this.income_acct = income_acct; }

    public String getData_visibility() {
        return data_visibility;
    }
    public void setData_visibility(String data_visibility) { this.data_visibility = data_visibility; }

    public String getBarcodeNo1() {
        return barcodeNo1;
    }
    public void setBarcodeNo1(String barcodeNo1) { this.barcodeNo1 = barcodeNo1; }
}
