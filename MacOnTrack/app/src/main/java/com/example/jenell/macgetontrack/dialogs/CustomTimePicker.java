package com.example.jenell.macgetontrack.dialogs;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by Jenell on 3/19/2015.
 */
public class CustomTimePicker extends TimePickerDialog {

    public CustomTimePicker(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {


        super(context, callBack, hourOfDay, minute, is24HourView);
    }





}
