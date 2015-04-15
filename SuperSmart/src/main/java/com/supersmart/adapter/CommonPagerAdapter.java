package com.supersmart.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by forest on 2015/3/15.
 */
public class CommonPagerAdapter extends PagerAdapter {

    private ArrayList<View> mViews;
    private String[] mTitles;

    public CommonPagerAdapter(ArrayList<View> views) {
        this(views, null);
    }

    public CommonPagerAdapter(ArrayList<View> views, String[] titles) {
        mTitles = titles;
        mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));

        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
       if(mTitles != null){
           return  mTitles[position];
       }
        return  super.getPageTitle(position);
    }
}
