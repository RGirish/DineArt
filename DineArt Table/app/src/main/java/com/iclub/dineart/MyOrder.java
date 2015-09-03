
package com.iclub.dineart;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyOrder extends ActionBarActivity {

    public static List<Integer[]> orders = new ArrayList<>();
    public static List<Integer[]> orders_p = new ArrayList<>();
    public static List<Integer[]> likes = new ArrayList<>();
    int n85,n70,n120,n10,n25,n5,n30,n145,NO_C,n20;
    public static SQLiteDatabase db;
    DecimalFormat df;
    double totalAmount;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setDimensions();

        try{ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");

        db=openOrCreateDatabase("dineart_table.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        try {
            Cursor c = db.rawQuery("SELECT COUNT(code) FROM categories;", null);
            c.moveToFirst();
            NO_C = c.getInt(0);
        }catch(Exception e){NO_C=0;}

        if(checkConnection()){
            Firebase.setAndroidContext(this);
            ref=new Firebase("https://dineart.firebaseio.com");
        }

        df = new DecimalFormat("####0.00");

        //On click function and on touch bg change for Place Order
        final LinearLayout myorderll = (LinearLayout) findViewById(R.id.myorderllmyorder);
        myorderll.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myorderll.setBackgroundResource(R.drawable.orange_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        myorderll.setBackgroundResource(R.drawable.orange_normal);
                        Cursor cursor=db.rawQuery("SELECT code FROM mytable;",null);
                        String tableNo="";
                        try{
                            cursor.moveToFirst();
                            tableNo = (cursor.getString(0) + ";");
                        }catch (Exception e){
                            Toast.makeText(MyOrder.this,"Table Number not set!",Toast.LENGTH_LONG).show();
                            break;
                        }
                        final String tableNo2=tableNo;
                        final Dialog dialog=new Dialog(MyOrder.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Display display = getWindowManager().getDefaultDisplay();
                        int width = display.getWidth();
                        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog.setContentView(R.layout.order_confirmation);
                        LinearLayout layout=(LinearLayout)dialog.findViewById(R.id.orderConfirmationLL);

                        for(int category=0;category<NO_C;++category){
                            Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
                            c.moveToFirst();
                            for (int item = 0; item < c.getInt(0); ++item) {
                                if (orders.get(category)[item] != 0) {

                                    TextView textView=new TextView(MyOrder.this);
                                    LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    textView.setGravity(Gravity.CENTER);
                                    params1.setMargins(0,n5,0,n5);
                                    textView.setLayoutParams(params1);
                                    textView.setText(ItemDetails.getTitle(category,item)+" - "+orders.get(category)[item]);
                                    textView.setTypeface(null,Typeface.BOLD);
                                    textView.setTextSize(16);
                                    layout.addView(textView);
                                }
                            }
                        }

                        Button confirmButton=new Button(MyOrder.this);
                        confirmButton.setText("Confirm Order");
                        confirmButton.setTypeface(null, Typeface.BOLD);
                        confirmButton.setTextSize(16);
                        confirmButton.setPadding(0, 0, 0, (int)getResources().getDimension(R.dimen.n2));
                        confirmButton.setTextColor(Color.WHITE);
                        LinearLayout.LayoutParams btnlp=new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n200), ViewGroup.LayoutParams.WRAP_CONTENT);
                        btnlp.setMargins(0,n20,0,0);
                        confirmButton.setLayoutParams(btnlp);
                        confirmButton.setBackgroundResource(R.drawable.selector_login);
                        confirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String myOrder="";
                                myOrder+=tableNo2;
                                myOrder+=(Categories.PHONE_NUMBER+";");
                                for(int category=0;category<NO_C;++category){
                                    Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
                                    c.moveToFirst();
                                    for (int item = 0; item < c.getInt(0); ++item) {
                                        if (orders.get(category)[item] != 0) {
                                            myOrder += (category + "_" + item + "_" + orders.get(category)[item] + ";");
                                            orders_p.get(category)[item] += orders.get(category)[item];
                                            orders.get(category)[item] = 0;
                                        }
                                    }
                                }

                                if(checkConnection()) {
                                    ref.child("bearerOrders").push().setValue(myOrder);
                                    ref.child("kitchenOrders").push().setValue(myOrder);
                                    ref.child("cashierOrders").push().setValue(myOrder);
                                }
                                dialog.dismiss();
                                Intent intent=new Intent(MyOrder.this,Categories.class);
                                intent.putExtra("firsttime",32);
                                intent.putExtra("phone",Categories.PHONE_NUMBER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                        layout.addView(confirmButton);

                        Button cancelButton=new Button(MyOrder.this);
                        cancelButton.setText("Cancel");
                        cancelButton.setTypeface(null, Typeface.BOLD);
                        cancelButton.setTextSize(16);
                        cancelButton.setPadding(0, 0, 0, (int)getResources().getDimension(R.dimen.n2));
                        cancelButton.setTextColor(Color.WHITE);
                        btnlp=new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n150), ViewGroup.LayoutParams.WRAP_CONTENT);
                        cancelButton.setLayoutParams(btnlp);
                        cancelButton.setBackgroundResource(R.drawable.selector_login);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        layout.addView(cancelButton);
                        dialog.show();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        myorderll.setBackgroundResource(R.drawable.orange_normal);
                        break;
                }
                return true;
            }
        });

        //Add all the ordered items to screen
        addItems();



    }


    public void addItems() {

        final LinearLayout mainll = (LinearLayout) findViewById(R.id.ordermainll);
        LinearLayout ll, ll2, ll3;
        LinearLayout.LayoutParams params;
        ImageView iv;
        Button b1, b2;
        TextView tv1, tv2;
        int count = 0, total_count_current = 0,total_count = 0;

        for(int category=0;category<NO_C;++category){
            Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
            c.moveToFirst();
            for (int item = 0; item < c.getInt(0); ++item) {
                if (orders_p.get(category)[item] != 0) {
                    total_count++;
                    totalAmount+=Double.parseDouble(ItemDetails.getPrice(category,item))*orders_p.get(category)[item];
                }
                if (orders.get(category)[item] != 0) {
                    total_count_current++;
                    total_count++;
                    totalAmount+=Double.parseDouble(ItemDetails.getPrice(category,item))*orders.get(category)[item];
                }
            }
        }

        ((TextView)findViewById(R.id.totalAmount)).setText(String.valueOf(df.format(totalAmount)));

        if (total_count != 0){
            LinearLayout temp = (LinearLayout) findViewById(R.id.totalAmountLL);
            if(total_count_current==0) {
                LinearLayout.LayoutParams params_temp = (LinearLayout.LayoutParams) temp.getLayoutParams();
                params_temp.setMargins((int) getResources().getDimension(R.dimen.mn50), 0, (int) getResources().getDimension(R.dimen.mn50), (int) getResources().getDimension(R.dimen.mn5));
                temp.setLayoutParams(params_temp);
            }
            temp.setVisibility(View.VISIBLE);
        }

        TextView textViewco=new TextView(this);
        LinearLayout.LayoutParams lparams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewco.setLayoutParams(lparams);
        textViewco.setText("Current Order");
        textViewco.setGravity(Gravity.CENTER);
        textViewco.setTextSize(21);
        textViewco.setTextColor(getResources().getColor(R.color.gray2));
        textViewco.setTypeface(null,Typeface.BOLD);
        mainll.addView(textViewco);

        Button lineco=new Button(this);
        lineco.setBackgroundColor(getResources().getColor(R.color.gray3));
        lparams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        lparams.setMargins((int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n20),0);
        lineco.setLayoutParams(lparams);
        mainll.addView(lineco);

        if (total_count_current != 0) {

            findViewById(R.id.placeOrder).setVisibility(View.VISIBLE);

            for(int category=0;category<NO_C;++category){
                Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
                c.moveToFirst();
                for (int item = 0; item < c.getInt(0); ++item) {
                    if (orders.get(category)[item] != 0) {

                        final int category2=category;
                        final int item2=item;

                        ll = new LinearLayout(this);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        ll.setBackgroundResource(R.drawable.selector_white);
                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (count == 0)params.setMargins(0,n20,0,0);
                        count++;
                        ll.setLayoutParams(params);
                        ll.setGravity(Gravity.CENTER_VERTICAL);

                        iv = new ImageView(this);
                        params = new LinearLayout.LayoutParams(0, n120);
                        params.weight = 1;
                        iv.setLayoutParams(params);
                        Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/dineart/items/" + category + "_" + item + ".jpg");
                        iv.setImageBitmap(bitmap);
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ll.addView(iv);

                        ll2 = new LinearLayout(this);
                        ll2.setOrientation(LinearLayout.VERTICAL);
                        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.weight = 2f;
                        ll2.setLayoutParams(params);
                        ll2.setPadding(n10, n10, n10, n10);

                        tv1 = new TextView(this);
                        params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER_VERTICAL;
                        tv1.setLayoutParams(params);
                        tv1.setTextSize(18);
                        tv1.setText(ItemDetails.getTitle(category, item));
                        tv1.setTypeface(null, Typeface.BOLD);
                        ll2.addView(tv1);


                        tv1 = new TextView(this);
                        params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.CENTER_VERTICAL;
                        params.setMargins(0,n5,0,0);
                        tv1.setLayoutParams(params);
                        tv1.setTextSize(16);
                        tv1.setTag(category + "_" + item);
                        tv1.setText("Rs." + String.valueOf(ItemDetails.getPrice(category2, item2) + " x " + orders.get(category2)[item2]));
                        tv1.setTypeface(null, Typeface.BOLD);
                        ll2.addView(tv1);


                        ll.addView(ll2);

                        ll3 = new LinearLayout(this);
                        params = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);
                        params.weight = 0.5f;
                        ll3.setLayoutParams(params);
                        ll3.setGravity(Gravity.CENTER);
                        ll3.setOrientation(LinearLayout.VERTICAL);

                        b1 = new Button(this);
                        params = new LinearLayout.LayoutParams(n25, n25);
                        params.setMargins(0, 0, 0, n5);
                        b1.setLayoutParams(params);
                        b1.setBackgroundResource(R.drawable.selector_arrowhead);
                        b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TextView tvtemp=(TextView)mainll.findViewWithTag(category2+";"+item2);
                                tvtemp.setText(String.valueOf(Integer.parseInt(tvtemp.getText().toString())+1));
                                orders.get(category2)[item2]++;
                                TextView tvtemp2=(TextView)mainll.findViewWithTag(category2+"_"+item2);
                                tvtemp2.setText("Rs." + String.valueOf( ItemDetails.getPrice(category2,item2)  + " x " + orders.get(category2)[item2] ));
                                totalAmount+=Double.parseDouble(ItemDetails.getPrice(category2, item2));
                                ((TextView)findViewById(R.id.totalAmount)).setText(String.valueOf(df.format(totalAmount)));
                            }
                        });
                        ll3.addView(b1);


                        tv2 = new TextView(this);
                        params = new LinearLayout.LayoutParams(n30, n30);
                        tv2.setLayoutParams(params);
                        tv2.setGravity(Gravity.CENTER);
                        tv2.setTextColor(getResources().getColor(R.color.gray1));
                        tv2.setTextSize(18);
                        tv2.setTag(category+";"+item);
                        tv2.setText(String.valueOf(orders.get(category)[item]));
                        tv2.setTypeface(null, Typeface.BOLD);
                        ll3.addView(tv2);


                        b2 = new Button(this);
                        params = new LinearLayout.LayoutParams(n25, n25);
                        params.setMargins(0, n5, 0, 0);
                        b2.setLayoutParams(params);
                        b2.setBackgroundResource(R.drawable.selector_arrowhead);
                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TextView tvtemp=(TextView)mainll.findViewWithTag(category2+";"+item2);
                                if(!tvtemp.getText().equals("1")){
                                    tvtemp.setText(String.valueOf(Integer.parseInt(tvtemp.getText().toString())-1));
                                    orders.get(category2)[item2]--;
                                    TextView tvtemp2=(TextView)mainll.findViewWithTag(category2+"_"+item2);
                                    tvtemp2.setText("Rs." + String.valueOf( ItemDetails.getPrice(category2,item2)  + " x " + orders.get(category2)[item2] ));
                                    totalAmount-=Double.parseDouble(ItemDetails.getPrice(category2, item2));
                                    ((TextView)findViewById(R.id.totalAmount)).setText(String.valueOf(df.format(totalAmount)));
                                }

                            }
                        });
                        b2.setRotation(180);
                        ll3.addView(b2);

                        ll.addView(ll3);
                        mainll.addView(ll);
                    }
                }
            }
        }else{
            noItemsSelected();
        }

        int no=0;

        //PREVIOUS ORDERS
        for(int category=0;category<NO_C;++category) {
            Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + category + ";", null);
            c.moveToFirst();
            final int category2 = category;
            for (int item = 0; item < c.getInt(0); ++item) {
                final int item2 = item;
                if (orders_p.get(category)[item] != 0) {
                    if(no==0){
                        Button line=new Button(this);
                        line.setBackgroundColor(getResources().getColor(R.color.gray3));
                        LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
                        params1.setMargins((int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n30),(int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n20));
                        line.setLayoutParams(params1);
                        mainll.addView(line);

                        TextView textView=new TextView(this);
                        params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textView.setLayoutParams(params1);
                        textView.setText("Previous Orders");
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextSize(21);
                        textView.setTextColor(getResources().getColor(R.color.gray2));
                        textView.setTypeface(null,Typeface.BOLD);
                        mainll.addView(textView);

                        line=new Button(this);
                        line.setBackgroundColor(getResources().getColor(R.color.gray3));
                        params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
                        params1.setMargins((int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n20),(int)getResources().getDimension(R.dimen.n20),0);
                        line.setLayoutParams(params1);
                        mainll.addView(line);
                    }

                    LinearLayout linearLayout1=new LinearLayout(this);
                    LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0,(int)getResources().getDimension(R.dimen.n20),0,0);
                    linearLayout1.setLayoutParams(params1);
                    linearLayout1.setGravity(Gravity.CENTER);
                    linearLayout1.setPadding(n10,n10,n10,n10);
                    linearLayout1.setBackgroundColor(Color.WHITE);
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);

                    TextView textView=new TextView(this);
                    params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(params1);
                    textView.setText(ItemDetails.getTitle(category,item));
                    textView.setTypeface(null,Typeface.BOLD);
                    textView.setTextSize(18);
                    linearLayout1.addView(textView);

                    textView=new TextView(this);
                    params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(params1);
                    textView.setText("Rs." + df.format(Double.parseDouble(ItemDetails.getPrice(category,item))*orders_p.get(category)[item]) + " (" + ItemDetails.getPrice(category,item) + " x " + orders_p.get(category)[item] + ")");
                    textView.setTypeface(null,Typeface.BOLD);
                    textView.setTextSize(16);
                    linearLayout1.addView(textView);

                    LinearLayout linearLayout2=new LinearLayout(this);
                    params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout2.setLayoutParams(params1);
                    linearLayout2.setGravity(Gravity.CENTER);
                    linearLayout2.setPadding(n10, n10, n10, n10);
                    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);

                    Button like = new Button(this);
                    like.setTag(String.valueOf(category) + String.valueOf(item) + "like");
                    params1 = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n30), (int)getResources().getDimension(R.dimen.n30));
                    like.setBackgroundResource(R.drawable.like);
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setBackgroundResource(R.drawable.like_pressed);
                            LinearLayout temp = (LinearLayout)view.getParent();
                            Button b = (Button)temp.findViewWithTag(String.valueOf(category2) + String.valueOf(item2) + "dislike");
                            b.setClickable(false);
                            view.setClickable(false);
                            likes.get(category2)[item2] = 1;
                            Toast.makeText(MyOrder.this,"Liked",Toast.LENGTH_SHORT).show();
                            likeItem(category2,item2);
                        }
                    });
                    like.setLayoutParams(params1);
                    linearLayout2.addView(like);

                    Button dislike = new Button(this);
                    dislike.setTag(String.valueOf(category) + String.valueOf(item) + "dislike");
                    dislike.setBackgroundResource(R.drawable.like);
                    params1 = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n30), (int)getResources().getDimension(R.dimen.n30));
                    params1.setMargins((int) getResources().getDimension(R.dimen.n20), 0, 0, 0);
                    dislike.setLayoutParams(params1);
                    dislike.setRotation(180);
                    dislike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setBackgroundResource(R.drawable.like_pressed);
                            LinearLayout temp = (LinearLayout)view.getParent();
                            Button b = (Button)temp.findViewWithTag(String.valueOf(category2) + String.valueOf(item2) + "like");
                            b.setClickable(false);
                            view.setClickable(false);
                            likes.get(category2)[item2] = 2;
                            Toast.makeText(MyOrder.this,"Disliked",Toast.LENGTH_SHORT).show();
                            dislikeItem(category2,item2);
                        }
                    });
                    linearLayout2.addView(dislike);
                    linearLayout1.addView(linearLayout2);

                    if(likes.get(category2)[item2]==1){
                        like.setBackgroundResource(R.drawable.like_pressed);
                        like.setClickable(false);
                        Button b = (Button)linearLayout2.findViewWithTag(String.valueOf(category2) + String.valueOf(item2) + "dislike");
                        b.setClickable(false);
                    }else if(likes.get(category2)[item2]==2){
                        dislike.setBackgroundResource(R.drawable.like_pressed);
                        dislike.setClickable(false);
                        Button b = (Button)linearLayout2.findViewWithTag(String.valueOf(category2) + String.valueOf(item2) + "like");
                        b.setClickable(false);
                    }

                    mainll.addView(linearLayout1);
                    no++;
                }
            }
        }
    }

    public void likeItem(int c, int i){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodItems");
        query.whereEqualTo("category", c);
        query.whereEqualTo("position", i);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object!=null) {
                    object.increment("likes");
                    object.saveInBackground();
                }
            }
        });
    }

    public void dislikeItem(int c, int i){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodItems");
        query.whereEqualTo("category", c);
        query.whereEqualTo("position", i);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object!=null) {
                    object.increment("dislikes");
                    object.saveInBackground();
                }
            }
        });
    }

    public void noItemsSelected(){

        LinearLayout mainll = (LinearLayout) findViewById(R.id.ordermainll);

        LinearLayout linearLayout1=new LinearLayout(this);
        LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.gravity=Gravity.CENTER_HORIZONTAL;
        params1.setMargins(0,(int)getResources().getDimension(R.dimen.n30),0,0);
        linearLayout1.setLayoutParams(params1);
        linearLayout1.setGravity(Gravity.CENTER);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);

        TextView textView=new TextView(this);
        textView.setText("No items have been selected yet.");
        params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params1);
        textView.setTextSize(18);
        linearLayout1.addView(textView);

        textView=new TextView(this);
        textView.setText("Click on the box to order an item!");
        params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params1);
        textView.setTextSize(16);
        linearLayout1.addView(textView);

        LinearLayout linearLayout2=new LinearLayout(this);
        LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity=Gravity.CENTER;
        params2.setMargins(0,(int)getResources().getDimension(R.dimen.n10),0,0);
        linearLayout2.setLayoutParams(params2);
        linearLayout2.setGravity(Gravity.CENTER);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imageView=new ImageView(this);
        params1=new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n25),(int)getResources().getDimension(R.dimen.n25));
        imageView.setLayoutParams(params1);
        imageView.setImageResource(R.drawable.checkednot);
        linearLayout2.addView(imageView);

        imageView=new ImageView(this);
        params1=new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n35),(int)getResources().getDimension(R.dimen.n35));
        params1.setMargins((int)getResources().getDimension(R.dimen.n10),0,(int)getResources().getDimension(R.dimen.n10),0);
        imageView.setLayoutParams(params1);
        imageView.setImageResource(R.drawable.arrow);
        linearLayout2.addView(imageView);

        imageView=new ImageView(this);
        params1=new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.n25),(int)getResources().getDimension(R.dimen.n25));
        imageView.setLayoutParams(params1);
        imageView.setImageResource(R.drawable.checked);
        linearLayout2.addView(imageView);
        linearLayout1.addView(linearLayout2);
        mainll.addView(linearLayout1);
    }


    public void setDimensions(){
        n120=(int)getResources().getDimension(R.dimen.n120);
        n10=(int)getResources().getDimension(R.dimen.n10);
        n5=(int)getResources().getDimension(R.dimen.n5);
        n85=(int)getResources().getDimension(R.dimen.n85);
        n70=(int)getResources().getDimension(R.dimen.n70);
        n30=(int)getResources().getDimension(R.dimen.n30);
        n25=(int)getResources().getDimension(R.dimen.n25);
        n145=(int)getResources().getDimension(R.dimen.n145);
        n20=(int)getResources().getDimension(R.dimen.n20);
    }

    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null ) return false;
        else return true;
    }

}