package com.vijay.newroot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView profileGrid;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    int spanCount = 3; // 3 columns
    int spacing = 5; // 50px
    boolean includeEdge = false;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        profileGrid = (RecyclerView) findViewById(R.id.profileList);
        progressBar= (ProgressBar)findViewById(R.id.progress_bar);
        mAuth=FirebaseAuth.getInstance();
        profileGrid.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,3,LinearLayoutManager.VERTICAL,false);
        profileGrid.setLayoutManager(layoutManager);
        profileGrid.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=databaseReference.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        FirebaseRecyclerAdapter<Blog,ProfileHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, ProfileHolder>(Blog.class,
                R.layout.card_view,
                ProfileHolder.class,
                query) {
            @Override
            protected void populateViewHolder(ProfileHolder viewHolder, final Blog model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleBlogIntent = new Intent(getApplicationContext(), BlogSingleActivity.class);
                singleBlogIntent.putExtra("blog_id", post_key);
                singleBlogIntent.putExtra("likeValue",model.getLikeCount());
                startActivity(singleBlogIntent);
                 finish();
                    }
                });
            }
        };
        profileGrid.setAdapter(firebaseRecyclerAdapter);
        progressBar.setVisibility(View.GONE);

    }

  public static class ProfileHolder extends RecyclerView.ViewHolder{
        ImageView image;
        View mView;
        ProgressBar progressBar;
        public ProfileHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setImage(Context context,String key){
            image= (ImageView)mView.findViewById(R.id.imageId);
            progressBar= (ProgressBar) mView.findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(key).placeholder(R.drawable.fb_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
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
            }).into(image);
        }
    }


}
class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}


