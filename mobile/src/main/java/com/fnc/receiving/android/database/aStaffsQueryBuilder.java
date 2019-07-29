package com.fnc.receiving.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.receiving.android.enumeration.aStaffsKey;
import com.fnc.receiving.android.model.aStaffs;

public class aStaffsQueryBuilder {
    public static ContentValues prepareStaffsInsertValues(aStaffs sl, Context context){
        ContentValues values = new ContentValues();
        values.put(aStaffsKey.EMPID.getKey(), sl.getEmpId());
        values.put(aStaffsKey.EMPNO.getKey(), sl.getEmpNo());
        values.put(aStaffsKey.EMAIL.getKey(), sl.getEmail());
        values.put(aStaffsKey.NAME.getKey(), sl.getName());
        values.put(aStaffsKey.BRANCH.getKey(), sl.getBranch());
        values.put(aStaffsKey.JOBTITLE.getKey(), sl.getJobtitle());
        values.put(aStaffsKey.PASS.getKey(), sl.getPass());
        values.put(aStaffsKey.ACTIVE.getKey(), sl.getActive());
        return values;
    }
}
