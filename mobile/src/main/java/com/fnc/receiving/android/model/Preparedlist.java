package com.fnc.receiving.android.model;

import java.io.Serializable;

public class Preparedlist implements Serializable {
    private int recid;
    private int checklist_recid;
    private String customer_id;
    private String customer_name;
    private String dept;
    private String remarks;
    private int inorder;
    private int status;
    private String json;

    public int getRecid() {
        return recid;
    }
    public void setRecid(int recid) {
        this.recid = recid;
    }

    public int getChecklistRecid() {
        return checklist_recid;
    }
    public void setChecklistRecid(int checklist_recid) {
        this.checklist_recid = checklist_recid;
    }

    public String getCustomerId() {
        return customer_id;
    }
    public void setCustomerId(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomerName() {
        return customer_name;
    }
    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getDept() {
        return dept;
    }
    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getInorder() {
        return inorder;
    }
    public void setInorder(int inorder) {
        this.inorder = inorder;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getJson() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }
}
