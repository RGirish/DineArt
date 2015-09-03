package com.iclub.dineartkitchen;

import android.database.Cursor;

import java.text.DecimalFormat;

public class ItemDetails {

    public static String getTitle(int category,int position) {
        Cursor c = MainActivity.db.rawQuery("SELECT title FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        String title = c.getString(0);
        return title;
    }
}