package com.fnc.order.android.model;

import java.io.Serializable;

public class StoreList implements Serializable {
    private int recid;
    private String customername;
    private String customer_recid;
    private String deliveryDate;
    private String deliveryTime;
    private String foodservice;
    private String ordertype;
    private String status;
    private String remarks;
    private String drivername;
    private String plateno;
    private String deviceid;
    private String isloaded;
    private String includedpo;
    private String status_lbl;
    private String createdbyName;
    private Integer isdone;

    public String getCustomerName() {
        return customername;
    }
    public void setCustomerName(String customername) {
        this.customername = customername;
    }

    public int getRecid() {
        return recid;
    }
    public void setRecid(int recid) {
        this.recid = recid;
    }

    public String getCustomerRecId() {
        return customer_recid;
    }
    public void setCustomerRecId(String customer_recid) {
        this.customer_recid = customer_recid;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getFoodService() {
        return foodservice;
    }
    public void setFoodService(String foodservice) {
        this.foodservice = foodservice;
    }

    public String getOrderType() {
        return ordertype;
    }
    public void setOrderType(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDriverName() {
        return drivername;
    }
    public void setDriverName(String drivername) {
        this.drivername = drivername;
    }

    public String getPlateNo() {
        return plateno;
    }
    public void setPlateNo(String plateno) {
        this.plateno = plateno;
    }

    public String getDeviceId() {
        return deviceid;
    }
    public void setDeviceId(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getIsLoaded() {
        return isloaded;
    }
    public void setIsLoaded(String isloaded) {
        this.isloaded = isloaded;
    }

    public String getIncludedPO() {
        return includedpo;
    }
    public void setIncludedPO(String includedpo) {
        this.includedpo = includedpo;
    }

    public String getStatusLbl() {
        return status_lbl;
    }
    public void setStatusLbl(String status_lbl) {
        this.status_lbl = status_lbl;
    }

    public String getCreatedBy() {
        return createdbyName;
    }
    public void setCreatedBy(String createdbyName) {
        this.createdbyName = createdbyName;
    }

    public Integer getIsDone() {
        return isdone;
    }
    public void setIsDone(int isdone) {
        this.isdone = isdone;
    }
}
