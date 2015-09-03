
package com.example.jewellery.bearer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    LinearLayout order,call;
    String COUNTER_NUMBER;
    public static SQLiteDatabase db;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        try{ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");

        COUNTER_NUMBER = "0";
        db = openOrCreateDatabase("dineart_counter.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
        createTables();

        order=(LinearLayout)findViewById(R.id.orders);
        call=(LinearLayout)findViewById(R.id.calls);

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://dineart.firebaseio.com");


        ChildEventListener cleanTableChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cleanTableCall(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
        ref.child("cleanTable").addChildEventListener(cleanTableChildEventListener);


        ChildEventListener orderChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                order(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
        ref.child("bearerOrders").addChildEventListener(orderChildEventListener);

        ChildEventListener waiterChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue().toString().equals("occupied")) tableOccupied();
                else call(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
        ref.child("waiterCalls").child(COUNTER_NUMBER).addChildEventListener(waiterChildEventListener);
    }

    public void createTables(){
        try{
            db.execSQL("CREATE TABLE items(title TEXT, category NUMBER,position NUMBER,price TEXT);");
        }catch(Exception e){}
    }

    public void order(String theOrder){
        String[] s = theOrder.split(";");
        if(s[0].equals(COUNTER_NUMBER)) {
            for (int i = 2; i < s.length; i++) {
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                String[] parts = s[i].split("_");
                tv.setText(ItemDetails.getTitle(Integer.parseInt(parts[0]),Integer.parseInt(parts[1])) + " - " + Integer.parseInt(parts[2]));
                tv.setTextSize(18);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                tv.setLayoutParams(params);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setTextColor(Color.parseColor("#555555"));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                    }
                });
                tv.setBackgroundColor(Color.argb(0, 55, 60, 70));
                order.addView(tv);
            }
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.carme);
            mediaPlayer.start();
        }
    }


    public void tableOccupied(){
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setText("Table "+COUNTER_NUMBER+" will be occupied shortly!");
        tv.setTextSize(18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        tv.setLayoutParams(params);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#555555"));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        tv.setBackgroundColor(Color.argb(0, 55, 60, 70));
        call.addView(tv);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.carme);
        mediaPlayer.start();
    }

    public void call(String string){
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        if(string.equals("call;")) tv.setText("Table calling!");
        else tv.setText("Table calling - " + string.split(";")[1]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        tv.setLayoutParams(params);
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#555555"));
        tv.setBackgroundColor(Color.argb(0, 55, 60, 70));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        call.addView(tv);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.carme);
        mediaPlayer.start();
    }

    public void cleanTableCall(String string){
        if(string.equals(COUNTER_NUMBER)){
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setText("Clean Table - " + string + "!");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            tv.setLayoutParams(params);
            tv.setTextSize(18);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.parseColor("#555555"));
            tv.setBackgroundColor(Color.argb(0, 55, 60, 70));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                }
            });
            call.addView(tv);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.carme);
            mediaPlayer.start();
        }
    }

    public void downloadEverything() {
        if (checkConnection()) {
            dialog = ProgressDialog.show(this, null, "Just a moment...", true);
            final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FoodItems");
            query2.orderByAscending("category");
            query2.addAscendingOrder("position");
            query2.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        db.execSQL("DELETE FROM items;");
                        for (ParseObject ob : objects) {
                            db.execSQL("INSERT INTO items VALUES('" + ob.getString("title") + "'," + ob.getNumber("category") + "," + ob.getNumber("position") + ",'" + ob.getNumber("price") + "');");
                        }
                        dialog.dismiss();
                    } else {
                        Log.e("PARSE", "Error: " + e.getMessage());
                    }
                }
            });
        }else{
            Toast.makeText(this,"Check your Internet Connection!",Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null ) return false;
        else return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.downloadDetails) {
            downloadEverything();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}