package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

        /*
        Intent intent = getIntent();
        String message = intent.getStringExtra(Intent.EXTRA_TEXT);
        TextView textView = new TextView(this);
        textView.setTextSize(21);
        textView.setText(message);

        FrameLayout layout = (FrameLayout) findViewById(R.id.container);
        layout.addView(textView);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment{

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private String mForecastStr;

        public DetailFragment(){
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text))
                .setText(mForecastStr);
            }
            return rootView;
        }

        @Override

        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.detailfragment, menu);

            // Get the menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);
            // Get the provider
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            // ShareActionProvider is a provider for a share action.
            // MenuItemCompat - class that helps access features in Menuitem
            // public static ActionProvider getActionProvider(MenuItem item)
            // gets in ActionProvider

            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            // When you want to set the share intent, above is used.
            // ShareActionProvider.setShareIntent(intent shareintent)
            // sets an intent with information about the share action.
            } else {
                Log.d(LOG_TAG, "Share Action Provide is null");
            }
        }

        private Intent createShareForecastIntent(){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            // ACTION_SEND에 의해 이메일도 같이 올라 옴/차별성 공부 필요요
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            // flags control how the system handles an Activity when you try to start it.
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecastStr + FORECAST_SHARE_HASHTAG);
            // data에 #SunshineApp이라는 문구 추가되어서 공유
            // putExtra(String name, CharSequence value) adds extended data to the intent.
            return shareIntent;
        }
    }
}
