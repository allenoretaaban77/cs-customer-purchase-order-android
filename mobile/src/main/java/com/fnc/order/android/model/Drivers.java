package com.fnc.order.android.model;

import java.io.Serializable;

public class Drivers implements Serializable {
    private String EmpNo;
    private String empname;

    public String getEmployeeNumber() { return EmpNo; }
    public void setEmployeeNumber(String EmpNo) { this.EmpNo = EmpNo; }

    public String getEmployeeName() { return empname; }
    public void setEmployeeName(String empname) { this.empname = empname; }

}