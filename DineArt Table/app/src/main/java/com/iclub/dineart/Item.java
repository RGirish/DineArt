package com.iclub.dineart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Item extends ActionBarActivity {

    ImageView theimage;
    TextView thetitle,theingredients,thedescription,theprice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        Intent intent = getIntent();
        int category = intent.getIntExtra("category", 1);
        int position = intent.getIntExtra("position", 1);

        thedescription = (TextView) findViewById(R.id.thedescription);
        thetitle = (TextView) findViewById(R.id.thetitle);
        theprice = (TextView) findViewById(R.id.theprice);
        theingredients = (TextView) findViewById(R.id.theingredients);
        theimage = (ImageView) findViewById(R.id.thephoto);

        thedescription.setText(ItemDetails.getDesc(category, position));
        thetitle.setText(ItemDetails.getTitle(category, position));
        theprice.setText("Rs." + ItemDetails.getPrice(category, position));
        theingredients.setText(ItemDetails.getIngre(category, position));
        Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/dineart/items/" + category + "_" + position + ".jpg");
        theimage.setImageBitmap(bitmap);

        //On click function and on touch bg change for MyOrder
        final LinearLayout myorderll = (LinearLayout) findViewById(R.id.myorderllsingleitem);
        myorderll.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myorderll.setBackgroundResource(R.drawable.orange_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        myorderll.setBackgroundResource(R.drawable.orange_normal);
                        startActivity(new Intent(Item.this, MyOrder.class));
                        break;
                }
                return true;
            }
        });

    }

}