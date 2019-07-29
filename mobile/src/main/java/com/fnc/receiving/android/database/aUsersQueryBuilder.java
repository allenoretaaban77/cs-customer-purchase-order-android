package com.fnc.receiving.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.receiving.android.enumeration.aUsersKey;
import com.fnc.receiving.android.model.aUsers;

public class aUsersQueryBuilder {
    public static ContentValues prepareUsersInsertValues(aUsers ul, Context context){
        ContentValues values = new ContentValues();
        values.put(aUsersKey.IDENTITYID.getKey(), ul.getIdentityId());
        values.put(aUsersKey.EMAIL.getKey(), ul.getEmail());
        values.put(aUsersKey.PASSWORD.getKey(), ul.getPassword());
        values.put(aUsersKey.FIRSTNAME.getKey(), ul.getFirstName());
        values.put(aUsersKey.MIDDLENAME.getKey(), ul.getMiddleName());
        values.put(aUsersKey.LASTNAME.getKey(), ul.getLastName());
        values.put(aUsersKey.DATEOFBIRTH.getKey(), ul.getDateOfBirth());
        values.put(aUsersKey.VERIFIED.getKey(), ul.getVerified());
        values.put(aUsersKey.COMPANY_UNIQIE.getKey(), ul.getCompany_UniqId());
        values.put(aUsersKey.REF_EMPLOYEE_NO.getKey(), ul.getReference_employee_no());
        values.put(aUsersKey.TEMPO_ID.getKey(), ul.getTempo_id());
        return values;
    }
}
