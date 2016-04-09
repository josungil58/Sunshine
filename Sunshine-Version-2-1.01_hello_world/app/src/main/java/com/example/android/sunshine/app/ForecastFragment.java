package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

/**
 * Encapsulates fetching the forecast and displaying it as a (@link ListView) layout
 */
public class ForecastFragment extends Fragment {
    //ForecastFragment를 background로 작동시키기 위해 fragment.java file을 작성
    //fragment를 mainactivity.java file에 작성할 수도 있지만 file이 지저분해지고 foreground에서 작동하는 것 방지를 위해 분리

    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //called to do initial creation of a fragment
        //Bundle에 저장된 Instance State를 불러옴
        //onCreate()는 여기서는 public으로 지정해야 main activity에서 사용 가능
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //added in order for this fragment to handle menu events
        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
      }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {//main.xml의 item id
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        //AsyncTask enables proper and easy use of the UI thread.
        //AsyncTask should ideally be used for short operations (a few seconds at the most)

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        //.getSimpleName() returns simple name of the class

        @Override
        protected Void doInBackground(String... params) {
            //invoke on the background thread immediately after onPreExecute() finishes executing

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            //If there's no zip code, there's nothing to look up. Verify size of params.
            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            //urlConnection class 상속
            //null은 연결하지 않겠다는 의미로 추정
            BufferedReader reader = null;
            //data reading using buffer

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            //아직은 연결하지 않겠다는 의미 - null, 장래 활용//초기값 설정

            String format = "json";
            String units ="metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                //날씨 data 7개를 받아서 string형식으로 저장

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        //public static Uri parse(String uriString)
                        // j - Creates a Uri which parses the given encoded URI string
                        //public abstract Uri.builder buildupon()
                        // - Construct a new builder, copying the attributes from this Uri
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        //public Uri.builder appendQueryParameter(String key, String value)
                        // - Encodes the key and value and then appends the parameter to the query string
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY )
                        .build();
                        //public Uri build() - constructs a Uri with the current attributes

                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseUrl.concat(apiKey));

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built Uri " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //this method can be called before connection, options, head, put, delete, trace 등의 request 방법이 있음
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                //inputStream is used to read data from the internet(getInputStream())
                StringBuffer buffer = new StringBuffer();
                //mainly used to interact with legacy APIs that expose it
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                //inputStreamReader가 inputStream으로 읽은 data를 문자로 변환

                String line;

                while ((line = reader.readLine()) != null) {
                    //public String readLind(); returns the next line of text available from this reader
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                    //buffer도 class. public StringBuilder append(String str)
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //length() returns the number of characters in this sequence
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast Jason String: " + forecastJsonStr);

            } catch (IOException e) {
                Log.e("LOG_TAG", "Error ", e);
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
                        Log.e("LOG_TAG", "Error closing stream", e);
                    }
                }
            }
            return null;


        }
    }

}