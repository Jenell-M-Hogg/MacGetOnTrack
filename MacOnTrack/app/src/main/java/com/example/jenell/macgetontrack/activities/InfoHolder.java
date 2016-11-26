package com.example.jenell.macgetontrack.activities;

import com.example.jenell.macontrack.R;

import java.util.Random;

/**
 * Created by Jenell on 3/27/2015.
 */
public class InfoHolder {
    public static String[] category=new String[]{"breakfast","lunch","dinner","sndr"};
    public static String[] categoryTitle=new String[]{"breakfast","lunch","dinner","a snack or drink"};
    public static String[] searchForDateActions={"searchForBreakfast","searchForLunch","searchForDinner","searchForSndr"};
    public static String[] searchForTodayActions={"searchForBreakfastToday","searchForLunchToday","searchForDinnerToday","searchForSndrToday"};
    public static String[] recipeBookActions={"selectToday","selectCalendar","browse"};

    public static String open_recipe="open_recipe";
    public static String open_recipe_select="open_to_select";
    public static String dateKey="date";

    static int[] iconId={R.drawable.breakfast_icon,R.drawable.lunch_icon,R.drawable.dinner_icon};

    private static int[] picShow={R.drawable.beans,R.drawable.cinnamon,R.drawable.fruitsalad,R.drawable.trailmix_nuts,R.drawable.bellpepers,R.drawable.citrus,R.drawable.strawberriesyum};


    public static int[] stringToIntArray(String s) {
        String[] g = s.split("-");
        int[] n=new int[g.length];
        for (int i=0;i<g.length;i++){
            n[i]=Integer.parseInt(g[i]);
        }
        return n;
    }

    public static int getPlaceInArray(String cat, String[] category) {
        int m=0;
        for (int i=0;i<InfoHolder.category.length;i++){
            if(cat.equals(InfoHolder.category[i])){
                m=i;
            }
        }
        return m;
    }

    public static int getPlaceInArray(int id, int[] picId) {
        for (int i=0;i<picId.length;i++){
            if(id==picId[i]){
                return i;
            }
        }
        return -1;
    }

    public static int randPicShow(){
        //Returns a random id from the picShow array
        Random rand=new Random();
        int ind=rand.nextInt(InfoHolder.picShow.length-1);
        return picShow[ind];

    }
}
