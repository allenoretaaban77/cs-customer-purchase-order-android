package com.fnc.receiving.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.database.Table;
import com.fnc.receiving.android.database.aUsersQueryBuilder;
import com.fnc.receiving.android.enumeration.aUsersKey;
import com.fnc.receiving.android.model.aUsers;

import java.util.LinkedList;

public class DcUsers extends DBHelper {

    private Context context;
    private static DcUsers instance;

    public static DcUsers getInstance(Context context){
        if(instance == null){
            instance = new DcUsers(context);
        }
        return instance;
    }

    public DcUsers(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyUserslist(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.USERS.getName(), null,null);
        db.close();
    }

    public void insertUsers(aUsers ul) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aUsersQueryBuilder.prepareUsersInsertValues(ul, context);
        db.insertWithOnConflict(Table.USERS.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteUsersViaId(String identityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.USERS.getName(), "identityId = ?", new String[] { identityId });
        db.close();
    }

    public LinkedList<aUsers> getUsers() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.USERS.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aUsers> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setUsers(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<aUsers> checkUser(String strUn, String strPw) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.USERS.getName()
            + " WHERE Email = '" + strUn + "' OR tempo_id = '" + strUn + "' AND Password = '" + strPw + "'";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aUsers> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setUsers(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aUsers setUsers(Cursor c) {
        aUsers ul = new aUsers();
        ul.setIdentityId(c.getString(c.getColumnIndex(aUsersKey.IDENTITYID.getKey())));
        ul.setEmail(c.getString(c.getColumnIndex(aUsersKey.EMAIL.getKey())));
        ul.setPassword(c.getString(c.getColumnIndex(aUsersKey.PASSWORD.getKey())));
        ul.setFirstName(c.getString(c.getColumnIndex(aUsersKey.FIRSTNAME.getKey())));
        ul.setMiddleName(c.getString(c.getColumnIndex(aUsersKey.MIDDLENAME.getKey())));
        ul.setLastName(c.getString(c.getColumnIndex(aUsersKey.LASTNAME.getKey())));
        ul.setDateOfBirth(c.getString(c.getColumnIndex(aUsersKey.DATEOFBIRTH.getKey())));
        ul.setVerified(c.getInt(c.getColumnIndex(aUsersKey.VERIFIED.getKey())));
        ul.setCompany_UniqId(c.getString(c.getColumnIndex(aUsersKey.COMPANY_UNIQIE.getKey())));
        ul.setReference_employee_no(c.getString(c.getColumnIndex(aUsersKey.REF_EMPLOYEE_NO.getKey())));
        ul.setTempo_id(c.getString(c.getColumnIndex(aUsersKey.TEMPO_ID.getKey())));
        return ul;
    }
}
