package com.iclub.dineart;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsList extends ActionBarActivity {

    int CATEGORY,n120,n10,n5,n85,n70,n30,ITEMCOUNT;
    ArrayList<Integer> simages;
    ArrayList<String> names;
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        db=openOrCreateDatabase("dineart_table.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Intent intent=getIntent();
        CATEGORY=intent.getIntExtra("id",1);
        Cursor c = db.rawQuery("SELECT COUNT(position) FROM items WHERE category=" + CATEGORY + ";", null);
        c.moveToFirst();
        ITEMCOUNT = c.getInt(0);

        simages=new ArrayList<>(5);
        names=new ArrayList<>(5);

        //import dimensions from dimens.xml to local variables
        setDimensions();

        //set the image resource ids in images[]
        //setImagesAndNames();

        //adds all the items under the current category to the screen
        addItems();

        //On click function and on touch bg change for MyOrder
        final LinearLayout myorderll=(LinearLayout)findViewById(R.id.myorderlllist);
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
                        startActivity(new Intent(ItemsList.this,MyOrder.class));
                        break;
                }
                return true;
            }
        });
    }

    public void addItems(){

        LinearLayout mainll=(LinearLayout)findViewById(R.id.mainlllist);
        LinearLayout ll,ll2;
        LinearLayout.LayoutParams params;
        ImageView iv;
        LinearLayout fl;
        Button b;
        TextView tv1;

        for(int i=0;i<ITEMCOUNT;++i) {
            final int i2=i;
            ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundResource(R.drawable.selector_white);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ItemsList.this,Item.class);
                    intent.putExtra("category",(CATEGORY));
                    intent.putExtra("position",(i2));
                    startActivity(intent);
                }
            });
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i==0)params.setMargins(0,n85,0,0);
            else if(i==4)params.setMargins(0, 0, 0, n70);
            ll.setLayoutParams(params);

            iv = new ImageView(this);
            params = new LinearLayout.LayoutParams(0, n120);
            params.weight=1;
            iv.setLayoutParams(params);
            Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/dineart/items/" + CATEGORY + "_" + i + ".jpg");
            iv.setImageBitmap(bitmap);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ll.addView(iv);

            fl=new LinearLayout(this);
            fl.setGravity(Gravity.TOP|Gravity.LEFT);
            fl.setOrientation(LinearLayout.VERTICAL);
            params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight=1.8f;
            fl.setLayoutParams(params);
            fl.setPadding(n10,n10,n10,n10);

            tv1=new TextView(this);
            params=new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            tv1.setLayoutParams(params);
            tv1.setTextSize(18);
            tv1.setText(ItemDetails.getTitle(CATEGORY,i));
            tv1.setTypeface(null, Typeface.BOLD);
            fl.addView(tv1);

            tv1=new TextView(this);
            params=new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,n5);
            params.gravity= Gravity.BOTTOM;
            tv1.setLayoutParams(params);
            tv1.setTextSize(16);
            tv1.setText("Rs." + ItemDetails.getPrice(CATEGORY,i));
            tv1.setTypeface(null, Typeface.BOLD);
            tv1.setGravity(Gravity.BOTTOM);
            fl.addView(tv1);

            tv1=new TextView(this);
            params=new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,n5,0,n5);
            params.gravity= Gravity.BOTTOM;
            tv1.setLayoutParams(params);
            tv1.setTextSize(15);
            tv1.setText("Likes: "+ItemDetails.getLikes(CATEGORY,i)+" | Dislikes: "+ItemDetails.getDislikes(CATEGORY,i));
            tv1.setTypeface(null, Typeface.BOLD);
            tv1.setGravity(Gravity.BOTTOM);
            fl.addView(tv1);
            ll.addView(fl);

            ll2=new LinearLayout(this);
            params=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight=0.4f;
            ll2.setLayoutParams(params);
            ll2.setGravity(Gravity.CENTER_HORIZONTAL);

            b=new Button(this);
            params=new LinearLayout.LayoutParams(n30,n30);
            params.setMargins(0,n10,n5,0);
            b.setLayoutParams(params);

            if(MyOrder.orders.get(CATEGORY)[i]==0){
                b.setBackgroundResource(R.drawable.checkednot);
                b.setTag("unchecked;"+CATEGORY+";"+i);
            }
            else{
                b.setBackgroundResource(R.drawable.checked);
                b.setTag("checked;"+CATEGORY+";"+i);
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check(view);
                }
            });
            ll2.addView(b);
            ll.addView(ll2);

            mainll.addView(ll);
        }

    }

    public void check(View view){
        Button button=(Button)view;
        if(button.getTag().toString().startsWith("unchecked")){
            button.setBackgroundResource(R.drawable.checked);
            button.setTag(button.getTag().toString().replace("un",""));
            String[] parts=button.getTag().toString().split(";");
            int category=Integer.parseInt(parts[1]);
            int item=Integer.parseInt(parts[2]);
            MyOrder.orders.get(category)[item]=1;
        }else{
            button.setBackgroundResource(R.drawable.checkednot);
            button.setTag(button.getTag().toString().replace("ch","unch"));
            String[] parts=button.getTag().toString().split(";");
            int category=Integer.parseInt(parts[1]);
            int item=Integer.parseInt(parts[2]);
            MyOrder.orders.get(category)[item]=0;
        }
    }

    public void setDimensions(){
        n120=(int)getResources().getDimension(R.dimen.n120);
        n10=(int)getResources().getDimension(R.dimen.n10);
        n5=(int)getResources().getDimension(R.dimen.n5);
        n85=(int)getResources().getDimension(R.dimen.n85);
        n70=(int)getResources().getDimension(R.dimen.n70);
        n30=(int)getResources().getDimension(R.dimen.n30);
    }


}