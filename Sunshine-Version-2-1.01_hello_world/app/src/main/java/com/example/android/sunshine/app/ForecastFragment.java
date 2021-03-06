package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
        // callback method
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

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //onCreateView()는 fragment가 자신의 user interface를 instantiate 하기 위해 call

        //create some dummy data for the ListView.
/*
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
*/
        //ArrayList weekForecast = new ArrayList<>();

        //create an ArrayAdapter
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,//layout .xml file's name
                        R.id.list_item_forecast_textview,//list_item_forecat.xml file 내 textview's id
                        new ArrayList<String>());
        // public ArrayAdapter(Context context, int resource, int textViewResourceID, List<T> objects) 형태의 constructor
        // resource - ID for a layout file
        // 여기를 인터넷으로 받은 data로 바로 집어넣을 수 있어야 한다.

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_forecast);//fragment_main.xml listview id
        listView.setAdapter(mForecastAdapter);//sets the data behind this ListView
        // 이 파일 맨 끝에 있는 onPostExecute method에서 mForecastAdapter의 data가 변화됨

        /* OnItemClickListner class의 객체화하고 listView
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forcast =mForecastAdapter.getItem(position);
                Toast.makeText(getActivity(), forcast, Toast.LENGTH_SHORT).show();
            }
        };
        listView.setOnItemClickListener(clickListener);
        이게 아래와 같은 결과임*/


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        // item을 클릭했을 때 view를 정의하는 class를 정하고 객체화

          @Override
          // OnItemClickListner class의 public method인 onClickItem을 재정의]['ㅔ;

          public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
           // adapterView의 item이 click 되었을 때 가동
            String forecast = mForecastAdapter.getItem(position);
            // public abstract Object getItem(int position)
            // gets the data item associated with the specified position in the data set
            // position - the position of the view in the adapter
            // id - the row id of the item that was clicked
            // Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
            // chain 형식으로 toast 정의 .makeText(context, text, duration)
            // getApplicationContext(), getContext() 등은 에러 발생
            // getActivity()
            // Return the Activity this fragment is currently associated with.

              Intent intent = new Intent(getActivity(), DetailActivity.class)
                      // public final Activity getActivity() - returns the Activity this fragment is
                      // currently associated with
                      // Intent(Context packageContext, class<?> cls)
                      .putExtra(Intent.EXTRA_TEXT, forecast);
              // public Intent putExtra(String name, CharaSequence Value)
              // add extended data to the intent
              startActivity(intent);
          }
            });

        return rootView;
    }
    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // PreferenceManager class is used to help create Preference hierarchy
        // public staticPreferences instance that points to the default file that is used by
        // the preference framework in the given context
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        // SharedPreference.getString(String key, String defValue)
        // retrieves a String value from the preferences
        // Context.getString(int resID) returns a localized string from the application's package's default string table
        weatherTask.execute(location);
        // 서울 강서구 구 우편번호 입력 - 구글 서울날씨와 결과가 많이 다름
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        // android.os.AsyncTask<Params, Progress, Results>
        // Params; background 작업시 필요한 data의 type 지정
        // Progress; 작업중 진행상황을 표시하는 data의 type
        // Results; 작업완료후 return할 data의 type

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        //.getSimpleName() returns simple name of the class

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            //EEE는 요일(day of week), MMM은 (Jan)
            //SimpleDateFormat class의 객체화
            return shortenedDateFormat.format(time);
            // public StringBudffer format(Date date, StringBuffer buffer, FieldPosition fieldPos)
            // formats the specified date as a string using the pattern of this date format
            // and appends the string to the string buffer
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low, String unitType) {
            // For presentation, assume the user doesn't care about tenths of a degree.

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }else  if (!unitType.equals(getString(R.string.pref_units_metric))){
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            //Math.round(num) - 소수점 첫째 자리에서 반올림한 정수값은 반환하는 메서드

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            // forecastJsonStr data를 JSON Object화 하는 작업
            // internet을 통해 받은 Json objectdls forecastJsonStr data 모두를 instantiate
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            //public JSONArray getJSONArray(String name)
            //-returns the value mapped by name if it exists and is a JSONArray, ot throws otherwise
            // Json Object에서 list란 array값으로 JSON Array로 변환.

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.
            // UTC(협정 세계 표준시, 프랑스어 Temps Universel Coordonné,
            // 영어: Coordinated Universal Time)는 1970년 1월 1일 자정부터 지정된 날짜 사이의 시간을
            // 밀리초로 반환

            Time dayTime = new Time();
            // Time() class의 객체화

            dayTime.setToNow();
            // public void setTime(long time) - sets the time for this time to the supplied milliseconds value
            // public void setToNow() - sets the time of the given Time object to the current time

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            // public static int getJulianDay(long millis, long gmtoff)
            // - computes the Julian day number for a point in time in a particular timezone.
            // public long gmtoff - dffset in seconds from UTC including any DST offset

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            // String Array resultStrs를 instantiate

            // Data is fetched in Celcius by default.
            // If user prefers to see in Farenheit, convert the values here.
            // We do this rather than fetching in Farenheit so that the user can
            // change this option without us having to re-fetch the data once
            // we start storing the values in a database

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
            // SettingsActivity가 preferenceActivity로 sharedPreferences에 저장한 data 활용


            for (int i = 0; i < weatherArray.length(); i++) {
                // JSONArray.length() returns the number of values in this array
                // For now, using the format "Day, description, hi/low"
                // weatherArray는 list안의 JSONArray data
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);
                // numDays 만큼의 날짜뵬 data
                // JSONArray에서 각 JSON Object(key/value) 추출

                Log.v(LOG_TAG, "dayForecast entry: " + dayForecast);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                // weather data 중에 0번째 data를 받아서 처리

                Log.v(LOG_TAG, "weatherObject entry: " + weatherObject);

                description = weatherObject.getString(OWM_DESCRIPTION);
                //DESCRIPTION = main

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {

                // for(String obj:array) improved for statement for array
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            //invoke on the background thread immediately after onPreExecute() finishes executing
            // .execute(params)의 data를 그 type대로 받음; location, zip code
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.

            //If there's no zip code, there's nothing to look up. Verify size of params.
            if (params.length == 0) {
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
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?
                // q=94043&mode=json&units=metric&cnt=7";
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
                        // doInBackground(params[])
                        //public Uri.builder appendQueryParameter(String key, String value)
                        // - Encodes the key and value and then appends the parameter to the query string
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
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
                    //왜 data의 줄이 바뀌지 않을까?- data를 한번에 다 받는 거니까 줄이 안 바뀌는 게 맞지 않을까?

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //length() returns the number of characters in this sequence
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast String: " + forecastJsonStr);

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

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
                // doInBackground에서 나오는 결과물이 결국에는 getWeatherDataFromJson()의 결과값이고
                // 이 값들이 onPostExecute를 통해 UI thread로 표출되는 것으로 보임
                // 전체 data 중에서 필요한 것만 골라서 return하라는 의미

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
            // 현재는 getWeatherDataFromJson()도 background에 포함 진행, 직접 화면에 표출되지 않음
        }

            @Override
            protected void onPostExecute(String[] result){
                if (result != null) {
                    mForecastAdapter.clear(); // 기존의 data를 모두 지우고
                    //for (String dayForecastStr : result){ // doInBackground로 받은 data를 새로 입력하라는 뜻.
                    //    mForecastAdapter.add(dayForecastStr);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mForecastAdapter.addAll(result);
                    }
                }
                 // AsyncTask must be subclassed to be used. The subclass will override at least one method(doInBackground)
                 // and most often will override a second one (onPostExecute(result))
                 // runs on UI thread after doInBackground, specified result is the value returned by doInBackground().
                 // New data is back from the server. Hooray!
                }
            }
        }

/**
* Warning:(95, 51) Explicit type argument String can be replaced with <>
Warning:(99, 34) Explicit type argument String can be replaced with <>
Warning:(130, 52) To get local formatting use `getDateInstance()`, `getDateTimeInstance()`, or `getTimeInstance()`, or use `new SimpleDateFormat(String template, Locale locale)` with for example `Locale.US` for ASCII dates.
Warning:(148, 20) Local variable 'highLowStr' is redundant
Warning:(321, 30) 'StringBuffer buffer' may be declared as 'StringBuilder'
Warning:(337, 28) String concatenation as argument to 'StringBuffer.append()' call
**/