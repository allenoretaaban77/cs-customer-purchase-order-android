package com.fnc.order.android.model;

import java.io.Serializable;

public class aBranchlist implements Serializable {
    private Integer branchid;
    private String branchcode;
    private String deviceid;
    private String description;
    private String deviceID1;
    private String active;
    private String customerID;
    private String old_branchid;
    private String old_customerid;

    public aBranchlist (Integer branchid, String branchcode, String deviceid, String description,
        String deviceID1, String active, String customerID, String old_branchid, String old_customerid) {
        this.branchid = branchid;
        this.branchcode = branchcode;
        this.deviceid = deviceid;
        this.description = description;
        this.deviceID1 = deviceID1;
        this.active = active;
        this.customerID = customerID;
        this.old_branchid = old_branchid;
        this.old_customerid = old_customerid;
    }

    public Integer getBranchid() { return branchid; }
    public void setBranchid(Integer integration_recid) { this.branchid = branchid; }

    public String getBranchcode() { return branchcode; }
    public void setBranchcode(String branchcode) { this.branchcode = branchcode; }

    public String getDeviceid() { return deviceid; }
    public void setDeviceid(String deviceid) { this.deviceid = deviceid; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeviceID1() { return deviceID1; }
    public void setDeviceID1(String deviceID1) { this.deviceID1 = deviceID1; }

    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getOld_branchid() { return old_branchid; }
    public void setOld_branchid(String old_branchid) { this.old_branchid = old_branchid; }

    public String getOld_customerid() { return old_customerid; }
    public void setOld_customerid(String old_customerid) { this.old_customerid = old_customerid; }
}
