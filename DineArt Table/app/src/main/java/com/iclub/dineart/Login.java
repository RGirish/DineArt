package com.iclub.dineart;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
    }

    public void onclickLogin(View view){
        EditText et=(EditText)findViewById(R.id.masterpassword);
        if(et.getText().toString().equals("5555")){
            startActivity(new Intent(this,Admin.class));
        }else{
            et.setText("");
            Toast.makeText(this,"Wrong Password!",Toast.LENGTH_LONG).show();
        }
        finish();
    }

}