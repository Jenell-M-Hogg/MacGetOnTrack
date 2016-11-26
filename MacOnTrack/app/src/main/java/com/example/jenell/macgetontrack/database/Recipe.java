package com.example.jenell.macgetontrack.database;

import android.media.Image;

/**
 * Created by Jenell on 3/8/2015.
 */
public class Recipe {

    int pictureID;
    String title;
    String[] ingredients;
    String[] directions;
    String category;


    public String getCategory() {
        return category;
    }

    Recipe(String title,String category,  String[] ingredients,String[] directions, int picture){
        this.title=title;
        this.category=category;
        this.ingredients=ingredients;
        this.directions=directions;
        pictureID=picture;

    }



    public int getPicture() {
        return pictureID;
    }

    public void setPicture(int picture) {
        this.pictureID = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getDirections() {
        return directions;
    }

    public void setDirections(String[] directions) {
        this.directions = directions;
    }
}
