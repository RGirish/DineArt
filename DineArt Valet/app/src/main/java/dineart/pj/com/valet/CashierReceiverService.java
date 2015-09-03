package dineart.pj.com.valet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class CashierReceiverService extends Service{

    static Firebase ref;
    private static int NOTIFICATION = 123;
    private NotificationManager mNM;
    ChildEventListener childEventListener;

    public class ServiceBinder extends Binder {
        CashierReceiverService getService() {
            return CashierReceiverService.this;
        }
    }

    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        ref = new Firebase("https://dineart.firebaseio.com");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(CashierReceiverService.this,"Notification",Toast.LENGTH_SHORT).show();
                showNotification();
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
        ref.child("valetReturn").addChildEventListener(childEventListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void showNotification() {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("TN 06 B 1468");
        notification.setContentText("Black Honda City @ S12");
        notification.setSmallIcon(R.drawable.ic_notification);
        notification.setSound(soundUri);
        notification.setContentIntent(pIntent);
        notification.setTicker("Black Honda City @ S12");
        notification.setOngoing(true);
        notification.setAutoCancel(true);

        Intent i = new Intent(this, NotificationClearIntentService.class);
        i.putExtra("id", NOTIFICATION);
        PendingIntent pIntentService = PendingIntent.getService(this, NOTIFICATION, i, PendingIntent.FLAG_CANCEL_CURRENT);

        notification.addAction(R.drawable.check, "Done", pIntentService);
        mNM.notify(NOTIFICATION, notification.build());
        NOTIFICATION++;
    }

    @Override
    public void onDestroy(){
        ref.child("valetReturn").removeEventListener(childEventListener);
        super.onDestroy();
    }

}