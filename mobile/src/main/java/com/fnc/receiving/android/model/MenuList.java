package com.fnc.receiving.android.model;

import java.io.Serializable;

public class MenuList implements Serializable {
    private String customerid;
    private String integration_recid;
    private String customername;
    private String remarks;
    private Integer recordcount;
    private String alphachar;

    public String getCustomerID() { return customerid; }
    public void setCustomerID(String customerid) { this.customerid = customerid; }

    public String getCustomerIntegrationId() { return integration_recid; }
    public void setCustomerIntegrationId(String integration_recid) { this.integration_recid = integration_recid;}

    public String getCustomerName() {
        return customername;
    }
    public void setCustomerName(String customername) { this.customername = customername;
    }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getRecordCount() { return recordcount; }
    public void setRecordCount(Integer recordcount) { this.recordcount = recordcount; }

    public String getAlphachar() { return alphachar; }
    public void setAlphachar(String alphachar) { this.alphachar = alphachar; }
}
