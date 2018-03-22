package com.vijay.newroot;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1,REQUEST_CAMERA=2,CROP_FROM_CAMERA=4;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private Uri mImageUri;
    private EditText mPostDesc;
    private Spinner mPostTitle;
    private TextView mLocation;
    private ImageButton mSelectImage;
    private StorageReference mStorage;
    private Button mSubmitBtn;
    private ProgressDialog mProgress;
    int permissionCheck;
    int PLACE_PICKER_REQUEST=3;
    private ImageView imageView;
    private  static int credit;
    static boolean creditva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        mPostTitle = (Spinner) findViewById(R.id.titleField);
        mPostDesc = (EditText) findViewById(R.id.descField);
        mLocation = (TextView) findViewById(R.id.locationField);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        imageView = (ImageView) findViewById(R.id.imageSelect2);
        mPostDesc.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mPostDesc, InputMethodManager.SHOW_IMPLICIT);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mProgress=new ProgressDialog(this);
        mSelectImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                permissionCheck= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck== PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
                }
                else{
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheck= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
                if(permissionCheck== PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, PLACE_PICKER_REQUEST);
                }
                else {
//                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                    Intent intent;

                    try {
                        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                .build();
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(PostActivity.this);

                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
//                        intent = builder.build(PostActivity.this);
//                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditva=true;
                startPosting();
            }
        });


    }
//    private void showPictureDialog(){
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {
//                "Select photo from gallery",
//                "Capture photo from camera" };
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                choosePhotoFromGallary();
//                                break;
//                            case 1:
//                                takePhotoFromCamera();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }
//    public void choosePhotoFromGallary() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, GALLERY_REQUEST);
//    }
//    private void takePhotoFromCamera() {
//        permissionCheck=ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);
//        if (permissionCheck== PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(PostActivity.this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
//        }
//        else{
//            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, REQUEST_CAMERA);
//
//
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA) {
//
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,REQUEST_CAMERA);
//            } else {
//
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//
//            }
//
//
//        }
        if (requestCode == PLACE_PICKER_REQUEST) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(PostActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }


            } else {

                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }
    private void startPosting() {

        final String title_val = mPostTitle.getSelectedItem().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        final String location_val=mLocation.getText().toString().trim();

            if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null && !TextUtils.isEmpty(location_val)) {
                mProgress.setMessage("Posting ...");
                mProgress.show();
                StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());


                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                    byte[] data = baos.toByteArray();

                    filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            final DatabaseReference newPost = mDatabase.push();


                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if(creditva) {
                                            credit = dataSnapshot.child("credit").getValue(Integer.class);
                                            newPost.child("title").setValue(title_val);
                                            newPost.child("desc").setValue(desc_val);
                                            newPost.child("image").setValue(downloadUrl.toString());
                                            newPost.child("uid").setValue(mCurrentUser.getUid());
                                            newPost.child("time").setValue(System.currentTimeMillis());
                                            newPost.child("likeCount").setValue(0);
                                            newPost.child("location").setValue(location_val);
                                            newPost.child("profileImage").setValue(dataSnapshot.child("image").getValue());
                                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        creditva = false;
                                                        mDatabaseUser.child("credit").setValue(credit + 5);
                                                        Toast.makeText(getApplicationContext(),"Hurray! You get 5 credit!",Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(PostActivity.this, HomeActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                        finish();

                                                    }

                                                }
                                            });
                                        } }

                                    @Override
                                    public void onCancelled (DatabaseError databaseError){

                                    }
                                 });

                        }
                    });
                }
             catch (IOException e) {
            e.printStackTrace();
        }

            }
        else{
                if(TextUtils.isEmpty(title_val)){
                    Toast.makeText(getApplicationContext(),"Need to choose a Category", Toast.LENGTH_SHORT).show();

                }
                if(TextUtils.isEmpty(location_val)){
                    Toast.makeText(getApplicationContext(),"Need to choose a Location", Toast.LENGTH_SHORT).show();
                }
                if(mImageUri==null){
                    Toast.makeText(getApplicationContext(),"Need to choose a Image", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(desc_val)){
                    Toast.makeText(getApplicationContext()," Need to give a Discription", Toast.LENGTH_SHORT).show();
                }
            }


    }
  public void addcredit(){

  }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if (requestCode == GALLERY_REQUEST ) {
            mImageUri = data.getData();

                CropImage.activity(mImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode ==RESULT_OK) {
                    mImageUri = result.getUri();
                    imageView.setImageURI(mImageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    result.getError();
                }
            }



//        if (requestCode == REQUEST_CAMERA ) {
//            Bundle bundle=data.getExtras();
//            Bitmap bitmap= (Bitmap) bundle.get("data");
//            mSelectImage.setImageBitmap(bitmap);
//
//
//            }

            if(requestCode==PLACE_PICKER_REQUEST ){
                Place place=PlaceAutocomplete.getPlace(this,data);
                mLocation.setText(place.getAddress());
            }

            }



        }


}



