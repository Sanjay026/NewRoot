package com.vijay.newroot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by HP on 1/23/2018.
 */
public class Pager extends FragmentStatePagerAdapter {
    //integer to count tabs
    int tabcount;
    //Constructor to the class
    public Pager(FragmentManager fm, int tabcount) {
        super(fm);
        this.tabcount=tabcount;
    }

    @Override
    public Fragment getItem(int position) {
        //retruning the tabcount
        switch (position) {
            case 0:
                Feed tab1 = new Feed();
                return tab1;
            case 1:
                Nature tab2=new Nature();
                return tab2;
            case 2:
                Food tab3=new Food();
                return tab3;
            case 3:
                Culture tab4=new Culture();
                return tab4;
            case 4:
                Heritage tab5=new Heritage();
                return tab5;
            default:
                return null;

        }
    }


    @Override
    public int getCount() {
        return tabcount;
    }
}
