package com.sedsoftware.udacity.popularmovies.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.fragments.PageFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = context.getResources().getStringArray(R.array.tab_names);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}