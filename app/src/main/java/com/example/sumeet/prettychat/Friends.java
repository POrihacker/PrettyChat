package com.example.sumeet.prettychat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Friends extends Fragment {


    private OnFragmentInteractionListener mListener;
    public RecyclerView grouplist;
    DatabaseReference databaseReference;
    FirebaseDatabase dataFire;
    FirebaseUser currentUser;


    public Friends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.friends_list, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataFire = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid().toString();
        databaseReference = dataFire.getReference().child("Users");
         databaseReference.keepSynced(true);
        grouplist = (RecyclerView) getActivity().findViewById(R.id.group_list);
        grouplist.setHasFixedSize(true);
        grouplist.setLayoutManager(new LinearLayoutManager(getContext()));



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseUi
        final FirebaseRecyclerAdapter<FriendsModel, GroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsModel, GroupViewHolder>(
                FriendsModel.class,
                R.layout.group_list_item,
                GroupViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final GroupViewHolder viewHolder, final FriendsModel model, final int position) {
                final String userid = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                final ImageView iconOnline= (ImageView) viewHolder.mVIEW.findViewById(R.id.online_icon);

                databaseReference.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String icon=dataSnapshot.child("online").getValue().toString();
                        if(icon.equals("true")){
                            iconOnline.setVisibility(View.VISIBLE);
                        }else {
                            iconOnline.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //load thumbnail
                final CircleImageView icon = (CircleImageView) viewHolder.mVIEW.findViewById(R.id.group_icon);
                Picasso.with(getContext()).load(model.getThumbnail()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(icon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getContext()).load(model.getThumbnail()).placeholder(R.drawable.default_image).into(icon);
                    }
                });
                ;
                viewHolder.mVIEW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent friendsIntent = new Intent(getContext(), ChatActivity.class);
                        friendsIntent.putExtra("user_name", model.getName());
                        friendsIntent.putExtra("from_user_id", userid);
                        startActivity(friendsIntent);
                    }
                });

            }

        };
        grouplist.setAdapter(firebaseRecyclerAdapter);

    }

    //View Holder Class

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        View mVIEW;
        Context context;

        public GroupViewHolder(View itemView) {
            super(itemView);
            mVIEW = itemView;

        }

        void setName(String name) {

            TextView groupName = (TextView) mVIEW.findViewById(R.id.user_name);
            groupName.setText(name);


        }

        void setStatus(String des) {
            TextView groupDes = (TextView) mVIEW.findViewById(R.id.user_status);
            groupDes.setText(des);
        }


    }
}

