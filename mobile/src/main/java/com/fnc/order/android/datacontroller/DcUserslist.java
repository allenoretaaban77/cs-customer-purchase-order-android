package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.OrderlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.database.UserslistQueryBuilder;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.UserslistKey;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.model.Userslist;

import java.util.LinkedList;

public class DcUserslist extends DBHelper {

    private Context context;
    private static DcUserslist instance;

    public static DcUserslist getInstance(Context context){
        if(instance == null){
            instance = new DcUserslist(context);
        }
        return instance;
    }

    public DcUserslist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyUserslist(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.USERSLIST.getName(), null,null);
        db.close();
    }

    public void inserUserslist(Userslist ul) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = UserslistQueryBuilder.prepareUserslistInsertValues(ul, context);
        db.insertWithOnConflict(Table.USERSLIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteUserlistViaid(String identityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.USERSLIST.getName(), "identityId = ?", new String[] { identityId });
        db.close();
    }

    public LinkedList<Userslist> getUserslist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.USERSLIST.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Userslist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setUserslist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Userslist> checkUser(String strUn, String strPw) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.USERSLIST.getName()
            + " WHERE Email = '" + strUn + "' OR tempo_id = '" + strUn + "' AND Password = '" + strPw + "'";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Userslist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setUserslist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private Userslist setUserslist(Cursor c) {
        Userslist ul = new Userslist();
        ul.setIdentityId(c.getString(c.getColumnIndex(UserslistKey.IDENTITYID.getKey())));
        ul.setEmail(c.getString(c.getColumnIndex(UserslistKey.EMAIL.getKey())));
        ul.setPassword(c.getString(c.getColumnIndex(UserslistKey.PASSWORD.getKey())));
        ul.setFirstName(c.getString(c.getColumnIndex(UserslistKey.FIRSTNAME.getKey())));
        ul.setMiddleName(c.getString(c.getColumnIndex(UserslistKey.MIDDLENAME.getKey())));
        ul.setLastName(c.getString(c.getColumnIndex(UserslistKey.LASTNAME.getKey())));
        ul.setDateOfBirth(c.getString(c.getColumnIndex(UserslistKey.DATEOFBIRTH.getKey())));
        ul.setVerified(c.getInt(c.getColumnIndex(UserslistKey.VERIFIED.getKey())));
        ul.setCompany_UniqId(c.getString(c.getColumnIndex(UserslistKey.COMPANY_UNIQIE.getKey())));
        ul.setReference_employee_no(c.getString(c.getColumnIndex(UserslistKey.REF_EMPLOYEE_NO.getKey())));
        ul.setTempo_id(c.getString(c.getColumnIndex(UserslistKey.TEMPO_ID.getKey())));
        return ul;
    }
}
