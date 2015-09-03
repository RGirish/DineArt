
package iclub.dineart.dineartcashier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    ProgressDialog dialog;
    static Firebase ref;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=openOrCreateDatabase("dineart_cashier.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
        createTables();
        db.execSQL("DELETE FROM allorders;");

        Firebase.setAndroidContext(this);
        ref=new Firebase("https://dineart.firebaseio.com");

        try{
            ParseCrashReporting.enable(this);}catch (Exception e){}
        Parse.initialize(this, "iXFKaAxcmX7naTr3s5eCQYww1BbnMUp4Bc77jLoE", "YEm5Bkktyfp9zGikxPfoYoVeUJkntj2gprDyA8aT");

        ref.child("placedOrders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String[] parts=dataSnapshot.getValue().toString().split(";");
                String theorder="";
                for(int i=2;i<parts.length;++i){
                    theorder+=(parts[i]+"!");
                }
                Log.e("INTO DB",theorder);
                db.execSQL("INSERT INTO allorders(tablenumber,phone,theorder) VALUES('"+parts[0]+"','"+parts[1]+"','"+theorder+"');");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        ref.child("valetDetails").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String temp = dataSnapshot.getValue().toString();
                String[] parts = temp.split(";");
                if(parts.length!=1){
                    String customerPhone = parts[1].split("_")[1];
                    db.execSQL("INSERT INTO valet VALUES('"+temp+"','"+customerPhone+"');");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

    }

    public void createTables(){
        try{
            db.execSQL("CREATE TABLE allorders(phone TEXT,tablenumber TEXT, theorder TEXT);");
        }catch(Exception e){}
        try{
            db.execSQL("CREATE TABLE items(title TEXT, category NUMBER,position NUMBER,price TEXT);");
        }catch(Exception e){}
        try{
            db.execSQL("CREATE TABLE valet(details TEXT,phone TEXT);");
        }catch(Exception e){}
    }

    public void onTap(View view){
        String phoneNumber="7299687990";
        Intent intent=new Intent(this,BillActivity.class);
        intent.putExtra("phone",phoneNumber);
        startActivity(intent);
    }

    public void downloadEverything(View view) {
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
}