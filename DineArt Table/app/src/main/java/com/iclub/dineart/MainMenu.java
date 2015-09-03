package com.iclub.dineart;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
    }

    public void onClickAdmin(View view){
        startActivity(new Intent(this, Login.class));
    }

    public void onClickMenu(View view){
        //Intent intent=new Intent(this, TagViewer.class);
        //startActivity(intent);
        Intent in = new Intent(this,Categories.class);
        in.putExtra("phone","4805706305");
        startActivity(in);
    }

}