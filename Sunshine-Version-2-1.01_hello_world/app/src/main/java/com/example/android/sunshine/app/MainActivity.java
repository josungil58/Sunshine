package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    /**
     * onCreate(Bundle savedInstanceState), getSupportFragmentManager
     * MainActivity가 class로 지정되어 있음
     */

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //called to do initial creation of a fragment
        //Bundle에 저장된 Instance State를 불러옴
        //onCreate()는 protected method
        super.onCreate(savedInstanceState);
        //Activity class의 onCreate()를 상속하였기 때문에 super.필요
        setContentView(R.layout.activity_main);
        //override된 것임에 유의
        //content의 view는 layout.activity_main.xml 화일로 지정
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // beginTransaction()은 activity state저장전에 사용
                    .add(R.id.container, new ForecastFragment())
                    // FragmentTransaction class 참조
                    // Fragment class file을 따로 만들든 nested로 정의를 하든 activity와 Fragment를 연결하는 방식은 같음
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        //in order to display menu, 'true' must be returned,
        //MenuInflator inflator = getMenuInfator();로 써도 마찬가지 결과
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//main.xml의 item
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_map){
            openPreferenceLocaitonInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

        private void openPreferenceLocaitonInMap(){
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String location = sharedPrefs.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            // Using the URI scheme for showing a location found on a map. This super-handy
            // intent can is detailed in the "Common Intents" page of Android's developer site:
            // http://developer.android.com/guide/component/intents.common.html#maps

            Uri geolocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geolocation);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "Couldn't call " + location + ". no receiving apps installed!");
            }
        }
}
