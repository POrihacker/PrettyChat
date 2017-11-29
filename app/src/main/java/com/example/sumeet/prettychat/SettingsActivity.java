package com.example.sumeet.prettychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    TextView displayName, Status;
    CircleImageView userImage;
    Button changeProfile_btn, changeStatus_btn;
    Toolbar mToolbar;
    private int REQUEST_CODE_GALLERY = 1;
    private StorageReference mImageStorageRef;
    private  FirebaseAuth regAuth;
    DatabaseReference mDatabaseRef;
    FirebaseDatabase fireRef;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        displayName = (TextView) findViewById(R.id.display_name);
        Status = (TextView) findViewById(R.id.user_status);
        userImage = (CircleImageView) findViewById(R.id.group_icon);
        changeProfile_btn = (Button) findViewById(R.id.change_image);
        changeStatus_btn = (Button) findViewById(R.id.change_status);
        mToolbar = (Toolbar) findViewById(R.id.settings_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        mProgress = new ProgressDialog(this);
        //firebase Storage
        mImageStorageRef = FirebaseStorage.getInstance().getReference();
        regAuth=FirebaseAuth.getInstance();
        fireRef=FirebaseDatabase.getInstance();
        //OnclickListener inititialize
        changeProfile_btn.setOnClickListener(this);
        changeStatus_btn.setOnClickListener(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String ImgUrl = dataSnapshot.child("image").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                displayResult(name, status, ImgUrl);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error Retrieving Check your Internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
        databaseReference.child("online").setValue("true");}
    }

    private void displayResult(String name, String status, final String imgUrl) {
        displayName.setText(name);
        Status.setText(status);
        Picasso.with(getApplicationContext()).load(imgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(userImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getApplicationContext()).load(imgUrl).placeholder(R.drawable.default_image).into(userImage);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == changeProfile_btn) {
            //changeProfile intent

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "Choose Image"), REQUEST_CODE_GALLERY);


        }
        if (v == changeStatus_btn) {
            String getStatus = Status.getText().toString();
            Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
            statusIntent.putExtra("getStatus", getStatus);
            startActivity(statusIntent);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //Storing image on Firebase Storage
                Uri resultUri = result.getUri();
                FirebaseUser user = regAuth.getCurrentUser();
                final String uid = user.getUid();

                //thumbnail creator
                File actualImageFile=new File(resultUri.getPath());


                Bitmap thumbnail = null;
                try {
                    thumbnail = new Compressor(this).setMaxWidth(150).setMaxHeight(150).setQuality(60)
                            .compressToBitmap(actualImageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                //Progress Dialog
                mProgress.setTitle("Setting Profile");
                mProgress.setMessage("please Wait");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                //STORING PROFILE IN STORAGE FOLDER

                StorageReference filepath = mImageStorageRef.child("User_profiles").child(uid+".jpg");
                final StorageReference thumbpath=mImageStorageRef.child("User_profiles").child("User_thumbnail").child(uid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            //Adding URL of IMAGE to Database after Storing in STORAGE

                            @SuppressWarnings("VisibleForTests") final
                            String downloadUrl = task.getResult().getDownloadUrl().toString();

                              //iF IMAGE UPLOAD IS SUCCESSFUL START UPLOADING THUMBNAIL
                            //Upload for thumbnail
                            UploadTask uploadTask = thumbpath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    @SuppressWarnings("VisibleForTests") final  String thumbUrl=thumb_task.getResult().getDownloadUrl().toString();
                                      if(thumb_task.isSuccessful()){

                                          //Save/update url of image and Thumbnail in database,

                                          Map  updateImages=new HashMap();
                                          updateImages.put("image",downloadUrl);
                                          updateImages.put("thumbnail",thumbUrl);

                                          fireRef=FirebaseDatabase.getInstance();
                                          mDatabaseRef=fireRef.getReference().child("Users").child(uid);
                                          mDatabaseRef.updateChildren(updateImages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if(task.isSuccessful()){

                                                      mProgress.dismiss();

                                                      Toast.makeText(SettingsActivity.this,"Succesfully Uploaded",Toast.LENGTH_SHORT).show();;}
                                                  else {

                                                      mProgress.hide();
                                                      Toast.makeText(SettingsActivity.this,"Error While Uploading ",Toast.LENGTH_SHORT).show();
                                                      //show error Toast
                                                  }
                                              }
                                          });}else {
                                          Toast.makeText(SettingsActivity.this,"Error While Uploading Thumbnail",Toast.LENGTH_SHORT).show();


                                      }


                                }
                            });


                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
