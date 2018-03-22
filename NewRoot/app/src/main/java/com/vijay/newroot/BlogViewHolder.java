package com.vijay.newroot;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HP on 1/23/2018.
 */
public class BlogViewHolder extends RecyclerView.ViewHolder{
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    View mView;
    DatabaseReference mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Blog");
    ImageButton mLikeBtn,buttonComment;
    TextView like_Count,cmnt_Count;
    ProgressBar progressBar;
    DatabaseReference DatabaseComment=FirebaseDatabase.getInstance().getReference().child("post-comments");
    public BlogViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mLikeBtn = (ImageButton)mView.findViewById(R.id.like_btn);
        buttonComment=(ImageButton)mView.findViewById(R.id.cmnt_btn);
        progressBar= (ProgressBar) mView.findViewById(R.id.progressBar2);

    }
    public void setLikeBtn(final String post_key){
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                    mLikeBtn.setImageResource(R.drawable.ic_favorite_black_24dp2);
                }
                else{
                    mLikeBtn.setImageResource(R.drawable.chatherat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //        public void setTitle(String title){
//
//            TextView post_title=(TextView) mView.findViewById(R.id.post_title);
//            post_title.setText(title);
//        }
    public void setDesc(String desc){
        ReadMoreTextView post_desc=((ReadMoreTextView) mView.findViewById(R.id.post_desc));
        post_desc.setText(desc);
      //  makeTextViewResizable(post_desc, 2, "more", true);

    }
    public void setLocation(String location){
        TextView location_=(TextView)mView.findViewById(R.id.blog_location);
        location_.setText(location);
    }
    public void setTime(CharSequence time){
        TextView post_time=(TextView)mView.findViewById(R.id.tv_time);
        post_time.setText(time);
    }
    public void setUsername(String username){
        TextView post_username= (TextView) mView.findViewById(R.id.post_username);
        post_username.setText(username);
    }
    public void setImage(final Context ctx, final String image){
        final ImageView post_image= (ImageView) mView.findViewById(R.id.post_image);
        // Picasso.with(ctx).load(image).into(post_image);

         progressBar.setVisibility(View.VISIBLE);

        Glide.with(ctx).load(image).fitCenter().placeholder(R.drawable.whiteload).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
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
        }).into(post_image);
    }
    public void setProfile(final Context ctx, final String profile){

        final CircleImageView profile_Image=(CircleImageView)mView.findViewById(R.id.iv_post_owner_image);

        Picasso.with(ctx).load(profile).placeholder(R.drawable.fb_avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profile_Image, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(profile).placeholder(R.drawable.fb_avatar).fit().into(profile_Image);
            }
        });

    }
    public void setLikeCount(int likeCount){
        like_Count=(TextView)mView.findViewById(R.id.like_count);
        like_Count.setText(Integer.toString(likeCount));
    }
    public void setCommentCount(final String key){
        cmnt_Count=(TextView)mView.findViewById(R.id.cmnt_count);
        final int count[]=new int[1];
        DatabaseComment.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 count[0]=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    count[0]++;
                }
                Log.d("THE SIZE OF Here ",String.valueOf(count[0])+key);
                cmnt_Count.setText(String.valueOf(count[0]));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("THE SIZE OF ",String.valueOf(count[0])+key);

    }
//    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
//
//        if (tv.getTag() == null) {
//            tv.setTag(tv.getText());
//        }
//        ViewTreeObserver vto = tv.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//                String text;
//                int lineEndIndex;
//                ViewTreeObserver obs = tv.getViewTreeObserver();
//                obs.removeGlobalOnLayoutListener(this);
//                if (maxLine == 0) {
//                    lineEndIndex = tv.getLayout().getLineEnd(0);
//                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
//                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
//                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//                } else {
//                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
//                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
//                }
//                tv.setText(text); tv.setMovementMethod(LinkMovementMethod.getInstance());
//                tv.setText(
//                        addClickablePartTextViewResizable(tv.getText().toString(), tv, lineEndIndex, expandText,
//                                viewMore), TextView.BufferType.SPANNABLE);
//            }
//        });
//
//    }
//    private static SpannableStringBuilder addClickablePartTextViewResizable(final String strSpanned, final TextView tv,
//                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
//        String str = strSpanned;
//        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
//
//        if (str.contains(spanableText)) {
//            ssb.setSpan(new MySpannable(true) {
//
//                @Override
//                public void onClick(View widget) {
//                    tv.setLayoutParams(tv.getLayoutParams());
//                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                    tv.invalidate();
//                    if (viewMore) {
//                        makeTextViewResizable(tv, -1, "less", false);
//                    } else {
//                        makeTextViewResizable(tv, 2, "more", true);
//                    }
//
//                }
//            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
//
//        }
//        return ssb;
//    }
//}
// class MySpannable extends ClickableSpan {
//
//    private boolean isUnderline = false;
//
//    /**
//     * Constructor
//     */
//    public MySpannable(boolean isUnderline) {
//        this.isUnderline = isUnderline;
//    }
//
//    @Override
//    public void updateDrawState(TextPaint ds) {
//
//        ds.setUnderlineText(isUnderline);
//        ds.setColor(Color.parseColor("#BDBDBD"));
//
//    }
//
//    @Override
//    public void onClick(View widget) {
//
//    }
}