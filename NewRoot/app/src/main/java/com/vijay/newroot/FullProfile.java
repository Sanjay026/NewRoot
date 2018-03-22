package com.vijay.newroot;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class FullProfile extends AppCompatActivity {
    ImageButton back,draw;
    ImageView profile;
    ProgressBar mProgress;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mImageStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_profile);
        back= (ImageButton) findViewById(R.id.backpencil);
        draw= (ImageButton) findViewById(R.id.pencil);
        profile= (ImageView) findViewById(R.id.imagepencil);
        mProgress= (ProgressBar) findViewById(R.id.progressBarpencil);
        mImageStorage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        setImage();

    }
    public void setImage(){
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                final String ref=(String)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("image").getValue();
                Glide.with(getApplicationContext()).load(ref).fitCenter().placeholder(R.drawable.userr).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).into(profile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void changeProfilePhoto(){
        mProgress= (ProgressBar) findViewById(R.id.progressBarpencil);
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress.setVisibility(View.VISIBLE);
        StorageReference filePath=mImageStorage.child(mImageUri.getLastPathSegment());
        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUri = taskSnapshot.getDownloadUrl().toString();
                mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);
                mProgress.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){
            mImageUri=data.getData();
            CropImage.activity(mImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode ==RESULT_OK) {
                mImageUri = result.getUri();
                profile.setImageURI(mImageUri);
                changeProfilePhoto();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError();
            }
        }
    }

}
