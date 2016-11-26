package com.example.jenell.macgetontrack.database;

/**
 * Created by Jenell on 3/8/2015.
 */
public class Day {
    //This class represents a day

    boolean[] successes={false,false,false};
    String[] recipes={"Choose a recipe","Choose a recipe","Choose a recipe"};
    String []times={"8:00","13:00","18:30"};
    String []sndr={"Choose a snack/drink","Choose a snack/drink","Choose a snack/drink"};

    String endOfDay="19:00";
    public String date;

    public Day(int[] currentDate){
        String d= "";
        for (int i=0;i<currentDate.length;i++){
            d=d+Integer.toString(currentDate[i])+"-";
        }
        date=d;

    }



    public Day(String date, String[] recipes, String[] times, String[] successes, String endOfDay) {
        this.date=date;
        this.recipes=recipes;
        this.times=times;
        this.successes=this.stringToBool(successes);
        this.endOfDay=endOfDay;

    }

    private boolean[] stringToBool(String[] successes) {
        //Takes an array of strings that represent booleans and converts them to boolean format.
        boolean[] b=new boolean[successes.length];

        for (int i=0;i<successes.length;i++){

            b[i]=Boolean.parseBoolean(successes[i]);
        }
        return b;

    }


    public boolean[] getSuccesses() {
        return successes;
    }

    public void setSuccesses(boolean[] successes) {
        this.successes = successes;
    }

    public String[] getRecipes() {
        return recipes;
    }

    public void setRecipes(String[] recipes) {
        this.recipes = recipes;
    }

    public String[] getTimes() {
        return times;
    }

    public void setTimes(String[] times) {
        this.times = times;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndOfDay() {
        return endOfDay;
    }

    public void setEndOfDay(String endOfDay) {
        this.endOfDay = endOfDay;
    }

    public void putRecipe(Recipe currentRecipe) {
        String cat=currentRecipe.getCategory();
        int i=-1;
        if(cat.equals("breakfast")){
            i=0;
        }
        else if (cat.equals("lunch")){
            i=1;
        }
        else if (cat.equals("dinner")){
            i=2;
        }
        else if(cat.equals("sndr")){
            i=3;
        }

        if(i!=3){
            recipes[i]=currentRecipe.getTitle();

        }
        else{
            int m=0;
            while(m!=3){
                if(sndr[m].equals("Choose a snack/drink")){
                    sndr[m]=currentRecipe.getTitle();
                    break;
                }
                i++;
            }
        }


    }
}
