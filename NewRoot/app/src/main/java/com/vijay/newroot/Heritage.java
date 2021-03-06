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
 * Created by HP on 1/27/2018.
 */
public class Heritage extends Fragment {
    private RecyclerView mHeritageList,creditlist;
    private DatabaseReference mDatabase,databaseUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager Manager,manager;
    private Query query,query2;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container, false);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mHeritageList = (RecyclerView) view.findViewById(R.id.blog_list);
        creditlist = (RecyclerView) view.findViewById(R.id.blog_list2);
        mHeritageList.setNestedScrollingEnabled(false);
        creditlist.setNestedScrollingEnabled(false);
        Manager=new LinearLayoutManager(this.getActivity());
        manager=new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,true);
        Manager.setReverseLayout(true);
        Manager.setStackFromEnd(true);
        mHeritageList.setHasFixedSize(true);
        creditlist.setHasFixedSize(true);
        mHeritageList.setLayoutManager(Manager);
        creditlist.setLayoutManager(manager);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUser=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        databaseUser.keepSynced(true);
        query=mDatabase.orderByChild("title").equalTo("Heritage");
        query2=databaseUser.orderByChild("credit").limitToLast(6);
        mHeritageList.setAdapter(new HeritageAdapter(Blog.class,R.layout.blog_row,BlogViewHolder.class,query,getActivity()));
        creditlist.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query2,getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query=mDatabase.orderByChild("title").equalTo("Heritage");
                query2=databaseUser.orderByChild("credit").limitToLast(6);
                creditlist.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query2,getActivity()));
                mHeritageList.setAdapter(new HeritageAdapter(Blog.class,R.layout.blog_row,BlogViewHolder.class,query,getActivity()));
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;

    }

}
class HeritageAdapter extends FirebaseRecyclerAdapter<Blog,BlogViewHolder> {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Boolean mProcessLike=false,mCredit=false;
    static int credit;
    private DatabaseReference DatabaseUser;
    public Context mcontext;
    private DatabaseReference mDatabaseLike,mDatabaseLikeCount;
    public HeritageAdapter(Class<Blog> modelClass, int modelLayout, Class<BlogViewHolder> viewHolderClass, Query ref,Context context) {
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
        viewHolder.setProfile(new Heritage().getActivity(), model.getProfileImage());
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

