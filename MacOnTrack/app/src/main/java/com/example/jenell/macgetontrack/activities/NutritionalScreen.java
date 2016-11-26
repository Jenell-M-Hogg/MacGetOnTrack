package com.example.jenell.macgetontrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.example.jenell.macontrack.R;


/**
 * Created by Jenell on 4/1/2015.
 */
public class NutritionalScreen extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutritional);
        setTitle("");

    }

    public void zoom(View v){
        Intent i=new Intent("zoom_in");
        startActivity(i);

    }
}
