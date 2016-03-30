package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            //urlConnection class 상속
            //null은 연결하지 않겠다는 의미로 추정
            BufferedReader reader = null;
            //data reading using buffer

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            //아직은 연결하지 않겠다는 의미 - null, 장래 활용

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                //날씨 data 7개를 받아서 string형식으로 저장
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;

                }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();

                }
                if (reader != null) {
                    try {
                        reader.close();

                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);

                    }

                }

            }

            return rootView;
        }


    }
}
