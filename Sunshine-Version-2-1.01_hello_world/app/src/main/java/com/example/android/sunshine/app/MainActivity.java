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
        Log.v(LOG_TAG, "in onCreate");
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
            // Intent(Context packageContext, Class<?> cls)
            // Context can be invoked using
            // .getApplicationContext(), .getContext(), getBaseContext() or this(in the Activity Class)
            // 'this' refers to the context of the current context
        }

        if (id == R.id.action_map) {
            openPreferenceLocaitonInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void openPreferenceLocaitonInMap() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        // public interface SharedPreferences
        // interface for accessing and modifying preference data returned by getSharedPreferences(String, int)
        // public static SharedPreferences getDefaultSharedPreferences(Context context)
        // 환경설정(settings)을 하기 위한 preference에서 저장된 data값을 활용
        //
        String location = sharedPrefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        // Using the URI scheme for showing a location found on a map. This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/component/intents.common.html#maps
        // public abstract String getString(String key, String defValue)

        Uri geolocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        // Maps intent에 전달되는 모든 문자열은 Uri로 encoding되어야 함.
        // android.net.Uri.parse() method를 사용하여서 문자열을 encoding할 수 있음
        // geo: latituce, longitude ?z=zoom; intent를 사용하여서 지도의 중심점을 설정
        // z는 0(세계지도)부터 21(개별건물)까지 수준을 정할 수 있음.
        // z값에 변화를 주어봤지만 별 차이가 없었음

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geolocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            //  app이 intent를 수신할 수 있는지 확인, 결과가 null이 아니라는 소리는 intent를 처리할 수 있는
            //  앱이 적어도 하나는 있다는 말, null은 해당 intent를 사용해서는 안됨.에러 발생
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ". no receiving apps installed!");
        }
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "in onResume");
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "in onPause");
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "in onStop");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "in onDestroy");
        super.onDestroy();
        // The activity is about to be destroyed.
    }

}
