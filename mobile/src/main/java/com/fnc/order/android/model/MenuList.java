package com.fnc.order.android.model;

import java.io.Serializable;

public class MenuList implements Serializable {
    private String customerid;
    private String customername;
    private String remarks;
    private Integer recordcount;

    public String getCustomerID() {
        return customerid;
    }
    public void setCustomerID(String customerid) {
        this.customerid = customerid;
    }

    public String getCustomerName() {
        return customername;
    }
    public void setCustomerName(String customername) {
        this.customername = customername;
    }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getRecordCount() { return recordcount; }
    public void setRecordCount(Integer recordcount) { this.recordcount = recordcount; }
}
