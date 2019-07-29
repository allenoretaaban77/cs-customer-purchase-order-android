package com.fnc.receiving.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.receiving.android.enumeration.aItemsKey;
import com.fnc.receiving.android.model.aItems;

public class aItemsQueryBuilder {
    public static ContentValues prepareItemsInsertValues(aItems il, Context context){
        ContentValues values = new ContentValues();
        values.put(aItemsKey.RECID.getKey(), il.getRecid());
        values.put(aItemsKey.CHECKLIST_RECID.getKey(), il.getChecklist_recid());
        values.put(aItemsKey.REC_TALLY.getKey(), il.getRec_tally());
        values.put(aItemsKey.REC_QUANTITY.getKey(), il.getQuantity());
        values.put(aItemsKey.TALLY.getKey(), il.getTally());
        values.put(aItemsKey.QUANTITY.getKey(), il.getQuantity());
        values.put(aItemsKey.PO_QUANTITY.getKey(), il.getPo_quantity());
        values.put(aItemsKey.NUNIT.getKey(), il.getNunit());
        values.put(aItemsKey.UNIT.getKey(), il.getUnit());
        values.put(aItemsKey.ITEMNAME_WUNIT.getKey(), il.getItemName_wUnit());
        values.put(aItemsKey.ALLOW_DECIMAL.getKey(), il.getAllowdecimal());
        values.put(aItemsKey.OLD_SKU.getKey(), il.getOld_sku());
        values.put(aItemsKey.OLD_BARCODE.getKey(), il.getOld_barcode());
        values.put(aItemsKey.ITEM_RECID.getKey(), il.getItem_recid());
        values.put(aItemsKey.SELLING_PRICE.getKey(), il.getSelling_price());
        return values;
    }
}