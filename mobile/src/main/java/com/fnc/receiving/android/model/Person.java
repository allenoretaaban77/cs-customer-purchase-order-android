package com.fnc.receiving.android.model;

import java.io.Serializable;

public class Person implements Serializable {
    private String identityid;
    private String name;

    public String getIdentityId() {
        return identityid;
    }
    public void setIdentityId(String identityid) {
        this.identityid = identityid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
