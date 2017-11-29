package com.example.sumeet.prettychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Toolbar mToolbar;
    EditText userName, userEmail, userPass;
    Button reg_submit;
    FirebaseAuth regAuth;
    ProgressDialog mProgress;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        regAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        getSupportActionBar().setTitle("Create an Account");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Firebase Stuff


        //initializing Views
        userName = (EditText) findViewById(R.id.UserName);
        userEmail = (EditText) findViewById(R.id.reg_emai_edit);
        userPass = (EditText) findViewById(R.id.reg_pass_edit);
        reg_submit = (Button) findViewById(R.id.reg_submit_btn);
        reg_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getting input from editText

                String username = userName.getText().toString();
                String useremail = userEmail.getText().toString();
                String userpass = userPass.getText().toString();
                if (!TextUtils.isEmpty(username) || TextUtils.isEmpty(useremail) || TextUtils.isEmpty(userpass)) {
                    mProgress.setTitle("Registering User");
                    mProgress.setMessage("please wait");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    register_user(username, useremail, userpass);
                } else {
                    Toast.makeText(RegisterActivity.this, "please fill All Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void register_user(final String username, String useremail, String userpass) {
        //creating user to Firebase
        regAuth.createUserWithEmailAndPassword(useremail, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Saving users in Database
                  if(regAuth.getCurrentUser()!=null){
                String uid =regAuth.getCurrentUser().getUid();
                        String deviceToken= FirebaseInstanceId.getInstance().getToken();
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users").child(uid);
                HashMap<String, String> map = new HashMap<>();
                map.put("name", username);
                map.put("status", "hey am on PrettyChat");
                map.put("image", "default");
                map.put("thumbnail", "default");
                map.put("deviceToken",deviceToken);
                map.put("online","true");
                databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgress.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            //start Intent of MainActivity
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                        } else {
                            mProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Error in Registered Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }}

        });
    }}


