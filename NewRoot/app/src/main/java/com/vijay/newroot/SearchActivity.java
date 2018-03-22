package com.vijay.newroot;

import android.content.Intent;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private Boolean mProcessLike=false;
    private DatabaseReference mDatabaseLike,mDatabaseLikeCount;
    private EditText searchtext;
    private ImageButton searchButton;
    private ImageView sad;
    private TextView textno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mAuth=FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mBlogList=(RecyclerView)findViewById(R.id.search_list);
        searchButton=(ImageButton)findViewById(R.id.searchbtn);
        searchtext=(EditText)findViewById(R.id.searchedit);
        mBlogList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        mBlogList.setLayoutManager(layoutManager);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLikeCount = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseLikeCount.keepSynced(true);
        sad=(ImageView)findViewById(R.id.sad);
        textno=(TextView)findViewById(R.id.resultwrong);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });

        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    String searchText=searchtext.getText().toString();
                firebaseUserSearch(StringUtils.capitalize(searchText.toLowerCase()));
                }
                return false;
            }
        });
    }
    private void firebaseUserSearch(String searchText){
        Query firebasequery=mDatabase.orderByChild("location").startAt(searchText).endAt(searchText+"\uf8ff");
        if(firebasequery.equals(null)){
            sad.setVisibility(View.VISIBLE);
            textno.setVisibility(View.VISIBLE);

        }
        else {
            FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                    Blog.class, R.layout.blog_row, BlogViewHolder.class, firebasequery
            ) {
                @Override
                protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {

                    final String post_key = getRef(position).getKey();
                    viewHolder.setDesc(model.getDesc());
                    viewHolder.setLocation(model.getLocation());
                    viewHolder.setImage(getApplicationContext(), model.getImage());
                    viewHolder.setProfile(getApplicationContext(), model.getProfileImage());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTime()));
                    viewHolder.setLikeBtn(post_key);
                    viewHolder.setLikeCount(model.getLikeCount());
                    viewHolder.setCommentCount(post_key);
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent singleBlogIntent = new Intent(SearchActivity.this, BlogSingleActivity.class);
                            singleBlogIntent.putExtra("blog_id", post_key);
                            startActivity(singleBlogIntent);

                        }
                    });
                    viewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent BlogIntent = new Intent(SearchActivity.this, Post_Comment.class);
                            BlogIntent.putExtra("EXTRA_POST_KEY", post_key);
                            startActivity(BlogIntent);

                        }
                    });
                    viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mProcessLike = true;

                            mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabaseLikeCount.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            int likecount = 0;
                                            likecount = dataSnapshot.child(post_key).child("likeCount").getValue(Integer.class);
                                            mDatabaseLikeCount.child(post_key).child("likeCount").setValue(likecount - 1);
                                            mProcessLike = false;
                                        } else {

                                            mDatabaseLikeCount.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                            int likecount = 0;
                                            likecount = dataSnapshot.child(post_key).child("likeCount").getValue(Integer.class);
                                            mDatabaseLikeCount.child(post_key).child("likeCount").setValue(likecount + 1);
                                            mProcessLike = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });
                }
            };
            mBlogList.setAdapter(firebaseRecyclerAdapter);
        }

    }
}
