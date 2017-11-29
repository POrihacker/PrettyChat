package com.example.sumeet.prettychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView imageView;
    TextView nametxt, statustxt;
    DatabaseReference databaseReference;
    DatabaseReference mCurrentDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolbar = (Toolbar) findViewById(R.id.profile_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        imageView = (ImageView) findViewById(R.id.imageView);
        nametxt = (TextView) findViewById(R.id.profiletxt);
        statustxt = (TextView) findViewById(R.id.statustxt);

        String uid=getIntent().getStringExtra("User1");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mCurrentDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayname=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                displayProfile(displayname,status,image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentDatabase.child("online").setValue("true");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurrentDatabase.child("online").setValue("true");
    }

    private void displayProfile(String displayname, String status, String image) {
        nametxt.setText(displayname);
        statustxt.setText(status);
        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_image).into(imageView);
    }







}
