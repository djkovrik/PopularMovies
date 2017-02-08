package com.sedsoftware.udacity.popularmovies.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.activities.DetailsActivity;
import com.sedsoftware.udacity.popularmovies.adapters.MoviesAdapter;
import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;
import com.sedsoftware.udacity.popularmovies.utils.NetworkUtils;
import com.sedsoftware.udacity.popularmovies.utils.SyncUtils;

public class PageFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.GridItemClickListener {

    public static final String ARG_URI = "CONTENT_URI";
    public static final String ARG_ID = "MOVIE_ID";

    private static final int ID_MOVIES_LOADER = 123;

    private static final String[] PROJECTION = new String[]{
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_POSTER_PATH
    };

    private static final Uri[] CONTENT_URI_VARIANTS = new Uri[]{
            MovieEntry.CONTENT_URI_POPULAR,
            MovieEntry.CONTENT_URI_TOP_RATED,
            MovieEntry.CONTENT_URI_FAVORITES
    };

    private MoviesAdapter mAdapter;
    private Uri mCurrentContentUri;
    private boolean mDualPane;


    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, CONTENT_URI_VARIANTS[page]);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (NetworkUtils.isInternetConnectionAvailable(getContext())) {
            SyncUtils.createSyncAccount(context);
        } else {
            Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentContentUri = getArguments().getParcelable(ARG_URI);

        getLoaderManager().initLoader(ID_MOVIES_LOADER, null, PageFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_movies_list);

        int columnsCount = getResources().getInteger(R.integer.grid_columns_count);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getActivity(), columnsCount, LinearLayoutManager.VERTICAL, false);

        mAdapter = new MoviesAdapter(this, getActivity());
        mAdapter.setHasStableIds(true);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        View detailsView = getActivity().findViewById(R.id.details_container);
        mDualPane = detailsView != null && detailsView.getVisibility() == View.VISIBLE;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_MOVIES_LOADER:
                return new CursorLoader(getActivity(),
                        mCurrentContentUri,
                        PROJECTION,
                        null, null, null);
            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onGridItemClick(int clickedMovieId) {
        showMovieDetails(clickedMovieId);
    }

    private void showMovieDetails(int currentMovieId) {

        if (mDualPane) {
            // Replace fragment
            DetailsFragment newDetails = DetailsFragment.newInstance(mCurrentContentUri, currentMovieId);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.details_container, newDetails);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            // Launch activity
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra(ARG_URI, mCurrentContentUri);
            intent.putExtra(ARG_ID, currentMovieId);
            startActivity(intent);
        }
    }
}