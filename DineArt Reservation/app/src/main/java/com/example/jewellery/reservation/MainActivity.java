package com.example.jewellery.reservation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements SwipeInterface{
    LinearLayout cont;
    AlarmManager am;
    int step = 0,dateset=0;
    String vname="",vphone="",vpipl="",vdate="When should we be ready for you ??";
    Date curr,set,open,closed;
    SimpleDateFormat sdf;
    Context cc;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cc=this;
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://dineart.firebaseio.com");
        try{ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");
        am = (AlarmManager)getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, Loclisten.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,101,intent,0);
        Calendar call = Calendar.getInstance();
        call.add(Calendar.HOUR,4);
        set = call.getTime();
        sdf = new SimpleDateFormat("HH-mm-dd-MM-yyyy");
        cont = (LinearLayout)findViewById(R.id.main);
        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
        LinearLayout swipe_layout = (LinearLayout) findViewById(R.id.main);
        swipe_layout.setOnTouchListener(swipe);
        TextView tv = new TextView(this);
        tv.setText("Welcome to The Williamsburg Diner !\nWe are Open 24*7 9:00am to 11:00pm");
        tv.setTextSize(40);
        tv.setTextColor(Color.BLACK);
        cont.addView(tv);
    }

      private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.DATE,3);
         Calendar temp=Calendar.getInstance();
         temp.setTime(date);
         temp.set(Calendar.HOUR_OF_DAY,9);
         temp.set(Calendar.MINUTE,00);
         open = temp.getTime();
         temp.setTime(date);
         dateset=0;
         temp.set(Calendar.HOUR_OF_DAY,23);
         temp.set(Calendar.MINUTE,00);
         closed=temp.getTime();
         Date d2 = cal.getTime();
            if((date.after(d2))||(date.before(curr))||(date.after(closed))||(date.before(open))){
             Toast.makeText(cc,"Invalid time set !!",Toast.LENGTH_SHORT).show();
                Calendar call = Calendar.getInstance();
                call.add(Calendar.HOUR,3);
                final Date d = call.getTime();
                curr=d;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                     .setListener(listener)
                     .setInitialDate(d)
                     .build()
                     .show();
                vdate="When should we be ready for you ??";
                TextView tv = (TextView)cont.findViewWithTag("date");
                tv.setText(vdate);
            }
            else{
                 vdate = sdf.format(date);
                 dateset=1;
                set = date;
                TextView tv = (TextView)cont.findViewWithTag("date");
                tv.setText(vdate);
            }
        }
        @Override
        public void onDateTimeCancel()
        {
            vdate="When should we be ready for you ??";
            TextView tv = (TextView)cont.findViewWithTag("date");
            tv.setText(vdate);
            dateset=0;
        }
    };


    @Override
    public void bottom2top(View v) {  }

    @Override
    public void left2right(View v) {
    switch(step){
        case 5:  step=4;
                TextView dated = new TextView(this);
                dated.setTextSize(20);
                dated.setText(vdate);
                dated.setTag("date");
                curr=Calendar.getInstance().getTime();
                dated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener)
                            .setInitialDate(set)
                            .build()
                            .show();
                    }
                });
                cont.removeAllViews();
                cont.addView(dated);
                     break;
        case 4: step=3;
                EditText pipl = new EditText(this);
                pipl.setInputType(InputType.TYPE_CLASS_NUMBER);
                pipl.setTextSize(20);
                pipl.setTag("pipl");
                pipl.setHint("How many in the company ??");
                pipl.setText(vpipl);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(2);
                pipl.setFilters(FilterArray);
                cont.removeAllViews();
                cont.addView(pipl);
                break;
        case 3: step=2;
                vpipl = ((EditText)cont.findViewWithTag("pipl")).getText().toString();
                EditText phone = new EditText(this);
                phone.setHint("We shall call you at ?");
                phone.setText(vphone);
                phone.setInputType(InputType.TYPE_CLASS_PHONE);
                phone.setTextSize(20);
                phone.setTag("phone");
                cont.removeAllViews();
                cont.addView(phone);
                break;
        case 2: step=1;
                vphone = ((EditText)cont.findViewWithTag("phone")).getText().toString();
                EditText name = new EditText(this);
                name.setHint("It's a Pleasure to meet you !");
                name.setTextSize(20);
                name.setTag("name");
                name.setText(vname);
                cont.removeAllViews();
                cont.addView(name);
                break;
        case 1: step=0;
                vname = ((EditText)cont.findViewWithTag("name")).getText().toString();
                TextView tv = new TextView(this);
                tv.setText("Welcome to The Williamsburg Diner !");
                tv.setTextSize(30);
                tv.setTextColor(Color.BLACK);
                cont.removeAllViews();
                cont.addView(tv);
                break;

    }
    }

    @Override
    public void right2left(View v) {
switch(step){
    case 0: step=1;
            EditText name = new EditText(this);
            name.setHint("It's a Pleasure to meet you !");
            name.setTextSize(20);
            name.setText(vname);
            name.setTag("name");
            cont.removeAllViews();
            cont.addView(name);
            break;
    case 1: vname = ((EditText) cont.findViewWithTag("name")).getText().toString();
            if(!(vname.equals(""))) {
            step = 2;
            EditText phone = new EditText(this);
            phone.setHint("We shall call you at ?");
            phone.setInputType(InputType.TYPE_CLASS_PHONE);
            phone.setTextSize(20);
            phone.setText(vphone);
            phone.setTag("phone");
            cont.removeAllViews();
            cont.addView(phone);
            }
            else {
            Toast.makeText(this,"Please Enter a Valid Name !",Toast.LENGTH_SHORT).show();
            }
            break;
    case 2: vphone = ((EditText)cont.findViewWithTag("phone")).getText().toString();
            if(vphone.length()>10||vphone.length()<10){
                Toast.makeText(this,"Please Enter a Valid Phone Number !!",Toast.LENGTH_SHORT).show();
            }
        else {
                step=3;
                EditText pipl = new EditText(this);
                pipl.setInputType(InputType.TYPE_CLASS_NUMBER);
                pipl.setTextSize(20);
                pipl.setTag("pipl");
                pipl.setHint("How many in the company ??");
                pipl.setText(vpipl);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(2);
                pipl.setFilters(FilterArray);
                cont.removeAllViews();
                cont.addView(pipl);
            }
            break;
    case 3: vpipl = ((EditText)cont.findViewWithTag("pipl")).getText().toString();
            if((Integer.parseInt(vpipl)>15)||(Integer.parseInt(vpipl)<2)){
                Toast.makeText(this,"Please enter a value between 2 and 15",Toast.LENGTH_SHORT).show();
            }
            else {
                step = 4;
                TextView dated = new TextView(this);
                dated.setTextSize(20);
                dated.setText(vdate);
                dated.setTag("date");
                curr = Calendar.getInstance().getTime();
                dated.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                .setListener(listener)
                                .setInitialDate(set)
                                .build()
                                .show();
                    }
                });
                cont.removeAllViews();
                cont.addView(dated);
            }
            break;
    case 4: if(dateset==1){
            step=5;
            TextView tv = new TextView(this);
            tv.setTextSize(20);
            tv.setText("A company of "+vpipl+" with "+vname+" to be contacted at "+vphone+" is expected at "+vdate+" to dine with us ! \n The Williamsburg Diner wil be happy to host you !!");
            cont.removeAllViews();
            cont.addView(tv);
            }
        else{
            Toast.makeText(this,"Set a Valid Date between next 3 hours to 3 days !",Toast.LENGTH_SHORT).show();
            }
            break;
    case 5: step = 6;
            Toast.makeText(this,"Thanks for Trusting us with your reservation. \n You Sir are awesome !",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this,Loclisten.class);
            in.putExtra("phone",vphone);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(set);
            cal2.set(Calendar.HOUR_OF_DAY,9);
            cal2.set(Calendar.MINUTE,00);
            PendingIntent pi = PendingIntent.getService(this,0,in,0);
            am.set(AlarmManager.RTC,cal2.getTimeInMillis(),pi);
            cal2.setTime(set);
               ParseObject gameScore = new ParseObject("Reservation");
                gameScore.put("name", vname);
                gameScore.put("phoneNumber", vphone);
                gameScore.put("seats", vpipl);
                gameScore.put("date", String.valueOf(cal2.get(Calendar.DATE)));
                gameScore.put("hour", String.valueOf(cal2.get(Calendar.HOUR_OF_DAY)));
                gameScore.saveInBackground();
            }
    }

    @Override
    public void top2bottom(View v) {}
}
