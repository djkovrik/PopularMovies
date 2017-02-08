package com.sedsoftware.udacity.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.fragments.DetailsFragment;
import com.sedsoftware.udacity.popularmovies.fragments.PageFragment;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE &&
                getResources().getBoolean(R.bool.landscape_details_disallowed)) {

            finish();
            return;
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey(PageFragment.ARG_URI)) {

            Uri contentUri = extras.getParcelable(PageFragment.ARG_URI);
            int movieId = intent.getIntExtra(PageFragment.ARG_ID, 0);

            DetailsFragment newDetails = DetailsFragment.newInstance(contentUri, movieId);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, newDetails);
            ft.commit();
        }
    }
}
