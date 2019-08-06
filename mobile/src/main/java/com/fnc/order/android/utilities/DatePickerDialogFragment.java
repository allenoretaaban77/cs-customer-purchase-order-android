package com.fnc.order.android.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.listeners.DatePickerListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aldwind on 25/01/2018.
 */

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    private  DatePickerListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                android.R.style.Theme_DeviceDefault_Dialog, this, year, month, day);
        return dialog;
    }
    public void setDatePickerListener(DatePickerListener listener) {
        this.mListener = listener;
    }

    public void setDate(int year1, int month1, int day1) {
        year = year1;
        month = month1;
        day = day1;
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT);
        Date date;
        try {
            date = dateFormat.parse(year +"-" + (monthOfYear+ 1) +"-"+dayOfMonth);
            if (mListener != null){
                mListener.didPickDate(getContext(),date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}