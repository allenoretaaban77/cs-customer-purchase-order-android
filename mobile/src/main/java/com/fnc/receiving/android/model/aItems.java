package com.fnc.receiving.android.model;

public class aItems {
    private int recid;
    private int checklist_recid;
    private String rec_tally;
    private String rec_qty;
    private String tally;
    private float quantity;
    private float po_quantity;
    private String nunit;
    private String unit;
    private String itemName_wUnit;
    private int allowdecimal;
    private int old_sku;
    private String old_barcode;
    private String item_recid;
    private String selling_price;

    public aItems (int recid, int checklist_recid, String rec_tally, String rec_qty, String tally,
        float quantity, float po_quantity, String nunit, String unit, String itemName_wUnit, int allowdecimal,
        int old_sku, String old_barcode, String item_recid, String selling_price) {
        this.recid = recid;
        this.checklist_recid = checklist_recid;
        this.rec_tally = rec_tally;
        this.rec_qty = rec_qty;
        this.tally = tally;
        this.quantity = quantity;
        this.po_quantity = po_quantity;
        this.nunit = nunit;
        this.unit = unit;
        this.itemName_wUnit = itemName_wUnit;
        this.allowdecimal = allowdecimal;
        this.old_sku = old_sku;
        this.old_barcode = old_barcode;
        this.item_recid = item_recid;
        this.selling_price = selling_price;
    }

    public int getRecid() { return recid; }
    public int getChecklist_recid() { return checklist_recid; }
    public String getRec_tally() { return rec_tally; }
    public String getRec_qty() { return rec_qty; }
    public String getTally() { return tally; }
    public float getQuantity() { return quantity; }
    public float getPo_quantity() { return po_quantity; }
    public String getNunit() { return nunit; }
    public String getUnit() { return unit; }
    public String getItemName_wUnit() { return itemName_wUnit; }
    public int getAllowdecimal() { return allowdecimal; }
    public int getOld_sku() { return old_sku; }
    public String getOld_barcode() { return old_barcode; }
    public String getItem_recid() { return item_recid; }
    public String getSelling_price() { return selling_price; }
}
