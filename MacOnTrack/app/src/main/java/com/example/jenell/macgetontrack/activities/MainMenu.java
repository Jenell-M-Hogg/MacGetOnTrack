package com.example.jenell.macgetontrack.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.jenell.macontrack.R;
import com.example.jenell.macgetontrack.database.DataBaseHandler;


public class MainMenu extends Activity {

    private String userName;
    private DataBaseHandler data;
    private Thread screenChanger;
    private int apk=Build.VERSION.SDK_INT;



    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data=new DataBaseHandler(this.getApplicationContext());
        boolean exists=data.checkIfUserExists();




        if (!exists){
            this.setUp();
        }
        else{
            userName=data.getCurrentUser();
            setContentView(R.layout.activity_main_menu);

            if(apk>=17){
                int id=InfoHolder.randPicShow();
                RelativeLayout f = (RelativeLayout) this.findViewById(R.id.page_main_menu);
                f.setBackground(getResources().getDrawable(id));

            }
        }








    }

    //Take to the set up screen
    private void setUp() {

        this.setContentView(R.layout.set_up);

        Button submit= (Button) findViewById(R.id.set_up_button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSetUp();
            }
        });



    }



    public void switchScreen(View l){
        Intent i=new Intent();
        String action = null;
        if (l.equals(this.findViewById(R.id.toProfile))){
            action="profile";

        }
        else if (l.equals(this.findViewById(R.id.toToday))){
            action="today";

        }
        else if (l.equals(this.findViewById(R.id.toCalendar))){
            action="calendar";
        }
        else if (l.equals(this.findViewById(R.id.toNutritional))){
            action="nutritional";

        }
        else if (l.equals(this.findViewById(R.id.main_menu_button_recipeBook))){
            action="recipeBookSearch";

        }
        else if(l.equals(this.findViewById(R.id.help_Button))){
            action="help";
        }

        i.setAction(action);
        startActivity(i);




    }



    @SuppressLint("NewApi")
    public void submitSetUp(){
        //This executes when the user submits on the set up page
        //this.userName=this.editTextToString(R.id.set_Up_editText_username);

        //Switch the screen to the main Menu
        String user=editTextToString(R.id.set_Up_editText_username);
        if(user.trim().length()>0){

            data.addCurrentUser(user);
            userName=user;
            data.setRecipes();
            setContentView(R.layout.activity_main_menu);
            if(apk>=17) {
                int id = InfoHolder.randPicShow();
                RelativeLayout f = (RelativeLayout) this.findViewById(R.id.page_main_menu);
                f.setBackground(getResources().getDrawable(id));
            }


        }
        else{
           String message="Please enter a name!";
           EditText t= (EditText) findViewById(R.id.set_Up_editText_username);
           t.setHint(message);
        }

    }







    //HELPER METHODS
    public String editTextToString(int id){
        EditText edit= (EditText) findViewById(id);
        String test= edit.getText().toString();
        return test;
    }









}
