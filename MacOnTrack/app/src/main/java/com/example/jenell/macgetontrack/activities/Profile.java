package com.example.jenell.macgetontrack.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macontrack.R;

/**
 * Created by Jenell on 4/1/2015.
 */

//todo test this class
public class Profile extends ActionBarActivity {


    DataBaseHandler data;
    String user;
    Bundle saved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data=new DataBaseHandler(this);
        user=data.getCurrentUser();
        saved=savedInstanceState;


        setContentView(R.layout.activity_profile);
        //Set the username text to the current user's username
        TextView username= (TextView) findViewById(R.id.profile_TextView_username);
        username.setText(user);

        setTitle("My Profile");


        //Set the profile pic to whatever the user selected
        Bitmap b=data.getUserPic();
        if(b!=null){
            this.changeProfilePic(b);
        }


    }

    public void submitEdit(View v){
        //Store new edit

        EditText e= (EditText) this.findViewById(R.id.profile_editText_username);
        String newUser=e.getText().toString();

        //If the new username is acceptable
        if(newUser.trim().length()>0){
            //TODO Debug

            data.setCurrentUser(newUser);

            Intent i = new Intent("profile");
            startActivity(i);

        }


        else{
            String message="Please enter a name!";
            EditText t= (EditText) findViewById(R.id.profile_editText_username);
            t.setText("");
            t.setHint(message);
        }

    }

    public void edit(View v){
        //Switch to edit mode

        //Make the submit button visible
        Button b= (Button) this.findViewById(R.id.profile_Button_submitEdit);
        b.setVisibility(View.VISIBLE);

        //Make the textview an editable
        TextView t= (TextView) this.findViewById(R.id.profile_TextView_username);
        t.setVisibility(View.INVISIBLE);

        EditText e= (EditText) this.findViewById(R.id.profile_editText_username);
        e.setText(user);

        e.setVisibility(View.VISIBLE);

        //Make the "edit button" invisible
        Button edit= (Button) this.findViewById(R.id.editProfile);
        edit.setVisibility(View.INVISIBLE);

        //Make the message visible
        t= (TextView) this.findViewById(R.id.message);
        t.setVisibility(View.VISIBLE);

    }

    public void getPicture(View v){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }


    private static final int PICK_IMAGE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri _uri = data.getData();

            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            cursor.close();


            final Bitmap b=BitmapFactory.decodeFile(imageFilePath);

            changeProfilePic(b);

            helperThread m= new helperThread(b);
            m.start();
            //Save the image into the data base

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeProfilePic(Bitmap b) {
        ImageView imageView = (ImageView) findViewById(R.id.profilePic);
        imageView.setImageBitmap(b);
    }


    public class helperThread extends Thread{
        Bitmap b;
        helperThread(Bitmap b){
            this.b=b;
        }
        @Override
        public void run() {
            data.setUserPic(b);
        }
    }

}
