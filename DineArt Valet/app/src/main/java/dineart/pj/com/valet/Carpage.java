package dineart.pj.com.valet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;


public class Carpage extends ActionBarActivity {

    int count = 0;
    LinearLayout ll;
    Context cc;
    Firebase ref;
    String customerPhoneNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpage);
        if(checkConnection()){
            Firebase.setAndroidContext(this);
            ref=new Firebase("https://dineart.firebaseio.com");
        }
        ll = (LinearLayout)findViewById(R.id.contain);
        cc = this;
        Intent in = getIntent();
        customerPhoneNumber=in.getStringExtra("pno");
        setNo();
    }

    public void clearUp(){
    ll.removeAllViews();
    }

    public void setCar(){
        clearUp();
        EditText make = new EditText(cc);
        make.setHint("Hyundai Mazda");
        make.setTag("make");
        EditText color = new EditText(cc);
        color.setHint("Black");
        color.setTag("color");
        EditText number = new EditText(cc);
        number.setHint("TN 06 RA 4567");
        number.setTag("number");
        EditText slot = new EditText(cc);
        slot.setHint("A22");
        slot.setTag("slot");
        final Button subcar = new Button(this);
        View.OnClickListener subcarr = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcar();
            }
        };
        subcar.setOnClickListener(subcarr);
        subcar.setText("Submit");
        ll.addView(make);
        ll.addView(color);
        ll.addView(number);
        ll.addView(slot);
        ll.addView(subcar);
    }

    public void setDriver(){
        clearUp();
        EditText name = new EditText(cc);
        name.setHint("Mike Ross");
        name.setTag("name");
        EditText phone = new EditText(cc);
        phone.setHint("8939083638");
        phone.setTag("phone");
        final Button subdri = new Button(this);
        View.OnClickListener subdriv = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subdri();
            }
        };
        subdri.setOnClickListener(subdriv);
        subdri.setText("Submit");
        ll.addView(name);
        ll.addView(phone);
        ll.addView(subdri);
    }

    public void setNo(){
        clearUp();
        TextView no = new TextView(cc);
        no.setTextSize(30);
        no.setText("No work to do!");
        final Button subno = new Button(this);
        View.OnClickListener subnoo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subNo();
            }
        };
        subno.setOnClickListener(subnoo);
        subno.setText("Submit");
        ll.addView(no);
        ll.addView(subno);
    }

    public void subcar(){
        String make = ((EditText)ll.findViewWithTag("make")).getText().toString();
        String clr = ((EditText)ll.findViewWithTag("color")).getText().toString();
        String num = ((EditText)ll.findViewWithTag("number")).getText().toString();
        String slot = ((EditText)ll.findViewWithTag("slot")).getText().toString();
        if(checkConnection())ref.child("valetDetails").push().setValue("mode_car;customerphone_"+customerPhoneNumber+";make_"+make+";color_"+clr+";number_"+num+";slot_"+slot);
        finish();
    }

    public void subdri(){
        String name = ((EditText)ll.findViewWithTag("name")).getText().toString();
        String number = ((EditText)ll.findViewWithTag("phone")).getText().toString();
        if(checkConnection())ref.child("valetDetails").push().setValue("mode_driver;customerphone_"+customerPhoneNumber+";name_"+name+";driverphone_"+number);
        finish();
    }

    public void subNo() {
        if(checkConnection())ref.child("valetDetails").push().setValue("mode_nothing;");
        finish();
    }

    public void change(View view){
        ImageView ib = (ImageView)view;
        switch(count){
            case 0 :
                count=1;
                ib.setImageDrawable(getResources().getDrawable(R.drawable.car));
                setCar();
                break;
            case 1 :
                count = 2;
                ib.setImageDrawable(getResources().getDrawable(R.drawable.driver));
                setDriver();
                break;
            case 2 :
                count = 0;
                ib.setImageDrawable(getResources().getDrawable(R.drawable.no));
                setNo();
                break;
        }
    }

    public boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null ) return false;
        else return true;
    }

}