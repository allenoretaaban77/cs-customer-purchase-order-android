package com.fnc.order.android.model;

import java.io.Serializable;

public class aBranchlist implements Serializable {
    private Integer branchid;
    private String branchcode;
    private String deviceid;
    private String description;

    public aBranchlist (Integer branchid, String branchcode, String deviceid, String description) {
        this.branchid = branchid;
        this.branchcode = branchcode;
        this.deviceid = deviceid;
        this.description = description;
    }

    public Integer getBranchid() { return branchid; }
    public void setBranchid(Integer integration_recid) { this.branchid = branchid; }

    public String getBranchcode() { return branchcode; }
    public void setBranchcode(String branchcode) { this.branchcode = branchcode; }

    public String getDeviceid() { return deviceid; }
    public void setDeviceid(String deviceid) { this.deviceid = deviceid; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
