package com.fnc.order.android.database;

import android.content.ContentValues;
import android.content.Context;

import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.model.aItemlist;

public class aItemlistQueryBuilder {

    public static ContentValues prepareaItemlistInsertValues(aItemlist aitemlist, Context context){
        ContentValues values = new ContentValues();
        values.put(aItemlistKey.INTEGRATION_RECID.getKey(), aitemlist.getIntegration_recid());
        values.put(aItemlistKey.RECID.getKey(), aitemlist.getRecid());
        values.put(aItemlistKey.OLD_SKU.getKey(), aitemlist.getOld_sku());
        values.put(aItemlistKey.BASEUNIT_RECID.getKey(), aitemlist.getBaseUnit_recid());
        values.put(aItemlistKey.BASEUNIT_QTY.getKey(), aitemlist.getBaseUnit_qty());
        values.put(aItemlistKey.ITEMNO.getKey(), aitemlist.getItemNo());
        values.put(aItemlistKey.ITEMNAME.getKey(), aitemlist.getItemname());
        values.put(aItemlistKey.ITEMNAME_WUNIT.getKey(), aitemlist.getItemName_wUnit());
        values.put(aItemlistKey.QUANTITY_INUNIT.getKey(), aitemlist.getQuantity_inUnit());
        values.put(aItemlistKey.DEPT.getKey(), aitemlist.getDept());
        values.put(aItemlistKey.UNIT.getKey(), aitemlist.getUnit());
        values.put(aItemlistKey.TBLUNIT_RECID.getKey(), aitemlist.getTblUnit_recid());
        values.put(aItemlistKey.UNIT_TOCONVERT.getKey(), aitemlist.getUnit_toconvert());
        values.put(aItemlistKey.BARCODENO.getKey(), aitemlist.getBarcodeNo());
        values.put(aItemlistKey.F_BASE.getKey(), aitemlist.getF_base());
        values.put(aItemlistKey.D_ITEMDEPARTMENT_CODE.getKey(), aitemlist.getD_itemdepartment_code());
        values.put(aItemlistKey.SELLING_PRICE.getKey(), aitemlist.getSelling_price());
        values.put(aItemlistKey.COST_PRICE.getKey(), aitemlist.getCost_price());
        values.put(aItemlistKey.TAXCODE.getKey(), aitemlist.getTaxcode());
        values.put(aItemlistKey.EXPENSE_ACCT.getKey(), aitemlist.getExpense_acct());
        values.put(aItemlistKey.INCOME_ACCT.getKey(), aitemlist.getIncome_acct());
        values.put(aItemlistKey.DATA_VISIBILITY.getKey(), aitemlist.getData_visibility());
        values.put(aItemlistKey.BARCODENO1.getKey(), aitemlist.getBarcodeNo1());
        return values;
    }
}


