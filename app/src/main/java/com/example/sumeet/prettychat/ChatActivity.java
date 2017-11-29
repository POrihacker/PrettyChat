package com.example.sumeet.prettychat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar mToolbar;
  public static String userId;
   DatabaseReference databaseReference;
    DatabaseReference mUserDatabase;
    DatabaseReference rootData;
    DatabaseReference currentRef;
    DatabaseReference mNotification;
     TextView dispay_chat_name,last_seen;
     CircleImageView icon_chat;
    EditText typeMessageEdit;
    ImageButton sendMessageBtn;
    String groupName;
    String CurrentUid;
    FirebaseUser CurrentUser;
    RecyclerView RecyclerMessage;
    MessageAdapter messageAdapter;
    private final List messageList=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);



        mToolbar = (Toolbar) findViewById(R.id.groupuser_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View barView = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(barView);
        messageAdapter=new MessageAdapter(messageList,getApplicationContext());
        //RecyclerView
        RecyclerMessage= (RecyclerView) findViewById(R.id.MessageRecyclerView);
        RecyclerMessage.setLayoutManager(new LinearLayoutManager(this));
        RecyclerMessage.setHasFixedSize(true);
        RecyclerMessage.setAdapter(messageAdapter);
        dispay_chat_name = (TextView) findViewById(R.id.display_name_chat);
        last_seen = (TextView) findViewById(R.id.last_seen_chat);
        icon_chat = (CircleImageView) findViewById(R.id.icon_chat);
        groupName = getIntent().getStringExtra("user_name");
        dispay_chat_name.setText(groupName);
        sendMessageBtn= (ImageButton) findViewById(R.id.send_message);
        typeMessageEdit= (EditText) findViewById(R.id.type_message);
        //USER iD
        userId = getIntent().getStringExtra("from_user_id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rootData = FirebaseDatabase.getInstance().getReference();
        currentRef = FirebaseDatabase.getInstance().getReference();
        CurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        CurrentUid=CurrentUser.getUid();
        loadMessages();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumbnail").getValue().toString();
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_image).into(icon_chat);
                databaseReference.keepSynced(true);
                if (online.equals("true")) {
                    last_seen.setText("online");
                } else {

                   //GetTimeAgo getTimeAgo = new GetTimeAgo();
                    //Long longtime = Long.parseLong(online);
                    //String lastseen = getTimeAgo.getTimeAgo(longtime, getApplicationContext());
                    last_seen.setText("Offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

   //---------------------------------Chat Database---------------------------------------------------------------------------

        rootData.child("Chat").child(CurrentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId)){

                    Map ChatAddMap=new HashMap();
                    ChatAddMap.put("seen",false);
                    ChatAddMap.put("timestamp",ServerValue.TIMESTAMP);

                    Map ChatUserMap=new HashMap();
                    ChatUserMap.put("Chat/" + CurrentUid + "/" + userId,ChatAddMap);
                    ChatUserMap.put("Chat/" + userId + "/" + CurrentUid,ChatAddMap);
                    rootData.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("ChatLog",databaseError.getMessage().toString());
                            }
                        }
                    });


                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    sendMessageBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendMessage();
        }
    });
    }

    private void loadMessages() {
 rootData.child("messages").child(CurrentUid).child(userId).addChildEventListener(new ChildEventListener() {
     @Override
     public void onChildAdded(DataSnapshot dataSnapshot, String s) {
     Messages messages=dataSnapshot.getValue(Messages.class);
         messageList.add(messages);
         messageAdapter.notifyDataSetChanged();
         RecyclerMessage.scrollToPosition(messageList.size()-1);
         rootData.keepSynced(true);
     }

     @Override
     public void onChildChanged(DataSnapshot dataSnapshot, String s) {

     }

     @Override
     public void onChildRemoved(DataSnapshot dataSnapshot) {

     }

     @Override
     public void onChildMoved(DataSnapshot dataSnapshot, String s) {

     }

     @Override
     public void onCancelled(DatabaseError databaseError) {

     }
 });



    }

    private void sendMessage() {
        //SendMesages
        String chatMessage=typeMessageEdit.getText().toString();
        if(!TextUtils.isEmpty(chatMessage)){
            //
      String current_user_ref="messages/" +  CurrentUid+"/"+userId;
            String other_user_ref="messages/" +userId + "/" + CurrentUid;
            DatabaseReference user_message_push=rootData.child("messages").child(CurrentUid).child(userId).push();
            String push_message_id=user_message_push.getKey();
            Map messageMap=new HashMap();
            messageMap.put("message",chatMessage);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",CurrentUid);
            messageMap.put("user",groupName);
              typeMessageEdit.setText("");
            Map messageUserMap=new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_message_id,messageMap);
            messageUserMap.put(other_user_ref + "/" + push_message_id, messageMap);
            rootData.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                  if (databaseError!=null){
                      Log.d("messageLod",databaseError.getMessage().toString());
                  }
                }
            });

            HashMap<String,String> notificationMap=new HashMap<>();
            notificationMap.put("from",CurrentUid);
            notificationMap.put("type","message");
            notificationMap.put("message",chatMessage);
            rootData.child("Notifications").child(userId).push().setValue(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu options to Toolbar
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.user_profile_Item:
                //start ProfileUser Intent
                Toast.makeText(this, "opened user Profile", Toast.LENGTH_SHORT).show();
                Intent startProfileIntent = new Intent(this, ProfileActivity.class);
                startProfileIntent.putExtra("User1", userId);
                startActivity(startProfileIntent);

            case R.id.user_delete_chat:
                //delete Messages in Database  b/ users,

        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            mUserDatabase.child("online").setValue("true");
    }
}


}
