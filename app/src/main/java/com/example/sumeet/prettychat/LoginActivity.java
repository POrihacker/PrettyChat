package com.example.sumeet.prettychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    Toolbar mToolbar;
    EditText loginEmail,loginPass;
    Button loginSubmit_btn;
    FirebaseAuth logAuth;
    ProgressDialog mProgress;
    DatabaseReference mUserdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Login User");
        mProgress.setMessage("Please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mUserdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logAuth=FirebaseAuth.getInstance();
        loginEmail= (EditText) findViewById(R.id.login_email);
        loginPass= (EditText) findViewById(R.id.login_pass);
        loginSubmit_btn= (Button) findViewById(R.id.btn_submit);
        loginSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logEmail=loginEmail.getText().toString();
                String logPass=loginPass.getText().toString();
                mProgress.show();
                loginUser(logEmail,logPass);
            }
        });

    }

    private void loginUser(String logEmail, String logPass) {
        logAuth.signInWithEmailAndPassword(logEmail,logPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String currentUser=logAuth.getCurrentUser().getUid();
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                  mUserdatabase.child(currentUser).child("deviceToken").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          mProgress.dismiss();
                          Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);
                          finish();
                      }
                  });



                }else {mProgress.hide();
                    Toast.makeText(LoginActivity.this,"Error Login,Check email or password are correct. ",Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
