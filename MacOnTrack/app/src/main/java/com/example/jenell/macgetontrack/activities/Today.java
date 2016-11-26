package com.example.jenell.macgetontrack.activities;


import android.app.AlarmManager;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;

import android.content.Intent;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jenell.macgetontrack.AlarmReceiver;
import com.example.jenell.macontrack.R;
import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macgetontrack.database.Day;

import java.text.SimpleDateFormat;


public class Today extends ActionBarActivity {
    DataBaseHandler data;
    private int[] currentDate;
    private final int[] boxes={R.id.today_CheckBox_breakfastSuccess,R.id.today_CheckBox_lunchSuccess,R.id.today_CheckBox_dinnerSuccess};
    private final int[] tL={R.id.today_TextView_breakfastTime,R.id.today_TextView_lunchTime,R.id.today_TextView_dinnerTime,R.id.today_TextView_endOfDay};
    private final int[] rL={R.id.today_TextView_breakfastrecipe,R.id.today_TextView_lunchRecipe,R.id.today_TextView_dinnerRecipe,R.id.today_TextView_drink1,R.id.Today_TextView_drink2,R.id.Today_TextView_drink3};
    private final int[] layouts={R.id.today_LinearLayout_breakfast,R.id.today_LinearLayout_lunch,R.id.today_LinearLayout_dinner};
    Day day;
    private final int[] pics={R.id.today_ImageView_breakfastPic,R.id.today_ImageView_lunchPic,R.id.today_ImageView_dinnerPic};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);


        data=new DataBaseHandler(this.getApplicationContext());

        String user=data.getCurrentUser();
        String pmessage=user+"'s Day";
        setTitle(pmessage);

        currentDate=this.getCurrentDate();
        day=data.getToday(currentDate);

        makeTodayCurrent();
        setUp();


    }





    private void makeTodayCurrent(){
        //This method sets every part of the GUI to what it should be based on the most recent date.

        //Day is initialized to be whatever from the data base.

        //Figure out whether to check off check boxes
        boolean b[]=day.getSuccesses();
        //Get the times
        String[]times=day.getTimes();
        //Get the recipes(These may or may not be null
        String[]recipes=day.getRecipes();

        //Set each part of the GUI
        for (int i=1;i<3;i++){
            CheckBox c= (CheckBox) this.findViewById(this.boxes[i]);
            c.setActivated(b[i]);
            c.setChecked(b[i]);

            TextView t= (TextView) this.findViewById(this.tL[i]);
            t.setText(times[i]);

            t= (TextView) this.findViewById(this.rL[i]);
            t.setText(recipes[i]);
        }
        //Set the end of the day
        TextView t= (TextView) this.findViewById(tL[3]);
        t.setText(day.getEndOfDay());
    }




    //FINISHED
    //Get the current date
    public int[] getCurrentDate(){
        java.util.Calendar now = java.util.Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = formatter.format(now.getTime());
        String[] separateCurrentDate = nowDate.split("-");
        String year = separateCurrentDate[0];
        String month = separateCurrentDate[1];
        String day = separateCurrentDate[2];
        int currentYear = Integer.parseInt(year);
        int currentMonth = Integer.parseInt(month);
        int currentDay = Integer.parseInt(day);

        int[] date= {currentDay,currentMonth,currentYear};
        return date;
    }



    private void setUp() {
        //This method finds all the items on the GUI and adds listeners to the necessary ones
        //This method looks to see if there is any data recorded for the current date and updates the page accordingly.
        // (Displays the recipes, displays the time, displays the end of the day, etc.
        
      for (int i=0;i<3;i++){
          //Update what the success graphics should look like based on the current day
         updateSuccessGraphics(i,day.getSuccesses()[i]);
         
          //Update the recipe graphics (The title, the picture)
          updateRecipeGraphics(day.getRecipes()[i],i);
          //Update the time graphics (Without setting a new alarm)
          updateTimeGraphics(day.getTimes()[i],i);

          this.addListenersToMealSlots(rL[i],tL[i],boxes[i],pics[i]);

          
          
      }

        TextView end= (TextView) this.findViewById(tL[3]);
        end.setClickable(true);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeChange(tL[3]);
            }
        });

        //Add listeners to the drinks
        for(int i=3;i<rL.length;i++){
            TextView t= (TextView) this.findViewById(rL[i]);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startRecipeChange(v);
                }
            });
        }
       


      }






    private void updateTimeGraphics(String newTime,int i) {
        //Update the time graphics. DON'T TOUCH THE DATABASE OR ALARM. The int i refers to breakfast, lunch, or dinner
        TextView t= (TextView) this.findViewById(this.tL[i]);
        t.setText(newTime);

    }


    private void updateRecipeGraphics(String newRecipe, int i) {
        //This method updates the graphics ONLY (Updates the picture, the recipe title)
        //The int i refers to breakfast, lunch, or dinner

        TextView r= (TextView) this.findViewById(rL[i]);
        r.setText(newRecipe);



        if (!newRecipe.equals("Choose a recipe")){
            //TODO DEBUG recipe picture changing
            ImageView recipePic= (ImageView) this.findViewById(pics[i]);

            //

            int drawableID=data.getRecipe(newRecipe).getPicture();
            recipePic.setImageDrawable(getResources().getDrawable(drawableID));
        }



    }


    private void addListenersToMealSlots(int recipe, int time, int checkBox,int pic) {
        //Given the id's of the GUI components in the meal slot, add listeners to each part

        TextView text= (TextView) findViewById(recipe);
        text.setClickable(true);
        //Add an OnClickListener to the recipe title
        View.OnClickListener l= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecipeChange(v);

            }
        };

        text.setOnClickListener(l);

        ImageView imageView= (ImageView) findViewById(pic);
        imageView.setOnClickListener(l);

        //Add a listener to the time
        text= (TextView) findViewById(time);
        text.setClickable(true);
        l=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeChange(v.getId());
            }
        };
        text.setOnClickListener(l);

        //Add a listener to the checkBox
        CheckBox c= (CheckBox) findViewById(checkBox);
        l=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CheckBox c= (CheckBox) v;
                boolean isChecked=c.isActivated();

                updateSuccess(!isChecked, v.getId());
            }
        };
        c.setOnClickListener(l);



    }

    private void updateSuccess(boolean success, int id) {

        //Given the new success and the id of the checkbox that was ticked/unticked
        


        int i=0;
        //Update the database to include the new found success
        if (id==R.id.today_CheckBox_breakfastSuccess){
            i=0;
        }
        else if (id==R.id.today_CheckBox_lunchSuccess){
            i=1;
        }
        else if (id == R.id.today_CheckBox_dinnerSuccess){
            i=2;
        }
        //Get the successes from the current day
        boolean[] suc=day.getSuccesses();
        //Update whatever success was changed
        suc[i]=success;

        day.setSuccesses(suc);
        
        //Update the graphics
        updateSuccessGraphics(i,success);


        //Update the database
        this.data.putToday(day);

    }

    private void updateSuccessGraphics(int i, boolean success) {
        //Given an int i (0=breakfast, 1=lunch...) and a new success value, update the GUI accordingly
        
        //Update the check box
        CheckBox c= (CheckBox) findViewById(boxes[i]);
        c.setActivated(success);
        c.setChecked(success);



        //Update the layout
        LinearLayout l = (LinearLayout) this.findViewById(layouts[i]);



        if(success){
          l.setBackgroundColor(getResources().getColor(R.color.trans_green));
          }

        else{
          l.setBackgroundColor(getResources().getColor(R.color.trans_white));
        }

        
    }

    private void startTimeChange(int id) {
        //TODO This is when a user clicks a time on the today page, indicating they wish to change it

        //Given the id of the textview that was clicked... start the dialogue and display a new time, if necessary
        int m=3;
        for (int i=0;i<tL.length;i++){
            if (id==tL[i]){
                m=i;

            }
        }

        createTimeDialog(m);



            //Identified whether the breakfast, lunch, or dinner was called, then start the dialog for a result


        }

    private void createTimeDialog(final int i) {

        final Context context=this;
        final TimePickerDialog.OnTimeSetListener callBack=new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String newTime="";
                newTime=Integer.toString(hourOfDay);
                newTime=newTime+":";
                if(minute<10){
                    newTime=newTime+"0";
                }
                newTime=newTime+Integer.toString(minute);




                updateTime(newTime, i);
            }

        };




        TextView text= (TextView) this.findViewById(tL[i]);
        String time= (String) text.getText();
        int[] hm=parseTime(time);

        TimePickerDialog t=new TimePickerDialog(context, callBack,hm[0],hm[1],true);

        t.show();



    }
