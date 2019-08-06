package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.UserslistKey;
import com.fnc.order.android.model.Userslist;

public class UserslistQueryBuilder {
    public static ContentValues prepareUserslistInsertValues(Userslist ul, Context context){
        ContentValues values = new ContentValues();
        values.put(UserslistKey.IDENTITYID.getKey(), ul.getIdentityId());
        values.put(UserslistKey.EMAIL.getKey(), ul.getEmail());
        values.put(UserslistKey.PASSWORD.getKey(), ul.getPassword());
        values.put(UserslistKey.FIRSTNAME.getKey(), ul.getFirstName());
        values.put(UserslistKey.MIDDLENAME.getKey(), ul.getMiddleName());
        values.put(UserslistKey.LASTNAME.getKey(), ul.getLastName());
        values.put(UserslistKey.DATEOFBIRTH.getKey(), ul.getDateOfBirth());
        values.put(UserslistKey.VERIFIED.getKey(), ul.getVerified());
        values.put(UserslistKey.COMPANY_UNIQIE.getKey(), ul.getCompany_UniqId());
        values.put(UserslistKey.REF_EMPLOYEE_NO.getKey(), ul.getReference_employee_no());
        values.put(UserslistKey.TEMPO_ID.getKey(), ul.getTempo_id());
        return values;
    }
}
