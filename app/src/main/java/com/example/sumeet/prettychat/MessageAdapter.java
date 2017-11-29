package com.example.sumeet.prettychat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sumeet.prettychat.ChatActivity.userId;

/**
 * Created by sumeet on 2017-10-14.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Messages> mMessageList;
    private FirebaseAuth cAuth;
    private DatabaseReference mDatabase;
    Context context;

    public MessageAdapter(List<Messages> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        this.context = context;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;
        public CircleImageView chathead;
        public TextView from, chat_user, time_chat;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messageBox);
            chathead = (CircleImageView) itemView.findViewById(R.id.chat_head);
            chat_user = (TextView) itemView.findViewById(R.id.chat_name);


        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.message_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.ViewHolder holder, int position) {
        final Messages c = mMessageList.get(position);
        cAuth=FirebaseAuth.getInstance();
        String currentUser = cAuth.getCurrentUser().getUid();
        String fromUser=c.getFrom();
        final String message=c.getMessage();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(fromUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String thumbnail=dataSnapshot.child("thumbnail").getValue().toString();
                holder.chat_user.setText(name);
                Picasso.with(context).load(thumbnail).placeholder(R.drawable.default_image).into(holder.chathead);
                holder.messageText.setText(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
