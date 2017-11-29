package com.example.sumeet.prettychat;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements Friends.OnFragmentInteractionListener, Chat.OnFragmentInteractionListener, Explore.OnFragmentInteractionListener {
    FirebaseAuth firebaseAuth;
    Toolbar mToolbar;
    ViewPager viewPager;
    ViewpageAdapter viewpageAdapter;
    TabLayout tabLayout;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("PrettyChat");

        //Firebase Initi
        if (firebaseAuth.getCurrentUser()!=null){
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());}
        //Viewpager and Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablay);
        tabLayout.addTab(tabLayout.newTab().setText("All USERS"));
        tabLayout.addTab(tabLayout.newTab().setText("CHAT"));
        tabLayout.addTab(tabLayout.newTab().setText("EXPLORE"));
        viewpageAdapter = new ViewpageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewpageAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onStart() {


        super.onStart();
        //direct user to start if not logged/registered
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            toStartActivity();
        } else {

            databaseReference.child("online").setValue("true");
        }
        }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if(currentUser!=null){
                databaseReference.child("online").setValue("false");}
    }



    private void toStartActivity() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu options to Toolbar
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.account_Item) {
            Intent accountIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(accountIntent);
        }
        if (item.getItemId() == R.id.groups_item) {

        }
        if (item.getItemId() == R.id.logout_item) {
            if(firebaseAuth.getCurrentUser()!=null){
            firebaseAuth.signOut();
            databaseReference.child("online").setValue("false");
            toStartActivity();
        }}

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
