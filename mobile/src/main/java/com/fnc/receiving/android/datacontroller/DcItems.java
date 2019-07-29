package com.fnc.receiving.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.database.Table;
import com.fnc.receiving.android.database.aItemsQueryBuilder;
import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.enumeration.aItemsKey;
import com.fnc.receiving.android.model.aItems;

import java.util.LinkedList;

public class DcItems extends DBHelper {

    private Context context;
    private static DcItems instance;

    public static DcItems getInstance(Context context){
        if(instance == null){
            instance = new DcItems(context);
        }
        return instance;
    }

    public DcItems(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyItems(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.ITEMS.getName(), null,null);
        db.close();
    }

    public void insertItems(aItems il) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aItemsQueryBuilder.prepareItemsInsertValues(il, context);
        db.insertWithOnConflict(Table.ITEMS.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteItemsViaId(String identityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.ITEMS.getName(), "identityId = ?", new String[] { identityId });
        db.close();
    }

    public LinkedList<aItems> getItems() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.ITEMS.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aItems> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setItems(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aItems setItems(Cursor c) {
        aItems il = new aItems(
            c.getInt(c.getColumnIndex(aItemsKey.RECID.getKey())),
            c.getInt(c.getColumnIndex(aItemsKey.CHECKLIST_RECID.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.REC_TALLY.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.REC_QUANTITY.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.TALLY.getKey())),
            c.getFloat(c.getColumnIndex(aItemsKey.QUANTITY.getKey())),
            c.getFloat(c.getColumnIndex(aItemsKey.PO_QUANTITY.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.NUNIT.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.UNIT.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.ITEMNAME_WUNIT.getKey())),
            c.getInt(c.getColumnIndex(aItemsKey.ALLOW_DECIMAL.getKey())),
            c.getInt(c.getColumnIndex(aItemsKey.OLD_SKU.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.OLD_BARCODE.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.ITEM_RECID.getKey())),
            c.getString(c.getColumnIndex(aItemsKey.SELLING_PRICE.getKey()))
        );
        return il;
    }
}
