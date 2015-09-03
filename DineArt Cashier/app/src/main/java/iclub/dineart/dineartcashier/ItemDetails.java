package iclub.dineart.dineartcashier;

import android.database.Cursor;

import java.text.DecimalFormat;

public class ItemDetails {

    public static String getPrice(int category,int position) {
        DecimalFormat df = new DecimalFormat("####0.00");
        Cursor c = BillActivity.db.rawQuery("SELECT price FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        double price = c.getDouble(0);
        return String.valueOf(df.format(price));
    }

    public static String getTitle(int category,int position) {
        Cursor c = BillActivity.db.rawQuery("SELECT title FROM items WHERE category="+category+" AND position="+position+";", null);
        c.moveToFirst();
        String title = c.getString(0);
        return title;
    }
}