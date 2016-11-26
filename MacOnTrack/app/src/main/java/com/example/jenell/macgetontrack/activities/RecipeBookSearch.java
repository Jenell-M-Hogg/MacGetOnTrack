package com.example.jenell.macgetontrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.jenell.macontrack.R;
import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macgetontrack.widgets.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeBookSearch extends ActionBarActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    DataBaseHandler data;
    boolean toSelect=false;
    boolean forToday=false;

    ArrayList<String>[] titles=new ArrayList[4];

    private String[] headers=new String[]{"Breakfast","Lunch","Dinner","Snacks/Drinks"};

    int categoryIndex;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_book_search);
        data=new DataBaseHandler(this.getApplicationContext());
        setTitle("");
        try{
            categoryIndex=this.getIntent().getExtras().getInt("category");

        }
        catch(Exception e){

        }

        //Initialize the titles
        this.setUpTitles();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        //Read the intent and adjust the layout accordingly
        readIntent();


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.recipebs_expandableListView_find);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        //Add the listeners to the children of the list view
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                toRecipePage(groupPosition,childPosition);

                return true;
            }
        });


        if(this.toSelect){
            for (int i=0;i<this.listDataHeader.size();i++){
                expListView.expandGroup(i);
            }
        }
        else{
            TextView t= (TextView) this.findViewById(R.id.search_recipe_TextView_Title);
            t.setText("Browse Through Recipes");
        }



    }

    private void readIntent() {


        String action = this.getIntent().getAction();
        this.toSelect=false;
        this.forToday=false;
        int ind=0;


        //If you're searching from the today page
        for (int i=0;i<InfoHolder.searchForTodayActions.length;i++){
            if(action.equals(InfoHolder.searchForTodayActions[i])){
                this.toSelect=true;
                this.forToday=true;
                ind=i;

            }


        }
        //If you're searching from the calendar page
        for(int i=0;i<InfoHolder.searchForDateActions.length;i++){
            if(action.equals(InfoHolder.searchForDateActions[i])){
                toSelect=true;

                ind=i;
            }
        }

        if (toSelect){
            this.setUpListCategory(ind);
        }


        else{
           setUpTotalList();
        }

    }

    private void setUpTotalList() {

        for(int i=0;i<headers.length;i++){
            setUpListCategory(i);
        }
    }

    private void setUpListCategory(int ind) {
        //Displays only one category on the expandable list view


        // Adding child data
        listDataHeader.add(this.headers[ind]);


        List<String> breaky=this.titles[ind];
        listDataHeader.size();

        listDataChild.put(listDataHeader.get(listDataHeader.size()-1), breaky); // Header, Child data

    }

    private void toRecipePage(int group,int child) {

        Intent in=new Intent();
        ArrayList<String> recipeGroup;
        String r;


        if(!this.toSelect){
            recipeGroup=titles[group];
            r=recipeGroup.get(child);
        }
        else{


            recipeGroup=titles[categoryIndex];
            r=recipeGroup.get(child);
        }





        in.setAction("recipeBook");
        in.putExtra(InfoHolder.open_recipe,r);

        String act;
        if(toSelect){
            if(forToday){
              //Select for today!
              act=InfoHolder.recipeBookActions[0];

            }
            else{
              act=InfoHolder.recipeBookActions[1];

            }
            int[] m=this.getIntent().getIntArrayExtra("date");
            in.putExtra("date",m);

            in.setAction(act);
        }

        in.putExtra("forToday",this.forToday);

        startActivity(in);

    }



    private void setUpTitles() {
        for (int i=0;i<4;i++){
            this.titles[i]= (ArrayList<String>) data.getRecipeTitles(InfoHolder.category[i]);

        }
    }



    @Override
    public void onBackPressed(){
        if(forToday){
            Intent i=new Intent("today");
            startActivity(i);
        }
        else if(toSelect){
            Intent i=new Intent("calendar");
            startActivity(i);
        }
        else{
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();

        return true;

    }
}
