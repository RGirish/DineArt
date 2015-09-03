package com.iclub.dineart;

import android.database.Cursor;

import java.text.DecimalFormat;

public class ItemDetails{

    public static String getPrice(int category,int position) {
        DecimalFormat df = new DecimalFormat("####0.00");
        Cursor c = Categories.db.rawQuery("SELECT price FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        double price = c.getDouble(0);
        return String.valueOf(df.format(price));
    }

    public static String getLikes(int category,int position) {
        Cursor c = Categories.db.rawQuery("SELECT likes FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        int likes = c.getInt(0);
        return String.valueOf(likes);
    }

    public static String getDislikes(int category,int position) {
        Cursor c = Categories.db.rawQuery("SELECT dislikes FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        int dislikes = c.getInt(0);
        return String.valueOf(dislikes);
    }

    public static String getTitle(int category,int position) {
        Cursor c = Categories.db.rawQuery("SELECT title FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        String title = c.getString(0);
        return title;
    }

    public static String getDesc(int category,int position){
        Cursor c = Categories.db.rawQuery("SELECT desc FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        String desc = c.getString(0);
        return desc;
    }

    public static String getIngre(int category,int position){
        Cursor c = Categories.db.rawQuery("SELECT ingre FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        String ingre = c.getString(0);
        return ingre;
    }
}