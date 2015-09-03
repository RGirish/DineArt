package com.iclub.dineart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Admin extends ActionBarActivity {

    public static SQLiteDatabase db;
    ProgressDialog dialog1,dialog2,dialog3;
    int NO_C,NO_I;
    ArrayList<Integer> notAvailableList_C;
    int COUNT_C=0,CURR_COUNT_C=0;
    ArrayList<String> notAvailableList_I;
    int COUNT_I=0,CURR_COUNT_I=0;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        try{ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");
        db=openOrCreateDatabase("dineart_table.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
        createTables();
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


    public void onClickSetTableNumber(View view){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_table_number);
        final EditText tableNumberEt=(EditText)dialog.findViewById(R.id.tableNumber);
        Button setBtn=(Button)dialog.findViewById(R.id.setBtn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.execSQL( "DELETE FROM mytable;" );
                db.execSQL("INSERT INTO mytable VALUES('" + tableNumberEt.getText().toString() + "');");
                dialog.dismiss();
                Toast.makeText(Admin.this,"Table Number is set!",Toast.LENGTH_LONG).show();
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }


    public void onClickSyncEverything(View view){

        File folder = new File(Environment.getExternalStorageDirectory() + "/dineart");
        deleteRecursive(folder);
        onClickSyncNewItems(null);
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void onClickSyncNewItems(View view){

        //First check if the thumbnails are already downloaded and download only those unavailable
        //create necessary folders in external sd card
        File folder = new File(Environment.getExternalStorageDirectory() + "/dineart");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folder2 = new File(Environment.getExternalStorageDirectory() + "/dineart/categories");
        if (!folder2.exists()) {
            folder2.mkdir();
        }
        File folder3 = new File(Environment.getExternalStorageDirectory() + "/dineart/items");
        if (!folder3.exists()) {
            folder3.mkdir();
        }


            if (checkConnection()){
                dialog1 = ProgressDialog.show(this, null, "Just a moment...", true);
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
                query.addAscendingOrder("code");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e==null){
                            db.execSQL("DELETE FROM categories;");
                            for(ParseObject ob : objects){
                                db.execSQL("INSERT INTO categories VALUES("+ob.getNumber("code")+");");
                            }
                            Cursor c=db.rawQuery("SELECT COUNT(code) FROM categories;",null);
                            c.moveToFirst();
                            NO_C=c.getInt(0);


                            final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FoodItems");
                            query2.orderByAscending("category");
                            query2.addAscendingOrder("position");
                            query2.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null){
                                        db.execSQL("DELETE FROM items;");
                                        for(ParseObject ob : objects){
                                            db.execSQL("INSERT INTO items VALUES('"+ob.getString("title")+"',"+ob.getNumber("category")+","+ob.getNumber("position")+",'"+ob.getString("description")+"','"+ob.getString("ingredients")+"',"+ob.getNumber("price")+","+ob.getNumber("likes")+","+ob.getNumber("dislikes")+");");
                                        }
                                        Cursor c=db.rawQuery("SELECT COUNT(category) FROM items;",null);
                                        c.moveToFirst();
                                        NO_I=c.getInt(0);
                                        dialog1.dismiss();
                                        downloadEverything();
                                    }else{
                                        Log.e("PARSE", "Error: " + e.getMessage());
                                    }
                                }
                            });

                        }else{
                            Log.e("PARSE", "Error: " + e.getMessage());
                        }
                    }
                });

            }else{
                Toast.makeText(this, "Check your Internet Connection!", Toast.LENGTH_LONG).show();
            }
    }


    public void downloadEverything(){

        notAvailableList_C = new ArrayList<>(NO_C);
        notAvailableList_C.clear();
        notAvailableList_I = new ArrayList<>(NO_I);
        notAvailableList_I.clear();
        Cursor c=db.rawQuery("SELECT COUNT(code) FROM categories;",null);
        c.moveToFirst();
        for(int category=0;category<c.getInt(0);++category){
            String FILENAME = Environment.getExternalStorageDirectory().toString() + "/dineart/categories/" + category + ".jpg";
            File file = new File(FILENAME);
            if(!file.exists()){
                notAvailableList_C.add(category);
            }
        }

        if(notAvailableList_C.size() > 0) {
            if(checkConnection()){
                dialog2 = ProgressDialog.show(this, null, "Just a moment...", true);
                CURR_COUNT_C = 0;
                COUNT_C = notAvailableList_C.size();
                for (final int k : notAvailableList_C) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
                    query.whereEqualTo("code", k);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                ParseFile myFile = objects.get(0).getParseFile("categoryImage");
                                myFile.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            writeFile_C(data, k + ".jpg");
                                            CURR_COUNT_C++;
                                            if (CURR_COUNT_C == COUNT_C) dialog2.dismiss();
                                        } else {
                                            Log.e("Something went wrong", "Something went wrong");
                                        }
                                    }
                                });
                            } else {
                                Log.e("PARSE", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }else{
                Toast.makeText(this, "Internet Connection unavailable!", Toast.LENGTH_LONG).show();
            }
        }



        //Items
        Cursor c2=db.rawQuery("SELECT DISTINCT(category) FROM items ORDER BY category;",null);
        c2.moveToFirst();
        int category;
        while(true){
            category=c2.getInt(0);
            Cursor c3=db.rawQuery("SELECT COUNT(position) FROM items WHERE category="+category+";",null);
            c3.moveToFirst();
            int no_in_this_cat=c3.getInt(0);
            for(int pos=0;pos<no_in_this_cat;++pos) {
                String FILENAME = Environment.getExternalStorageDirectory().toString() + "/dineart/items/" + category + "_" + pos +".jpg";
                File file = new File(FILENAME);
                if (!file.exists()) {
                    notAvailableList_I.add(category + "_" + pos);
                }
            }

            if(c2.isLast())break;
            c2.moveToNext();
        }


        if(notAvailableList_I.size() > 0) {
            if(checkConnection()){
                dialog3 = ProgressDialog.show(this, null, "Just a moment...", true);
                CURR_COUNT_I = 0;
                COUNT_I = notAvailableList_I.size();
                for (final String s : notAvailableList_I) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodItems");
                    String[] parts=s.split("_");
                    query.whereEqualTo("category", Integer.parseInt(parts[0]));
                    query.whereEqualTo("position", Integer.parseInt(parts[1]));
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                ParseFile myFile = objects.get(0).getParseFile("itemImage");
                                myFile.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            writeFile_I(data, s + ".jpg");
                                            CURR_COUNT_I++;
                                            if (CURR_COUNT_I == COUNT_I) dialog3.dismiss();
                                        } else {
                                            Log.e("Something went wrong", "Something went wrong");
                                        }
                                    }
                                });
                            } else {
                                Log.e("PARSE", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }else{
                Toast.makeText(this, "Internet Connection unavailable!", Toast.LENGTH_LONG).show();
            }
        }


    }

    public void writeFile_C(byte[] data, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/dineart/categories/"+fileName);
            out.write(data);
            out.close();
        }catch(Exception e){
            Log.e("WriteFile_C",e.getMessage());
        }
    }

    public void writeFile_I(byte[] data, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/dineart/items/"+fileName);
            out.write(data);
            out.close();
        }catch(Exception e){
            Log.e("WriteFile_I",e.getMessage());
        }
    }


    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null ) return false;
        else return true;
    }

}