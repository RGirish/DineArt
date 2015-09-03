package com.iclub.dineartsms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class FirebaseListenerService extends Service {

    Firebase ref;
    ChildEventListener childEventListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://dineart.firebaseio.com/");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string = dataSnapshot.getValue().toString();
                String[] parts = string.split(";");
                String phoneNumber = parts[0];
                String messageText = parts[1];
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
                ref.child("smsMessages").child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
        ref.child("smsMessages").addChildEventListener(childEventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        ref.removeEventListener(childEventListener);
        super.onDestroy();
    }

}