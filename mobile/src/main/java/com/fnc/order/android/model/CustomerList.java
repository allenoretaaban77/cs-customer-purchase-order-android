package com.fnc.order.android.model;


import java.io.Serializable;

public class CustomerList implements Serializable {
    private int recid;
    private int customerID;
    private String customername;

    public Integer getRecId() {
        return recid;
    }
    public void setRecId(int recid) {
        this.recid = recid;
    }

    public Integer getCustomerId() {
        return customerID;
    }
    public void setCustomerId(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customername;
    }
    public void setCustomerName(String customername) {
        this.customername = customername;
    }
}
