package com.fnc.order.android.model;


import java.io.Serializable;

public class CustomerList implements Serializable {
    private Long recid;
    private Long customerID;
    private String customername;
    private Long group_recid;
    private int integration_recid;
    private String dateupdated;

    public Long getRecId() {
        return recid;
    }
    public void setRecId(Long recid) {
        this.recid = recid;
    }

    public Long getCustomerId() {
        return customerID;
    }
    public void setCustomerId(Long customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customername;
    }
    public void setCustomerName(String customername) {
        this.customername = customername;
    }
}
