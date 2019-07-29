package com.fnc.receiving.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.fnc.receiving.android.enumeration.aStaffsKey;
import com.fnc.receiving.android.enumeration.aUsersKey;
import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.enumeration.aItemsKey;

import java.io.File;
import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    public static final String DBPath = Environment.getExternalStorageDirectory().toString()
            + File.separator + "Android"
            + File.separator + "data"
            + File.separator;

    public DBHelper(Context context) {
        super(context, DBPath + context.getPackageName() + File.separator + DbConstants.DB_NAME,
                null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTables(Table.CHECKLIST, setChecklistFields()));
        db.execSQL(createTables(Table.ITEMS, setItemsFields()));
        db.execSQL(createTables(Table.USERS, setUsersFields()));
        db.execSQL(createTables(Table.STAFFS, setStaffsFields()));
        Log.i(TAG,"Database created path : " + db.getPath());
    }

    protected String createTables(Table table,LinkedList<?> list) {
        return QueryBuilder.createTables(table,list);
    }

    protected LinkedList<aChecklistKey> setChecklistFields() {
        LinkedList<aChecklistKey> fields = new LinkedList<>();
        fields.add(aChecklistKey.RECID);
        fields.add(aChecklistKey.BRANCH_CODE);
        fields.add(aChecklistKey.CUSTOMER_NAME);
        fields.add(aChecklistKey.DELIVERY_DATE);
        fields.add(aChecklistKey.CHECKLIST_RECID);
        fields.add(aChecklistKey.INCLUDED_PO);
        fields.add(aChecklistKey.DRIVER);
        fields.add(aChecklistKey.PLATE_NO);
        fields.add(aChecklistKey.CREATED_BY);
        fields.add(aChecklistKey.DR_RECID);
        fields.add(aChecklistKey.DR_NUMBER);
        fields.add(aChecklistKey.INVOICE_NUMBER);
        fields.add(aChecklistKey.PAGE_NO);
        fields.add(aChecklistKey.STATUS);
        fields.add(aChecklistKey.STATUS_LBL);
        fields.add(aChecklistKey.REMARKS);
        fields.add(aChecklistKey.FOOD_SERVICE);
        fields.add(aChecklistKey.CREATED_BY_NAME);
        fields.add(aChecklistKey.IS_SENT);
        fields.add(aChecklistKey.JSON_SENT);
        return fields;
    }

    protected LinkedList<aItemsKey> setItemsFields() {
        LinkedList<aItemsKey> fields = new LinkedList<>();
        fields.add(aItemsKey.RECID);
        fields.add(aItemsKey.CHECKLIST_RECID);
        fields.add(aItemsKey.REC_TALLY);
        fields.add(aItemsKey.REC_QUANTITY);
        fields.add(aItemsKey.TALLY);
        fields.add(aItemsKey.QUANTITY);
        fields.add(aItemsKey.PO_QUANTITY);
        fields.add(aItemsKey.NUNIT);
        fields.add(aItemsKey.UNIT);
        fields.add(aItemsKey.ITEMNAME_WUNIT);
        fields.add(aItemsKey.ALLOW_DECIMAL);
        fields.add(aItemsKey.OLD_SKU);
        fields.add(aItemsKey.OLD_BARCODE);
        fields.add(aItemsKey.ITEM_RECID);
        fields.add(aItemsKey.SELLING_PRICE);
        return fields;
    }

    protected LinkedList<aUsersKey> setUsersFields() {
        LinkedList<aUsersKey> fields = new LinkedList<>();
        fields.add(aUsersKey.IDENTITYID);
        fields.add(aUsersKey.EMAIL);
        fields.add(aUsersKey.PASSWORD);
        fields.add(aUsersKey.FIRSTNAME);
        fields.add(aUsersKey.MIDDLENAME);
        fields.add(aUsersKey.LASTNAME);
        fields.add(aUsersKey.DATEOFBIRTH);
        fields.add(aUsersKey.VERIFIED);
        fields.add(aUsersKey.COMPANY_UNIQIE);
        fields.add(aUsersKey.REF_EMPLOYEE_NO);
        fields.add(aUsersKey.TEMPO_ID);
        return fields;
    }

    protected LinkedList<aStaffsKey> setStaffsFields() {
        LinkedList<aStaffsKey> fields = new LinkedList<>();
        fields.add(aStaffsKey.EMPID);
        fields.add(aStaffsKey.EMPNO);
        fields.add(aStaffsKey.EMAIL);
        fields.add(aStaffsKey.NAME);
        fields.add(aStaffsKey.BRANCH);
        fields.add(aStaffsKey.JOBTITLE);
        fields.add(aStaffsKey.PASS);
        fields.add(aStaffsKey.ACTIVE);
        return fields;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion + 1;
        switch (version){
            case 2:
                db.execSQL(createTables(Table.CHECKLIST, setChecklistFields()));
                db.execSQL(createTables(Table.ITEMS, setItemsFields()));
                db.execSQL(createTables(Table.USERS, setUsersFields()));
                db.execSQL(createTables(Table.STAFFS, setStaffsFields()));
        }
    }
}
