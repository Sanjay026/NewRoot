package com.vijay.newroot;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by HP on 3/16/2018.
 */

public class FirebaseCredit extends FirebaseRecyclerAdapter<Users,CreditHolder> {
    Context context;

    public FirebaseCredit(Class<Users> modelClass, int modelLayout, Class<CreditHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context=context;
    }

    @Override
    protected void populateViewHolder(CreditHolder viewHolder, Users model, int position) {
        viewHolder.setImage(context,model.getImage());
    }
}
