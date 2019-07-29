package com.fnc.receiving.android.model;

public class aStaffs {
    private int empId;
    private String empNo;
    private String Email;
    private String name;
    private int Branch;
    private int Jobtitle;
    private String pass;
    private String active;

    public aStaffs (int empId, String empNo, String Email, String name, int Branch, int Jobtitle,
        String pass, String active) {

        this.empId = empId;
        this.empNo = empNo;
        this.Email = Email;
        this.name = name;
        this.Branch = Branch;
        this.Jobtitle = Jobtitle;
        this.pass = pass;
        this.active = active;
    }

    public int getEmpId() { return empId; }
    public String getEmpNo() { return empNo; }
    public String getEmail() { return Email; }
    public String getName() { return name; }
    public int getBranch() { return Branch; }
    public int getJobtitle() { return Jobtitle; }
    public String getPass() { return pass; }
    public String getActive() { return active; }
}
