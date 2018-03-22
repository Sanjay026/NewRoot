package com.vijay.newroot;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key=null;
    private DatabaseReference mDatabase,DatabaseUser;
    private FirebaseAuth mAuth;
    private TextView mBlogSingleDesc;
    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle;
    private ImageButton mSingleRemoveBtn;
    private ImageButton image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;
    boolean creditboolean=false;
    int credit;
    static int totallike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        DatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        mPost_key=getIntent().getExtras().getString("blog_id");
        totallike=getIntent().getExtras().getInt("likeValue");
        Log.d("THIS IS LIKEVALUE",Integer.toString(totallike));
        progressBar= (ProgressBar) findViewById(R.id.progressBarblogsingle);
        mBlogSingleImage = (ImageView) findViewById(R.id.singleBlogImage);
        mBlogSingleTitle = (TextView) findViewById(R.id.singleBlogTitle);
        mBlogSingleDesc = (TextView) findViewById(R.id.singleBlogDesc);
        mSingleRemoveBtn=(ImageButton)findViewById(R.id.singleRemoveBtn);
        image=(ImageButton)findViewById(R.id.blogsingleimagebutton);
        progressBar.setVisibility(View.VISIBLE);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        });
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                final String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);
                Glide.with(BlogSingleActivity.this).load(post_image).placeholder(R.drawable.whiteload).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(mBlogSingleImage);

                if (mAuth.getCurrentUser().getUid().toString().equals(post_uid)) {
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).removeValue();
                creditboolean=true;
                DatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(creditboolean){
                            creditboolean=false;
                            credit=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("credit").getValue(Integer.class);
                            long x=totallike*1;
                            DatabaseUser.child(mAuth.getCurrentUser().getUid()).child("credit").setValue(credit-(5+x));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent mainIntent=new Intent(BlogSingleActivity.this,HomeActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            }
        });


    }
}
