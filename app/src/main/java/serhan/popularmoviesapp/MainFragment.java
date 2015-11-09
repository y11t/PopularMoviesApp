package serhan.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private GridView gridView;
    private MyAdapter myAdapter;
    //private final String API_KEY = "c59e90221f3bbae2b5ec10d1d9d433a1";
    private final String LOG_TAG = MainActivity.class.getSimpleName();


    public MainFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        //buraya AsyncTaskin outputu gelicek . cunku oda String[] outputu veriyor.
        //gridView.setAdapter(new MyAdapter(getActivity(), eatFoodyImages));


        myAdapter = new MyAdapter(getActivity(),new ArrayList<String>());
        gridView.setAdapter(myAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String movie = (String) myAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
                Toast.makeText(getActivity(),
                        String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }

    private void updateMovie(){
        FetchMovieTask movieTask = new FetchMovieTask();  //initiate FetchWeatherTask
        //removed the hardcoded weatherTask.execute("94043") here
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_popular));
        movieTask.execute(sort);
        Log.v(LOG_TAG, "params " + sort.toString());
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovie();

    }

    public class FetchMovieTask extends AsyncTask<String,Void,String[]>{



        private String[] getMovieDataFromJson (String movieJsonStr , int numMovies) throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "title";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_POSTER = "poster_path";
            final String OWM_POPULARITY = "popularity";
            final String OWM_RANKING = "vote_average";
            final String OWM_DESCRIPTION = "main";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);
            //TODO: Fetching Data from the constructed URL , fetching movie details
            String[] resultStrs = new String[numMovies];

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popular));

            for(int i = 0; i < movieArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"

                String poster;

                // Get the JSON object representing the day
                JSONObject movieForecast = movieArray.getJSONObject(i);
                poster = movieForecast.getString(OWM_POSTER);
                // description is in a child array called "weather", which is 1 element long.
                //JSONObject movieObject = movieForecast.getJSONObject(0);
                //poster = movieObject.getString(OWM_POSTER);
                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.



                resultStrs[i] = "http://image.tmdb.org/t/p/w185/"+poster;
                ;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        protected String[] doInBackground(String... params){

            if (params.length == 0){
                return null;            //if there's nothing to return.
            }

            HttpURLConnection urlConnection = null; //An URLConnection for HTTP (RFC 2616) used to send
            // and receive data over the web. Data may be of any type and length.
            // This class may be used to send and receive streaming data whose length is not known in advance.
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;


            String sort = "popular";

            int numMovies = 20;

            try {

                // Possible parameters are avaiable at OWM's forecast API page, at

                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM,params[0])       //WE READ THE PASSED PARAM HERE.
                        //.appendQueryParameter(PAGE_PARAM, Integer.toString(numMovies))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIES_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG,"Forecast JSON String :" + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr, numMovies);    //Returning this String.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }

        protected void onPostExecute (String[] result){ //TODO:Handle adding elements to the MyAdapter

            if(result!=null){
                myAdapter.clear();
                           //result 0 a esit degilse , mock datayi siliyoruz.
                //yerine dayForecastStr'yi koyuyoruz .
                for (String movieStr : result) {
                    myAdapter.add(movieStr);
                }

            }

        }



    }


}
