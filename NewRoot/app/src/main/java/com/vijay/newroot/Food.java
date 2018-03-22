package com.vijay.newroot;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by HP on 1/23/2018.
 */
public class Food extends Fragment {
    @Nullable
    private RecyclerView mFoodList,creditList;
    private DatabaseReference mDatabase,databaseUsers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mManager,manager;
    private Query query,query2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container, false);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mFoodList = (RecyclerView) view.findViewById(R.id.blog_list);
        creditList = (RecyclerView) view.findViewById(R.id.blog_list2);
        mFoodList.setHasFixedSize(true);
        creditList.setHasFixedSize(true);
        mFoodList.setNestedScrollingEnabled(false);
        creditList.setNestedScrollingEnabled(false);
        mManager=new LinearLayoutManager(this.getActivity());
        manager=new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,true);
        mManager.setStackFromEnd(true);
        mManager.setReverseLayout(true);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        mFoodList.setLayoutManager(mManager);
        creditList.setLayoutManager(manager);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        databaseUsers.keepSynced(true);
        query=mDatabase.orderByChild("title").equalTo("Food");
        query2=databaseUsers.orderByChild("credit").limitToLast(6);
        mFoodList.setAdapter(new FoodAdapter(Blog.class,R.layout.blog_row,BlogViewHolder.class,query,getActivity()));
        creditList.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query2,getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query=mDatabase.orderByChild("title").equalTo("Food");
                query2=databaseUsers.orderByChild("credit").limitToLast(6);
                mFoodList.setAdapter(new FoodAdapter(Blog.class,R.layout.blog_row,BlogViewHolder.class,query, getActivity()));
                creditList.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query2,getActivity()));
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;

    }

}
class FoodAdapter extends FirebaseRecyclerAdapter<Blog,BlogViewHolder> {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Boolean mProcessLike=false,mCredit=false;
    private DatabaseReference DatabaseUser;
    static int credit;
    public Context mcontext;
    private DatabaseReference mDatabaseLike,mDatabaseLikeCount;
    public FoodAdapter(Class<Blog> modelClass, int modelLayout, Class<BlogViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mcontext=context;
    }
    @Override
    protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
        mDatabaseLikeCount = FirebaseDatabase.getInstance().getReference().child("Blog");
        DatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        final String post_key = getRef(position).getKey();
        final String uid=model.getUid();
        viewHolder.setDesc(model.getDesc());
        viewHolder.setLocation(model.getLocation());
        viewHolder.setImage(mcontext, model.getImage());
        viewHolder.setProfile(new Food().getActivity(), model.getProfileImage());
        viewHolder.setUsername(model.getUsername());
        viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTime()));
        viewHolder.setLikeBtn(post_key);
        viewHolder.setLikeCount(model.getLikeCount());
        viewHolder.setCommentCount(post_key);
//        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent singleBlogIntent = new Intent(mcontext, BlogSingleActivity.class);
//                singleBlogIntent.putExtra("blog_id", post_key);
//                mcontext.startActivity(singleBlogIntent);
//
//            }
//        });
        viewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BlogIntent = new Intent(mcontext, Post_Comment.class);
                BlogIntent.putExtra("EXTRA_POST_KEY", post_key);
                mcontext.startActivity(BlogIntent);

            }
        });
        viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                mCredit=true;
                mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDatabaseLikeCount.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                int likecount = 0;
                                likecount = dataSnapshot.child(post_key).child("likeCount").getValue(Integer.class);
                                mDatabaseLikeCount.child(post_key).child("likeCount").setValue(likecount - 1);
                                DatabaseUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(mCredit) {
                                            mCredit=false;
                                            credit = dataSnapshot.child(uid).child("credit").getValue(Integer.class);
                                            Log.d("VALUE OF CREDIT IS:", Integer.toString(credit));
                                            DatabaseUser.child(uid).child("credit").setValue(credit-1);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                mProcessLike = false;
                            } else {

                                mDatabaseLikeCount.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                int likecount = 0;
                                likecount = dataSnapshot.child(post_key).child("likeCount").getValue(Integer.class);
                                mDatabaseLikeCount.child(post_key).child("likeCount").setValue(likecount + 1);
                                DatabaseUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(mCredit) {
                                            mCredit=false;
                                            credit = dataSnapshot.child(uid).child("credit").getValue(Integer.class);
                                            Log.d("VALUE OF CREDIT IS:", Integer.toString(credit));
                                            DatabaseUser.child(uid).child("credit").setValue(credit+1);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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

}
