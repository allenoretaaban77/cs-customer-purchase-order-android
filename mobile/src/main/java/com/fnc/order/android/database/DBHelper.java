package com.fnc.order.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.ReturnKey;

import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTables(Table.ORDER, setOrderFields()));
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
