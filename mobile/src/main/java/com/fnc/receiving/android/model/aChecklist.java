package com.fnc.receiving.android.model;

import java.io.Serializable;

public class aChecklist implements Serializable {
    private int recid;
    private String branchcode;
    private String customername;
    private String deliveryDate;
    private String ordertype;
    private String includedPO;
    private String driver;
    private String plateno;
    private String createdby;
    private int drrecid;
    private int drNumber;
    private String invoiceNumber;
    private int pageno;
    private String status;
    private String status_lbl;
    private String remarks;
    private String foodservice;
    private String createdbyName;
    private int is_sent;
    private String jsonSent;

    public aChecklist (int recid, String branchcode, String customername, String deliveryDate,
        String ordertype, String includedPO, String driver, String plateno, String createdby,
        int drrecid, int drNumber, String invoiceNumber, int pageno, String status, String status_lbl,
        String remarks, String foodservice, String createdbyName) {

        this.recid = recid;
        this.branchcode = branchcode;
        this.customername = customername;
        this.deliveryDate = deliveryDate;
        this.ordertype = ordertype;
        this.includedPO = includedPO;
        this.driver = driver;
        this.plateno = plateno;
        this.createdby = createdby;
        this.drrecid = drrecid;
        this.drNumber = drNumber;
        this.invoiceNumber = invoiceNumber;
        this.pageno = pageno;
        this.status = status;
        this.status_lbl = status_lbl;
        this.remarks = remarks;
        this.foodservice = foodservice;
        this.createdbyName = createdbyName;
    }

    public aChecklist (int recid, String branchcode, String customername, String deliveryDate,
        String ordertype, String includedPO, String driver, String plateno, String createdby,
        int drrecid, int drNumber, String invoiceNumber, int pageno, String status, String status_lbl,
        String remarks, String foodservice, String createdbyName, Integer is_sent, String jsonSent) {

        this.recid = recid;
        this.branchcode = branchcode;
        this.customername = customername;
        this.deliveryDate = deliveryDate;
        this.ordertype = ordertype;
        this.includedPO = includedPO;
        this.driver = driver;
        this.plateno = plateno;
        this.createdby = createdby;
        this.drrecid = drrecid;
        this.drNumber = drNumber;
        this.invoiceNumber = invoiceNumber;
        this.pageno = pageno;
        this.status = status;
        this.status_lbl = status_lbl;
        this.remarks = remarks;
        this.foodservice = foodservice;
        this.createdbyName = createdbyName;
        this.is_sent = is_sent;
        this.jsonSent = jsonSent;
    }

    public int getRecid() { return recid; }
    public String getBranchcode() { return branchcode; }
    public String getCustomername() { return customername; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getOrdertype() { return ordertype; }
    public String getIncludedPO() { return includedPO; }
    public String getDriver() { return driver; }
    public String getPlateno() { return plateno; }
    public String getCreatedby() { return createdby; }
    public int getDrrecid() { return drrecid; }
    public int getDrNumber() { return drNumber; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public int getPageno() { return pageno; }
    public String getStatus() { return status; }
    public String getStatus_lbl() { return status_lbl; }
    public String getRemarks() { return remarks; }
    public String getFoodservice() { return foodservice; }
    public String getCreatedbyName() { return createdbyName; }
    public int getIs_sent() { return is_sent; }
    public String getJsonSent() { return jsonSent; }
}
