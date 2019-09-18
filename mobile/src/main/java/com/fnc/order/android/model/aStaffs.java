package com.fnc.order.android.model;

public class aStaffs {
    private int empId;
    private String refempno;
    private String empNo;
    private String Email;
    private String name;
    private int Branch;
    private int Jobtitle;
    private String pass;
    private String active;
    private String ismobileadmin;

    public aStaffs (int empId, String refempno, String empNo, String Email, String name, int Branch, int Jobtitle,
                    String pass, String active, String ismobileadmin) {

        this.empId = empId;
        this.refempno = refempno;
        this.empNo = empNo;
        this.Email = Email;
        this.name = name;
        this.Branch = Branch;
        this.Jobtitle = Jobtitle;
        this.pass = pass;
        this.active = active;
        this.ismobileadmin = ismobileadmin;
    }

    public int getEmpId() { return empId; }
    public String getRefempno() { return refempno; }
    public String getEmpNo() { return empNo; }
    public String getEmail() { return Email; }
    public String getName() { return name; }
    public int getBranch() { return Branch; }
    public int getJobtitle() { return Jobtitle; }
    public String getPass() { return pass; }
    public String getActive() { return active; }
    public String getIsmobileadmin() { return ismobileadmin; }
}