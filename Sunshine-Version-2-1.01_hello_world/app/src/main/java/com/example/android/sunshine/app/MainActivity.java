package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

/**
 * onCreate(Bundle savedInstanceState), getSupportFragmentManager
 * MainActivity가 class로 지정되어 있음
 */

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
                    //beginTransaction()은 activity state저장전에 사용
                    .add(R.id.container, new PlaceholderFragment())
                    //FragmentTransaction class 참조
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
        if (id == R.id.action_settings) {//main.xml의 item id
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {//PlaceholderFragment도 fragment

        ArrayAdapter<String> mForecastAdapter;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //onCreateView()는 fragment가 자신의 user interface를 instantiate 하기 위해 call

            //create some dummy data for the ListView.
            String[] data = {
                    "Today - Sunny - 88/63",
                    "Tommorrow - Foggy - 70/40",
                    "Weds - Cloudy - 72/63",
                    "Thurs - Asteroids - 75/65",
                    "Fri - Heavy Rain - 65/56",
                    "Sat - Help Trapped in Weatherstation - 60/51",
                    "Sun - Sunny - 80/68"
            };
            List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

            //create an ArrayAdapter
            mForecastAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_forecast,//layout .xml file's name
                            R.id.list_item_forecast_textview,//.xml file 내 textview's id
                            weekForecast
                    );
            //public ArrayAdapter(Context context, int resource, int textViewResourceID, T[] objects) 형태의 constructor
            //resource - ID for a layout file

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView) rootView.findViewById(
                    R.id.listview_forecast);//fragment_main.xml listview id
            listView.setAdapter(mForecastAdapter);//sets the data behind this ListView

            return rootView;
        }


    }
}
