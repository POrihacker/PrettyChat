package com.example.sumeet.prettychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    Button startLog,startReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startLog= (Button) findViewById(R.id.startLogin_btn);
        startReg= (Button) findViewById(R.id.startReg_btn);
        startReg.setOnClickListener(this);
        startLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==startLog){
            //start Intent of LoginActivity
            Intent startLogin=new Intent(StartActivity.this,LoginActivity.class);
            startActivity(startLogin);
            finish();
        }
        else{
            //start Intent of RegisterActivity
            Intent startReg=new Intent(StartActivity.this,RegisterActivity.class);
            startActivity(startReg);
            finish();

        }

    }
}
