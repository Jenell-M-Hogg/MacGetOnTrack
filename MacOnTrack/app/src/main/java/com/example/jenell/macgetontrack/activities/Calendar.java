package com.example.jenell.macgetontrack.activities;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;

import android.widget.TextView;


import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macgetontrack.database.Day;
import com.example.jenell.macontrack.R;
import com.example.jenell.macgetontrack.database.Recipe;

import java.text.SimpleDateFormat;

public class Calendar extends ActionBarActivity {
    private final int calenderID=R.id.calendarView;
    private DataBaseHandler data;
    private final int[] recipeSlot={R.id.calendar_breakdescr,R.id.calendar_lunchDescr,R.id.calendar_dinnerDescr};
    private final int[] boxes={R.id.calendar_breakSuc,R.id.calendar_lunchSuc,R.id.calendar_dinnerSuc};
    private final int[] picId={R.id.calendar_breakfastPic,R.id.calendar_luncPic,R.id.calendar_dinnerPic};

    private Day currentDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        data=new DataBaseHandler(this.getApplicationContext());



        //Set the action bar title
        String user = data.getCurrentUser();
        user=user+"'s Paleo Calendar";
        setTitle(user);

        //setUpTheCalendar
        this.setUp();


    }

    private void setUp() {
        //Set the calendar's start date
        int[] currentDate=this.getCurrentDate();
        currentDay=data.getToday(currentDate);
        int[] startDate=this.getIntent().getIntArrayExtra("date");


        if (startDate!=null){
           this.setUpCalendar(startDate[0],startDate[1],startDate[2]);
            currentDay=data.getToday(startDate);
        }
        else{


            this.setUpCalendar(currentDate[0],currentDate[1],currentDate[2]);
        }


        CalendarView calendarView=(CalendarView) findViewById(calenderID);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                int[] selected=new int[]{dayOfMonth,month+1,year};
                onDateSelected(selected);
            }
        });



        this.displayCalendarDateInfo(this.currentDay);

        //add listeners to each of the Imageviews

        for (int i=0; i<picId.length;i++){
            ImageView im= (ImageView) findViewById(picId[i]);
            im.setClickable(true);
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startRecipeChange(v);
                }


            });
        }
    }

    private int[] getCurrentDate() {

        java.util.Calendar now=java.util.Calendar.getInstance();
        //Get the current date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = formatter.format(now.getTime());
        String[] separateCurrentDate = nowDate.split("-");
        String year = separateCurrentDate[0];
        String month = separateCurrentDate[1];
        String day = separateCurrentDate[2];
        int currentYear = Integer.parseInt(year);
        int currentMonth = Integer.parseInt(month);
        int currentDay = Integer.parseInt(day);

        return new int[]{currentDay, currentMonth, currentYear};
    }

    private void startRecipeChange(View v) {
        int index=InfoHolder.getPlaceInArray(v.getId(),picId);
        String action=InfoHolder.searchForDateActions[index];

        Intent i=new Intent();
        i.setAction(action);
        i.putExtra("category", index);
        i.putExtra("date",InfoHolder.stringToIntArray(currentDay.getDate()));
        startActivity(i);


    }


    private void setUpCalendar(int currentDay, int currentMonth, int currentYear) {
        //This method prepares the GUI to display a calendar for the month
        //Retrive an array of image buttons representing the calendar view
        CalendarView c= (CalendarView) findViewById(calenderID);
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        calendar.set(calendar.YEAR, currentYear);
        calendar.set(calendar.MONTH, currentMonth-1);
        calendar.set(calendar.DAY_OF_MONTH, currentDay);

        long milliTime = calendar.getTimeInMillis();

          c.setDate(milliTime,true,true);
    }


    public void onDateSelected(int [] ds){

        currentDay=data.getCalendarDate(ds);
        displayCalendarDateInfo(currentDay);

    }


    private void displayCalendarDateInfo(Day d) {
       //Given the day object, display the information about that day to the user in the lower bar of the screen
        String[] r = d.getRecipes();
        boolean[] b=d.getSuccesses();

        for(int i=0;i<3;i++){
            TextView slot= (TextView) findViewById(recipeSlot[i]);
            slot.setText(r[i]);
            Recipe recipe=data.getRecipe(r[i]);


            if(r[i].equals("Choose a recipe")){
                ImageView pic= (ImageView) this.findViewById(picId[i]);
                int i1 = InfoHolder.iconId[i];
                pic.setImageDrawable(getResources().getDrawable(i1));
            }
            else{
                ImageView pic= (ImageView) this.findViewById(picId[i]);
                pic.setImageDrawable(getResources().getDrawable(recipe.getPicture()));
            }


            CheckBox box= (CheckBox) this.findViewById(boxes[i]);
            box.setChecked(b[i]);


        }

    }

    @Override
    public void onBackPressed(){
        //Override the back pressed button so it always goes to the main menu

        Intent i = new Intent();
        i.setAction("main");
        startActivity(i);
        this.finish();



    }


}
