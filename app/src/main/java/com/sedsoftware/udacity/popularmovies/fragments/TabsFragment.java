package com.sedsoftware.udacity.popularmovies.fragments;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.adapters.SimpleFragmentPagerAdapter;

public class TabsFragment extends Fragment {

    public TabsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getFragmentManager(), getContext()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabslayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // EmptyFragment creation, only for sw600+ devices (handled by showEmptyDetails),
        // in other cases details_container is not available
        Resources res = getResources();
        boolean showEmptyDetails = res.getBoolean(R.bool.show_empty_details);
        int orientation = res.getConfiguration().orientation;
        Fragment fragment = getFragmentManager().findFragmentById(R.id.details_container);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE && fragment == null && showEmptyDetails) {
            EmptyFragment emptyFragment = new EmptyFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, emptyFragment).commit();
        }
    }
}
