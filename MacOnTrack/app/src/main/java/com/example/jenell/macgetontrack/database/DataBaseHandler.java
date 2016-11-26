package com.example.jenell.macgetontrack.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenell on 3/16/2015.
 */
public class DataBaseHandler {
   DataBaseInterface data;

    public DataBaseHandler(Context context){
        data=DataBaseInterface.getInstance(context);
    }




    public String getCurrentUser(){
        return data.getUser();
    }




    public Day getToday(int[] currentDate){
        String date = intDateToString(currentDate);
        boolean exists=data.checkIfDayExists(date);

        if (!exists){
            Day lastToday=new Day(currentDate);
            data.addToday(lastToday);
            return lastToday;
            //Return a new today if it doesn't exist in the database yet
        }

        else{
            Day lastToday=data.getToday(intDateToString(currentDate));
            //Figure out if the last day put into the data base is today!
            return lastToday;


        }



    }

    private String intDateToString(int[] currentDate) {
        String d="";
        for (int i=0;i<currentDate.length;i++){
            d+=Integer.toString(currentDate[i])+"-";
        }
        return d;
    }

    public Recipe getRecipe(String recipe) {
        return data.getRecipe(recipe);

    }

    public void setCurrentUser(String user) {
        data.updateUser(user);
    }

    public void addCurrentUser(String user) {
        data.addUser(user);
    }

    public void setRecipes() {
        data.setUpRecipeBook();
    }

    public boolean checkIfUserExists() {
        return data.checkIfUserExists();
    }

    //TODO DEBUG
    public Day getCalendarDate(int[] ds) {
        String date=this.intDateToString(ds);

        Day d=data.getToday(date);
        if(d==null){
            return new Day(ds);
        }
        return d;
    }

    public List<String> getRecipeTitles(String category) {
        ArrayList<String> m=data.getRecipeTitles(category);
        return m;

    }


    public void putToday(Day d) {
        String date = d.getDate();

        boolean isThere=data.checkIfDayExists(date);

        if(isThere){
            data.updateToday(d);
        }
        else{
            data.addToday(d);
        }
    }

    public Bitmap getUserPic(){
        try{
            byte[] b=data.getPicture();

            return this.getImage(b);

        }
        catch(Exception e){
            return null;
        }
    }

    public void setUserPic(Bitmap b) {
        byte[] bytes=getBytes(b);

        try{
            data.setPicture(bytes);
        }
        catch(Exception e){
            data.addPicture(bytes);
        }


    }


    private byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
