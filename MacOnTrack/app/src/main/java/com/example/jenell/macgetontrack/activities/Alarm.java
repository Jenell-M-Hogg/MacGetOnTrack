package com.example.jenell.macgetontrack.activities;

import android.app.Activity;

import android.os.Bundle;

import android.widget.TextView;

import com.example.jenell.macgetontrack.database.DataBaseHandler;
import com.example.jenell.macgetontrack.database.Day;
import com.example.jenell.macontrack.R;

import java.text.SimpleDateFormat;

public class Alarm extends Activity{
    DataBaseHandler data;
    private final String[] ratio={"You ate 3/3 paleo meals today!","You ate 2/3 paleo meals today!","You ate 1/3 paleo meals today...","You ate 0/3 paleo meals today..."};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        data=new DataBaseHandler(this);

        Day d=data.getToday(getCurrentDate());



        boolean[] s = d.getSuccesses();
        int count=countSuccess(s);

        displayMessage(count);


    }


    private void displayMessage(int count) {
        String user=data.getCurrentUser();

        String pmessage=null;
        String ratio=null;
        if (count==0){
           ratio=this.ratio[3];
           pmessage=user+" , I'm getting worried about you. Tomorrow's another day, so don't give up!";
        }
        else if (count==1){
            ratio=this.ratio[2];
            pmessage=user+" , what happened yo? Don't give up!";

        }
        else if(count==2){
            ratio=this.ratio[1];
            pmessage=user+". You were so close!";
        }
        else if(count==3){
            ratio=this.ratio[0];
            pmessage="Excellent job, "+user;
        }

        TextView r= (TextView) this.findViewById(R.id.alarm_TextView_ratio);
        TextView p= (TextView) this.findViewById(R.id.alarm_textView_pmessage);

        r.setText(ratio);
        p.setText(pmessage);

    }

    private int countSuccess(boolean[] s) {
        int sum = 0;
        for (int i=0;i<s.length;i++){
            if(s[i]){
                sum=sum+1;
            }
        }
        return sum;
    }

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




}
