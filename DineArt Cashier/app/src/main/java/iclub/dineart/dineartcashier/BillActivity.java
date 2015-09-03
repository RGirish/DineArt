
package iclub.dineart.dineartcashier;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import java.text.DecimalFormat;


public class BillActivity extends ActionBarActivity {

    int n10,n40,n5,n80,n90;
    static SQLiteDatabase db;
    String phoneNumber;
    double totalAmount;
    DecimalFormat df;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        final LinearLayout myorderll=(LinearLayout)findViewById(R.id.finishPayment);
        myorderll.setOnTouchListener( new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        myorderll.setBackgroundResource(R.drawable.orange_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        myorderll.setBackgroundResource(R.drawable.orange_normal);
                        final Dialog dialog=new Dialog(BillActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.payment_successful_confirmation);
                        final LinearLayout llPage1=(LinearLayout)dialog.findViewById(R.id.llPage1);
                        final LinearLayout llPage2=(LinearLayout)dialog.findViewById(R.id.llPage2);
                        final TextView textView=(TextView)dialog.findViewById(R.id.changeToBeGiven);
                        final EditText et=(EditText)dialog.findViewById(R.id.amountGiven);
                        Button goBtn=(Button)dialog.findViewById(R.id.goBtn);
                        goBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(et.getText().toString().equals("")){
                                    llPage1.setVisibility(View.GONE);
                                    llPage2.setVisibility(View.VISIBLE);
                                    textView.setVisibility(View.GONE);
                                }else{
                                    double amtgvn=Double.parseDouble(et.getText().toString());
                                    llPage1.setVisibility(View.GONE);
                                    llPage2.setVisibility(View.VISIBLE);
                                    textView.setText("Change - Rs." + df.format(amtgvn-totalAmount));
                                }
                            }
                        });
                        Button paymentSuccessfulBtn=(Button)dialog.findViewById(R.id.paymentSuccessful);
                        paymentSuccessfulBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Cursor cursor = db.rawQuery("SELECT details FROM valet WHERE phone='"+phoneNumber+"';",null);
                                cursor.moveToFirst();
                                String details = cursor.getString(0);
                                MainActivity.ref.child("valetReturn").push().setValue(details);
                                Toast.makeText(BillActivity.this,"Done!",Toast.LENGTH_SHORT).show();
                                finish();
                                Cursor c = db.rawQuery("SELECT tablenumber FROM allorders WHERE phone='"+phoneNumber+"';",null);
                                c.moveToFirst();
                                String tno = c.getString(0);
                                ref.child("cleanTable").push().setValue(String.valueOf(tno));

                                db.execSQL("DELETE FROM allorders WHERE phone='"+phoneNumber+"';");
                                dialog.dismiss();
                            }
                        });
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        dialog.show();
                        break;
                }
                return true;
            }
        });

        df = new DecimalFormat("####0.00");

        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phone");
        Log.e("PHONE",phoneNumber);

        db=openOrCreateDatabase("dineart_cashier.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);

        setDimensions();
        addAllItems();
    }


    public void addAllItems(){

        LinearLayout billLinearLayout=(LinearLayout)findViewById(R.id.billLinearLayout);
        int SNO=1;

        Cursor cursor=db.rawQuery("SELECT theorder FROM allorders WHERE phone='"+phoneNumber+"';",null);
        try{
            cursor.moveToFirst();

            while(true){

                String theOrder=cursor.getString(0);
                String[] parts=theOrder.split("!");
                for(int i=0;i<parts.length;++i){

                    String[] anItem=parts[i].split("_");
                    LinearLayout linearLayout1=new LinearLayout(this);
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0,n10,0,0);

                    TextView textView1=new TextView(this);
                    textView1.setText(SNO+".");
                    SNO++;
                    textView1.setGravity(Gravity.CENTER);
                    textView1.setPadding(n10,n10,0,0);
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setTextSize(17);
                    textView1.setTextColor(getResources().getColor(R.color.gray1));
                    params1=new LinearLayout.LayoutParams(n40,LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView1.setLayoutParams(params1);
                    linearLayout1.addView(textView1);

                    LinearLayout linearLayout2=new LinearLayout(this);
                    linearLayout2.setOrientation(LinearLayout.VERTICAL);
                    params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.weight=0.75f;
                    linearLayout2.setLayoutParams(params1);
                    linearLayout2.setPadding(0,n10,0,n10);

                    textView1=new TextView(this);
                    textView1.setText(ItemDetails.getTitle(Integer.parseInt(anItem[0]),Integer.parseInt(anItem[1])));
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setTextSize(17);
                    textView1.setTextColor(getResources().getColor(R.color.gray1));
                    params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView1.setLayoutParams(params1);
                    linearLayout2.addView(textView1);

                    textView1=new TextView(this);
                    String price=ItemDetails.getPrice(Integer.parseInt(anItem[0]), Integer.parseInt(anItem[1]));
                    textView1.setText("Rs." + price + " x " + anItem[2]);
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setTextSize(15);
                    textView1.setTextColor(getResources().getColor(R.color.gray2));
                    params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(0,n5,0,0);
                    textView1.setLayoutParams(params1);
                    linearLayout2.addView(textView1);
                    linearLayout1.addView(linearLayout2);

                    textView1=new TextView(this);
                    textView1.setText("Rs." + df.format(Double.parseDouble(price) * Integer.parseInt(anItem[2])));
                    totalAmount += (Double.parseDouble(price) * Integer.parseInt(anItem[2]));
                    textView1.setGravity(Gravity.RIGHT);
                    textView1.setPadding(0,n10,n10,0);
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setTextSize(15);
                    textView1.setTextColor(getResources().getColor(R.color.gray1));
                    params1=new LinearLayout.LayoutParams(n90,LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView1.setLayoutParams(params1);
                    linearLayout1.addView(textView1);
                    billLinearLayout.addView(linearLayout1);



                }
                if(cursor.isLast())break;
                cursor.moveToNext();
            }

        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }


        ((TextView)findViewById(R.id.totalAmount)).setText(df.format(totalAmount));

    }

    public void setDimensions(){
        n10=(int)getResources().getDimension(R.dimen.n10);
        n40=(int)getResources().getDimension(R.dimen.n40);
        n5=(int)getResources().getDimension(R.dimen.n5);
        n80=(int)getResources().getDimension(R.dimen.n80);
        n90=(int)getResources().getDimension(R.dimen.n90);
    }
}