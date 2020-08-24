package com.fnc.order.android.model;

public class aStaffs {
    private Long empId;
    private String refempno;
    private String empNo;
    private String Email;
    private String name;
    private Long Branch;
    private Long Jobtitle;
    private String pass;
    private String active;
    private String ismobileadmin;

    public aStaffs (Long empId, String refempno, String empNo, String Email, String name, Long Branch, Long Jobtitle,
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

    public Long getEmpId() { return empId; }
    public String getRefempno() { return refempno; }
    public String getEmpNo() { return empNo; }
    public String getEmail() { return Email; }
    public String getName() { return name; }
    public Long getBranch() { return Branch; }
    public Long getJobtitle() { return Jobtitle; }
    public String getPass() { return pass; }
    public String getActive() { return active; }
    public String getIsmobileadmin() { return ismobileadmin; }
}