//TODO DE
    private int[] parseTime(String time) {
        int length=time.length();
        String minute;
        String hour;
        if(length==5){
            minute=time.substring(3,5);
            hour=time.substring(0,2);
        }
        else{
            minute=time.substring(2,4);
            hour=time.substring(0,1);
        }
        int m=Integer.parseInt(minute);
        int h=Integer.parseInt(hour);
        int[] hm=new int[]{h,m};
        return hm;
    }

    private void startRecipeChange(View v) {
        //TODO This is when a user clicks a recipe title on the today page, indicating they wish to change it
        //Given the id of the textview that was clicked
        int id=v.getId();
        int m=0;
        for (int i=0;i<rL.length;i++){
            if (id==rL[i]){
                m=i;
            }
            if(i<2){
                if (id==pics[i]){
                    m=i;
                }
            }

        }
            createRecipeDialog(m);

    }

    private void createRecipeDialog(int m) {
        Intent intent=new Intent();
        int categoryIndex=-1;


        if(m==0){

           intent.setAction("searchForBreakfastToday");
        }
        if(m==1){

            intent.setAction("searchForLunchToday");

        }
        if(m==2){

            intent.setAction("searchForDinnerToday");

        }
        if(m>=3){
            intent.setAction("searchForSndrToday");
            intent.putExtra("drinkIndex",m);
        }
        intent.putExtra("category",m);
        intent.putExtra("date",InfoHolder.stringToIntArray(day.getDate()));
        intent.putExtra("forToday",true);


        this.startActivity(intent);

    }



    //TODO DEBUG
    public  void updateTime(String newTime, int i) {

        if(i==3){
            day.setEndOfDay(newTime);

            startAlarm(newTime);
        }
        else{
            String[] ts=day.getTimes();
            ts[i]=newTime;
            day.setTimes(ts);
        }

        data.putToday(day);

        updateTimeGraphics(newTime,i);




    }


    //TODO FUNCTIONAL CANCEL ALARMS
    private void startAlarm(String newTime) {


        int hm[]=parseTime(newTime);
        // Set the alarm to start at approximately 2:00 p.m.
       java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(calendar.HOUR_OF_DAY, hm[0]);
        calendar.set(calendar.MINUTE, hm[1]);




        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);



        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show();



    }
}
