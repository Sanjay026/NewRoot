package com.vijay.newroot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by HP on 3/15/2018.
 */

public class CreditHolder extends RecyclerView.ViewHolder {
    private ImageView image;
   private View mView;

    public CreditHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setImage(final Context context, final String key) {
        image = (ImageView) mView.findViewById(R.id.credit_image);
        Picasso.with(context).load(key).placeholder(R.drawable.fb_avatar).fit().networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
               Picasso.with(context).load(key).fit().placeholder(R.drawable.fb_avatar).into(image);
            }
        });
    }
}