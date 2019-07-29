package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum aUsersKey {
    IDENTITYID("identityId", DataType.TEXT),
    EMAIL("Email", DataType.TEXT),
    PASSWORD("Password", DataType.TEXT),
    FIRSTNAME("FirstName", DataType.TEXT),
    MIDDLENAME("MiddleName", DataType.TEXT),
    LASTNAME("LastName", DataType.TEXT),
    DATEOFBIRTH("DateOfBirth", DataType.TEXT),
    VERIFIED("Verified", DataType.INTEGER),
    COMPANY_UNIQIE("Company_UniqId", DataType.TEXT),
    REF_EMPLOYEE_NO("reference_employee_no", DataType.TEXT),
    TEMPO_ID("tempo_id", DataType.TEXT);

    private String key;
    private String dataType;

    aUsersKey(String key,String dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public String getDataType() {
        return dataType;
    }
}
