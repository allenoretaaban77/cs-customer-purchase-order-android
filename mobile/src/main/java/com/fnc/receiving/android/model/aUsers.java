package com.fnc.receiving.android.model;

import java.io.Serializable;

public class aUsers implements Serializable {
    private String identityId;
    private String Email;
    private String Password;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String DateOfBirth;
    private Integer Verified;
    private String Company_UniqId;
    private String reference_employee_no;
    private String tempo_id;

    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) { this.identityId = identityId; }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String Email) { this.Email = Email; }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String Password) { this.Password = Password; }

    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String FirstName) { this.FirstName = FirstName; }

    public String getMiddleName() {
        return MiddleName;
    }
    public void setMiddleName(String MiddleName) { this.MiddleName = MiddleName; }

    public String getLastName() {
        return LastName;
    }
    public void setLastName(String LastName) { this.LastName = LastName; }

    public String getDateOfBirth() {
        return DateOfBirth;
    }
    public void setDateOfBirth(String DateOfBirth) { this.DateOfBirth = DateOfBirth; }

    public Integer getVerified() {
        return Verified;
    }
    public void setVerified(Integer Verified) { this.Verified = Verified; }

    public String getCompany_UniqId() {
        return Company_UniqId;
    }
    public void setCompany_UniqId(String Company_UniqId) { this.Company_UniqId = Company_UniqId; }

    public String getReference_employee_no() {
        return reference_employee_no;
    }
    public void setReference_employee_no(String reference_employee_no) { this.reference_employee_no = reference_employee_no; }

    public String getTempo_id() {
        return tempo_id;
    }
    public void setTempo_id(String tempo_id) { this.tempo_id = tempo_id; }
}
