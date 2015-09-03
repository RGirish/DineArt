package com.iclub.dineart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;

public class Categories extends ActionBarActivity {

    int n90,n2,NO_C;
    public static SQLiteDatabase db;
    Firebase ref;
    public static String PHONE_NUMBER;
    public static boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDimensions();

        Intent i = getIntent();
        //Toast.makeText(this,i.getStringExtra("phone"),Toast.LENGTH_SHORT).show();
        PHONE_NUMBER = i.getStringExtra("phone");

        if(checkConnection()){
            Firebase.setAndroidContext(this);
            ref = new Firebase("https://dineart.firebaseio.com");
        }

        try{ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");

        db=openOrCreateDatabase("dineart_table.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
        createTables();

        try {
            Cursor c = db.rawQuery("SELECT COUNT(code) FROM categories;", null);
            c.moveToFirst();
            NO_C = c.getInt(0);
        }catch(Exception e){NO_C=0;}


        try {

            for(int category=0;category<NO_C;++category){
                Cursor c2 = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
                c2.moveToFirst();
                Integer[] row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.orders.add(row);
                row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.orders_p.add(row);
                row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.likes.add(row);
            }

        }catch(Exception e){}

        addCategories();

        //On click function and on touch bg change for MyOrder
        final LinearLayout myorderll=(LinearLayout)findViewById(R.id.myorderll);
        myorderll.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        myorderll.setBackgroundResource(R.drawable.orange_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        myorderll.setBackgroundResource(R.drawable.orange_normal);
                        startActivity(new Intent(Categories.this,MyOrder.class));
                        break;
                }
                return true;
            }
        });

        Intent intent=getIntent();
        int firsttime = intent.getIntExtra("firsttime",2);
        if(firsttime==32){
            flag=true;
            final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.order_placed);
            Button okBtn=(Button)dialog.findViewById(R.id.okBtn);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    public void createTables(){
        try{
            db.execSQL("CREATE TABLE categories(code NUMBER);");
        }catch(Exception e){}
        try{
            db.execSQL("CREATE TABLE items(title TEXT, category NUMBER,position NUMBER,desc TEXT,ingre TEXT,price TEXT,likes NUMBER,dislikes NUMBER);");
        }catch(Exception e){}
        try{
            db.execSQL("CREATE TABLE mytable(code TEXT);");
        }catch(Exception e){}
    }

    //import dimensions from dimens.xml to local variables
    public void setDimensions(){
        n90=(int)getResources().getDimension(R.dimen.n90);
        n2=(int)getResources().getDimension(R.dimen.n2);
    }

    //add images for all the categories
    public void addCategories(){
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        LinearLayout mainll=(LinearLayout)findViewById(R.id.mainll);
        LinearLayout ll=new LinearLayout(this);
        ImageView iv;
        LinearLayout.LayoutParams params;

        for(int id=0;id<NO_C;++id){
            final int id2=id;
            if(id==0 || id%2==0){
                ll=new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if(id==0){params.setMargins(0,n90,0,0);}
                ll.setLayoutParams(params);
            }
            iv=new ImageView(this);
            iv.setOnTouchListener( new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            v.setAlpha(0.9f);
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setAlpha(1f);
                            Intent intent=new Intent(Categories.this, ItemsList.class);
                            Bundle bundle=new Bundle();
                            bundle.putInt("id",id2);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            v.setAlpha(1f);
                            break;
                    }
                    return true;
                }
            });
            params=new LinearLayout.LayoutParams(width/2,width/2);
            params.setMargins(0,n2,n2,n2);
            iv.setLayoutParams(params);
            Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/dineart/categories/" + (id) + ".jpg");
            iv.setImageBitmap(bitmap);
            ll.addView(iv);
            if(id==0 || id%2==0){
                mainll.addView(ll);
            }
        }
   }

    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null ) return false;
        else return true;
    }

    public void onClickCallWaiter(View view){
        String code;
        Cursor c = db.rawQuery("SELECT code FROM mytable;", null);
        try {
            c.moveToFirst();
            code = c.getString(0);
            final String code2 = code;
            final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.call_waiter);
            Button yesBtn=(Button)dialog.findViewById(R.id.yesBtn);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText temp = (EditText)findViewById(R.id.waiterMessage);
                    String message = temp.getText().toString();
                    ref.child("waiterCalls").child(code2).push().setValue("call;"+message);
                    dialog.dismiss();
                    temp.setText("");
                    Toast.makeText(Categories.this,"Your waiter is on the way!",Toast.LENGTH_LONG).show();
                }
            });
            Button noBtn=(Button)dialog.findViewById(R.id.noBtn);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Toast.makeText(Categories.this,"Waiter call cancelled!",Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }catch (Exception e){
            Toast.makeText(this,"Table Number yet to be set!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void onClickClear(View view){
        final Dialog clearDialog = new Dialog(this);
        clearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        clearDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        clearDialog.setContentView(R.layout.clear);
        Button clearBtn=(Button)clearDialog.findViewById(R.id.clearButton);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp = (EditText)clearDialog.findViewById(R.id.clearPassword);
                if(temp.getText().toString().equals("1994")) {
                    refreshLikesAndDislikesThenFinish();
                    Cursor c = db.rawQuery("SELECT code FROM mytable;",null);
                    c.moveToFirst();
                    ref.child("freeTables").push().setValue(c.getString(0));
                    clearDialog.dismiss();
                }
            }
        });
        clearDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            final Dialog clearDialog = new Dialog(this);
            clearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            clearDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            clearDialog.setContentView(R.layout.clear);
            Button clearBtn=(Button)clearDialog.findViewById(R.id.clearButton);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText temp = (EditText)clearDialog.findViewById(R.id.clearPassword);
                    if(temp.getText().toString().equals("1994")) {
                        refreshLikesAndDislikesThenFinish();
                        Cursor c = db.rawQuery("SELECT code FROM mytable;",null);
                        c.moveToFirst();
                        ref.child("freeTables").push().setValue(c.getString(0));
                        clearDialog.dismiss();
                    }
                }
            });
            clearDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshLikesAndDislikesThenFinish(){

        final ProgressDialog pd = ProgressDialog.show(this,null,"Just a moment...");

        try {

            MyOrder.orders.clear();
            MyOrder.orders_p.clear();

            for(int category=0;category<NO_C;++category){
                Cursor c2 = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
                c2.moveToFirst();
                Integer[] row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.orders.add(row);
                row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.orders_p.add(row);
                row = new Integer[c2.getInt(0)];
                for (int s = 0; s < c2.getInt(0); ++s) {
                    row[s] = 0;
                }
                MyOrder.likes.add(row);
            }

        }catch(Exception e){}

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodItems");
        query.selectKeys(Arrays.asList("category", "position" , "likes" , "dislikes"));
        query.orderByAscending("category");
        query.addAscendingOrder("position");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(ParseObject ob : objects){
                        db.execSQL("UPDATE items SET likes="+ob.getNumber("likes")+", dislikes = "+ob.getNumber("dislikes")+" WHERE category="+ob.getNumber("category")+" AND position="+ob.getNumber("position")+";");
                    }
                    pd.dismiss();
                    finish();
                }else{
                    Log.e("PARSE", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed(){}

}