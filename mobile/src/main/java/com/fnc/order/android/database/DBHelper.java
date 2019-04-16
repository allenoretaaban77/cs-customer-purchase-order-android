package com.fnc.order.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.model.Ordered;

import java.io.File;
import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    public static final String DBPath = Environment.getExternalStorageDirectory().toString()
            + File.separator + "Android"
            + File.separator + "data"
            + File.separator + "com.fnc.order.android";

    public DBHelper(Context context) {
        super(context, DBPath + File.separator + DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTables(Table.ORDER, setOrderFields()));
        db.execSQL(createTables(Table.ORDERED, setOrderedFields()));
        Log.i(TAG,"Database created path : "+db.getPath());
    }

    protected String createTables(Table table,LinkedList<?> list) {
        return QueryBuilder.createTables(table,list);
    }

    protected LinkedList<OrderKey> setOrderFields() {
        LinkedList<OrderKey> fields = new LinkedList<>();
        fields.add(OrderKey.QUANTITY);
        fields.add(OrderKey.ITEM_RECID);
        fields.add(OrderKey.ITEM_NAME);
        fields.add(OrderKey.UNIT_NAME);
        fields.add(OrderKey.REMARKS);
        fields.add(OrderKey.OLD_SKU);
        fields.add(OrderKey.SELLING_PRICE);
        return fields;
    }

    protected LinkedList<OrderedKey> setOrderedFields() {
        LinkedList<OrderedKey> fields = new LinkedList<>();
        fields.add(OrderedKey.CUSTOMER_INTEG_RECID);
        fields.add(OrderedKey.CUSTOMER_RECID);
        fields.add(OrderedKey.CUSTOMER_NAME);
        fields.add(OrderedKey.DELIVERY_DATE);
        fields.add(OrderedKey.CREATED_BY);
        fields.add(OrderedKey.REMARKS);
        fields.add(OrderedKey.REF_EMPLOYEE_NO);
        fields.add(OrderedKey.JSON);
        fields.add(OrderedKey.DATETIME);
        fields.add(OrderedKey.STATUS);
        return fields;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion + 1;
        switch (version){
            case 2:
                db.execSQL(createTables(Table.ORDER, setOrderFields()));
        }
    }
}
