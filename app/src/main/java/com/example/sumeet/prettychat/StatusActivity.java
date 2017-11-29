package com.example.sumeet.prettychat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sumeet.prettychat.R.id.update_status;

public class StatusActivity extends AppCompatActivity {
    Toolbar mToolbar;
    EditText statusText;
    Button UpdateStatus_btn;
    DatabaseReference mStatusReference;
    FirebaseUser mCurrent;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mProgress = new ProgressDialog(this);
        String intentStatus=getIntent().getStringExtra("getStatus");


        //firebase Initialization
        mCurrent = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrent.getUid();
        mStatusReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        statusText = (EditText) findViewById(R.id.account_status);
        statusText.setText(intentStatus);
        UpdateStatus_btn = (Button) findViewById(R.id.update_status);
        UpdateStatus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle("Updating Status");
                mProgress.setMessage("please wait...");
                mProgress.setCanceledOnTouchOutside(false);
                String status = statusText.getText().toString();
                mProgress.show();
                mStatusReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            Toast.makeText(StatusActivity.this,"Status updated",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StatusActivity.this, "Unable to Update status,try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
