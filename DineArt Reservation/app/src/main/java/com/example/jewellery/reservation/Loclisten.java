
package com.example.jewellery.reservation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.FloatMath;
import android.widget.Toast;
import com.firebase.client.Firebase;

public class Loclisten extends Service {

    String phoneNumber;
    Firebase ref;
    LocationManager locationManager;
    LocationListener locationListener;

    public Loclisten() {
    }

    double latitude,longitude;

    public void setLocation(Location location){
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        double dist = gps2m(12.9272275f,80.1158634f,(float)latitude, (float)longitude);
        Toast.makeText(this,"inniku test "+dist,Toast.LENGTH_SHORT).show();

        if(dist<3000){
            ref.child("locationUpdates").push().setValue(phoneNumber);
            locationManager.removeUpdates(locationListener);
            stopService(new Intent(getApplicationContext(),Loclisten.class));
        }

    }

    //12.9272275f,80.1158634f tambaram
    //12.946063 80.245233 mnm
    //12.8699801 80.215783 sjce


    private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180/3.14169);
        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;
        float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
        float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
        float t3 = FloatMath.sin(a1)* FloatMath.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        return 6366000*tt;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        phoneNumber = intent.getStringExtra("phone");

        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://dineart.firebaseio.com/");

        try {
            locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    setLocation(location);
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }catch (Exception e){
            Toast.makeText(this, "Error in location search!", Toast.LENGTH_LONG).show();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}