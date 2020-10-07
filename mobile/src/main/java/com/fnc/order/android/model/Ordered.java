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
    private String json_complete;
    private String grandtotal;
    private String grandtotalcount;
    private String datetime;
    private int status;
    private String reference_recid;
    private String deliver_date_default;

    public String getCustomerIntegRecid() { return customer_integ_recid; }
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

    public String getJsonComplete() {
        return json_complete;
    }
    public void setJsonComplete(String json_complete) { this.json_complete = json_complete; }

    public String getGrandtotal() { return grandtotal; }
    public void setGrandtotal(String grandtotal) { this.grandtotal = grandtotal; }

    public String getGrandtotalcount() { return grandtotalcount; }
    public void setGrandtotalcount(String grandtotalcount) { this.grandtotalcount = grandtotalcount; }

    public String getDateTime() {
        return datetime;
    }
    public void setDateTime(String datetime) { this.datetime = datetime; }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) { this.status = status; }

    public String getReferenceRecid() {
        return reference_recid;
    }
    public void setReferenceRecid(String reference_recid) { this.reference_recid = reference_recid; }

    public String getDeliver_date_default() {
        return deliver_date_default;
    }
    public void setDeliver_date_default(String deliver_date_default) { this.deliver_date_default = deliver_date_default; }

}
