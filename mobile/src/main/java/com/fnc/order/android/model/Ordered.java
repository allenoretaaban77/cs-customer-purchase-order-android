package com.fnc.order.android.model;

import java.io.Serializable;

public class Ordered implements Serializable {
    private String customer_integ_recid;
    private String customer_recid;
    private String customer_name;
    private String deliver_date;
    private String createdby;
    private String remarks;
    private String reference_employee_no;
    private String json;
    private String grandtotal;
    private String datetime;
    private int status;

    public String getCustomerIntegRecid() {
        return customer_integ_recid;
    }
    public void setCustomerIntegRecid(String customer_integ_recid) { this.customer_integ_recid = customer_integ_recid; }

    public String getCustomerRecid() {
        return customer_recid;
    }
    public void setCustomerRecid(String customer_recid) { this.customer_recid = customer_recid; }

    public String getCustomerName() {
        return customer_name;
    }
    public void setCustomerName(String customer_name) { this.customer_name = customer_name; }

    public String getDeliveryDate() {
        return deliver_date;
    }
    public void setDeliveryDate(String deliver_date) { this.deliver_date = deliver_date; }

    public String getCreatedBy() {
        return createdby;
    }
    public void setCreatedBy(String createdby) { this.createdby = createdby; }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getReferenceEmployeeNo() {
        return reference_employee_no;
    }
    public void setReferenceEmployeeNo(String reference_employee_no) { this.reference_employee_no = reference_employee_no; }

    public String getJson() {
        return json;
    }
    public void setJson(String json) { this.json = json; }

    public String getGrandtotal() {
        return grandtotal;
    }
    public void setGrandtotal(String grandtotal) { this.grandtotal = grandtotal; }

    public String getDateTime() {
        return datetime;
    }
    public void setDateTime(String datetime) { this.datetime = datetime; }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) { this.status = status; }

}
