package com.fnc.order.android.model;

import java.io.Serializable;

public class aAdminGroupings implements Serializable {
    Integer recid;
    String code;
    String Description;
    String deleted;

    public Integer getRecid() { return recid; }
    public void setRecid(Integer recid) { this.recid = recid; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return Description; }
    public void setDescription(String Description) { this.Description = Description; }

    public String getDeleted() { return deleted; }
    public void setDeleted(String deleted) { this.deleted = deleted; }
}
