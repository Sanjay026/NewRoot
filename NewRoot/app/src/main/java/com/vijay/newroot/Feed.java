package com.vijay.newroot;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Created by HP on 1/27/2018.
 */
public class Feed extends android.support.v4.app.Fragment {
    private RecyclerView feedlist,creditlist;
    private LinearLayoutManager manager,manager2;
    DatabaseReference mDatabase,databaseUsers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView confusee;
    private TextView wentwrong;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.feed,container,false);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        feedlist=(RecyclerView)view.findViewById(R.id.blog_list);
        creditlist=(RecyclerView)view.findViewById(R.id.blog_list2);
        feedlist.setHasFixedSize(true);
        creditlist.setHasFixedSize(true);
        feedlist.setNestedScrollingEnabled(false);
        creditlist.setNestedScrollingEnabled(false);
        manager=new LinearLayoutManager(getActivity());
        manager2=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        manager2.setAutoMeasureEnabled(true);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        manager2.setReverseLayout(true);
        manager2.setStackFromEnd(true);
        feedlist.setLayoutManager(manager);
        creditlist.setLayoutManager(manager2);
        confusee=(ImageView)view.findViewById(R.id.confuse);
        wentwrong=(TextView)view.findViewById(R.id.wentwrong);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        databaseUsers.keepSynced(true);
        Query query=databaseUsers.orderByChild("credit").limitToLast(6);


        if(isOnline()) {
            feedlist.setAdapter(new FirebaseAdapter(Blog.class, R.layout.blog_row, BlogViewHolder.class, mDatabase, getActivity()));
            creditlist.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query,getActivity()));
//            callCreditFirebase();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Query query=databaseUsers.orderByChild("credit").limitToLast(6);
                feedlist.setAdapter(new FirebaseAdapter(Blog.class,R.layout.blog_row,BlogViewHolder.class,mDatabase,getActivity()));
                creditlist.setAdapter(new FirebaseCredit(Users.class,R.layout.card_view2,CreditHolder.class,query,getActivity()));
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;

    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            confusee.setVisibility(View.VISIBLE);
            wentwrong.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }




}


class FirebaseAdapter extends FirebaseRecyclerAdapter<Blog,BlogViewHolder> {
    private DatabaseReference mDatabase,DatabaseUser;
    private FirebaseAuth mAuth;
    private Boolean mProcessLike=false,mCredit=false;
    public Context mcontext;
    private DatabaseReference mDatabaseLikeCount;
    static  int credit;

    public FirebaseAdapter(Class<Blog> modelClass, int modelLayout, Class<BlogViewHolder> viewHolderClass, DatabaseReference ref, Context mcontext) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mcontext = mcontext;
    }



    @Override
    protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        mDatabaseLikeCount = FirebaseDatabase.getInstance().getReference().child("Blog");
        DatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        final String post_key = getRef(position).getKey();
        final String uid=model.getUid();
        viewHolder.setDesc(model.getDesc());
        viewHolder.setLocation(model.getLocation());
        viewHolder.setImage(mcontext, model.getImage());
        viewHolder.setProfile(new Feed().getActivity(), model.getProfileImage());
        viewHolder.setUsername(model.getUsername());
        viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTime()));
        viewHolder.setLikeBtn(post_key);
        viewHolder.setLikeCount(model.getLikeCount());
        viewHolder.setCommentCount(post_key);
        viewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BlogIntent = new Intent(mcontext,Post_Comment.class);
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
                                Log.d("CREDIT VALUE HERE",Integer.toString(credit));
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
                                Log.d("CREDIT VALUE HERE",Integer.toString(credit));

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


                            }
                            mProcessLike = false;




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




