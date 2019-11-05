package com.example.mycarparksearch;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/*
This is the activity that will display the directions obtained by the Google API in text
 */
public class ShowDirectionsActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;
    private TextView header;
    private LinearLayout insideScrollView;
    private Location currentLocation;

    // On create function. This code runs at start and gathers the extra data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_directions);

        context = getApplicationContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        header = findViewById(R.id.toXTextView);
        header.setText("To " + getIntent().getStringExtra(MapsActivity.CAR_PARK_NO));
        insideScrollView = findViewById(R.id.insideScrollView); // insideScrollView is a view inside the scrollView that is meant to contain all the instructions
        insideScrollView.removeAllViews();

        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        // On success listener waits for the task to succeed, then runs the code inside it
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    try {
                        double lat = Double.parseDouble(getIntent().getStringExtra(MapsActivity.CAR_PARK_LAT));
                        double lon = Double.parseDouble(getIntent().getStringExtra(MapsActivity.CAR_PARK_LON));
                        String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(lat,lon));
                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.locationNotFoundText,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<String>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<String> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<String> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parseDirections(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<String> result) {
            for (int i = 1; i <= result.size(); i++) {
                String r = i + ". " + result.get(i-1);

                TextView myView = new TextView(context);
                myView.setText(r);
                insideScrollView.addView(myView, LinearLayout.LayoutParams.MATCH_PARENT);
            }

        }
    }

    // A piece of code that forms the Google API directions URL. This code needs to be in the Activity class as it relies on a getString function
    // getString functions require the parent class to extend an Activity interface
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
}
