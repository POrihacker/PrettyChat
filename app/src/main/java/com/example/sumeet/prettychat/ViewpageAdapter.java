package com.example.sumeet.prettychat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by sumeet on 2017-10-09.
 */

public class ViewpageAdapter extends FragmentPagerAdapter{

int tabCount;
    public ViewpageAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount=tabCount;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Friends friends=new Friends();
                return  friends;
            case 1:
                Chat chat=new Chat();
                return chat;
            case 2:
                Explore explore=new Explore();
                return explore;
            default:return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
