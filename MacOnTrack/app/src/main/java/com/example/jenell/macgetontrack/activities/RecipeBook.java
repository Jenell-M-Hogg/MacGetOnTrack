package com.example.jenell.macgetontrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jenell.macontrack.R;
import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macgetontrack.database.Day;
import com.example.jenell.macgetontrack.database.Recipe;

public class RecipeBook extends ActionBarActivity{
    private final int directions=R.id.recipe_page_TextView_directions;
    private final int ingredient=R.id.recipe_page_TextView_ingredient;
    private final int recipeTitle=R.id.recipe_page_TextView_recipeTitle;
    private final int pic=R.id.recipe_page_ImageView_pic;

    private final int id_decisionPanel=R.id.recipe_page_LinearLayout_decisionpanel;
    private final int id_decisionYes=R.id.recipe_page_decisionYes;

    private final int id_recipeQuestion=R.id.recipe_page_textView_mealchoice;





    private final int[] slideShow= new int[]{};

    private int[] selectForDate;

    private boolean forToday=false;
    private boolean toSelect=false;
    private int categoryIndex;

    private Recipe currentRecipe;



    DataBaseHandler data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        data=new DataBaseHandler(this.getApplicationContext());

        setContentView(R.layout.activity_recipebook);


        this.initialize();
    }
    //TODO
    private void initialize() {

        Bundle b=this.getIntent().getExtras();
        this.selectForDate=b.getIntArray("date");

        String action=this.getIntent().getAction();
        if (action.equals("recipeBook")){
            //The recipe book has been opened to a page, without selection.
            String recipe=b.getString("open_recipe");
            currentRecipe=data.getRecipe(recipe);
            categoryIndex=InfoHolder.getPlaceInArray(currentRecipe.getCategory(),InfoHolder.category);

            displayRecipe(false);
        }
        else if(action.equals(InfoHolder.recipeBookActions[0])|action.equals(InfoHolder.recipeBookActions[1])){
            //The recipe book has been opened to a page for selection
            String recipe=b.getString("open_recipe");
            currentRecipe=data.getRecipe(recipe);
            categoryIndex=InfoHolder.getPlaceInArray(currentRecipe.getCategory(),InfoHolder.category);


            displayRecipe(true);
            this.toSelect=true;

            if(action.equals(InfoHolder.recipeBookActions[0])){
                forToday=true;
            }
        }
        else{
            //Just open to a recipe
            currentRecipe= data.getRecipe("Chicken Adobo");
            categoryIndex=InfoHolder.getPlaceInArray(currentRecipe.getCategory(),InfoHolder.category);

            displayRecipe(false);

        }




    }


//TODO for selection
    private void displayRecipe(boolean forSelection) {


        String[] dir=currentRecipe.getDirections();
        String display=directionString(dir);

        TextView disp= (TextView) this.findViewById(this.directions);
        disp.setText(display);

        String[] ing=currentRecipe.getIngredients();
        display=getStringwNewLine(ing);
        disp= (TextView) this.findViewById(this.ingredient);
        disp.setText(display);

        display=currentRecipe.getTitle();
        disp=(TextView) this.findViewById(this.recipeTitle);
        disp.setText(display);




        ImageView picture= (ImageView) this.findViewById(this.pic);
        int drawableID=currentRecipe.getPicture();
        picture.setImageDrawable(getResources().getDrawable(drawableID));

        if (forSelection){
            LinearLayout l = (LinearLayout) findViewById(this.id_decisionPanel);
            l.setVisibility(View.VISIBLE);

            TextView t= (TextView) findViewById(id_recipeQuestion);
            String message= (String) t.getText();

            message="Will you have this for "+InfoHolder.categoryTitle[categoryIndex]+"?";
            t.setText(message);






            ImageView image= (ImageView) this.findViewById(R.id.recipe_page_ImageView_decisionIcon);

            if(categoryIndex<3){
                image.setImageDrawable(getResources().getDrawable(InfoHolder.iconId[categoryIndex]));
            }
            else{
                image.setImageDrawable(getResources().getDrawable(InfoHolder.iconId[3]));
            }


        }



    }

    private String getStringwNewLine(String[] arr) {
        String display="";

        for (int i=0;i<arr.length;i++){
            display=display+arr[i]+"\n";
        }
        return display;
    }

    private String directionString(String[] arr){
        String display="";
        for (int i=0;i<arr.length;i++){
            arr[i]=arr[i].substring(2);
            String step=Integer.toString(i + 1);
            step=step+".";
            display=display+step+"\t"+arr[i]+"\n"+"\n";
        }

        return display;

    }




    public void onDecision(View v){

        Intent i=new Intent();



            Day d=data.getToday(selectForDate);


            d.putRecipe(currentRecipe);
            data.putToday(d);



            if(forToday){
                i.setAction("today");
            }
            else{
                i.setAction("calendar");
                i.putExtra("date",selectForDate);
            }


            i.putExtra("date",this.getIntent().getIntArrayExtra("date"));

            startActivity(i);

        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();

        return true;

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }







    }
