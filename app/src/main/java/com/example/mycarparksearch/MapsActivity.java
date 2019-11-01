package com.example.mycarparksearch;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.mycarparksearch.R.id.save_carpark;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    SupportMapFragment mapFrag;
    Location currentLocation;
    LocationRequest locationRQ;
    Marker currentLocationMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText locationEditText;
    ImageButton menuButton;
    ImageButton clbutton;
    Marker destinationMarker;
    private Polyline mPolyline;
    boolean firstTime = false; // First-time makes sure that the app zooms in on your current location only once
    SQLiteControl db;
    ArrayList<String>listItem;
    ArrayAdapter adapter;
    ListView favoritelist;
    ListView savedCarparkList;
    PlacesClient placesClient;
    private boolean shouldExecuteOnresume = false;
    private int countOnResume = 0;

    private static final int REQUEST_CODE = 101;
    public static final String CAR_PARK_NO = "com.example.mycarparksearch.CAR_PARK_NO";
    public static final String CAR_PARK_LAT = "com.example.mycarparksearch.CAR_PARK_LAT";
    public static final String CAR_PARK_LON = "com.example.mycarparksearch.CAR_PARK_LON";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        db = new SQLiteControl(this);
        listItem = new ArrayList<>();

        favoritelist = (ListView) findViewById(R.id.favorite_list);
        favoritelist.setVisibility(View.GONE);

        savedCarparkList = (ListView) findViewById(R.id.savedCarpark_List);
        savedCarparkList.setVisibility(View.GONE);

        View headerView = getLayoutInflater().inflate(R.layout.listview_header, null);
        favoritelist.addHeaderView(headerView);

        View carparkheaderView = getLayoutInflater().inflate(R.layout.savedcarpark_header, null);
        savedCarparkList.addHeaderView(carparkheaderView);


        //initialise search places fragment
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),"AIzaSyDNGI_gB0BZnUiD2ZslUGABrz_eLhBSwzg");
        }

        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment =
                (com.google.android.libraries.places.widget.AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();

                Log.i("Place", "onPlaceSelected:"+latLng.latitude+"\n"+latLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        // Sets button colour to null
        menuButton = findViewById(R.id.menuButton);
        locationEditText = findViewById(R.id.locationEditText);

        clbutton = findViewById(R.id.clButton);
        clbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLastLocation();
                updateLastLocation();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MapsActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.save_favorites:
                            favoritelist.setVisibility(View.VISIBLE);
                            viewFavorite();
                            return true;
                            case save_carpark:
                                savedCarparkList.setVisibility(View.VISIBLE);
                            viewSavedCarpark();
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        locationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return true;
            }
        });

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFrag.getMapAsync(this);
    }

    // LocationCallback is triggered when a LocationRequest gets a new coordinate to use
    LocationCallback locationCB = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i("Callback","Callback triggered!");
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                currentLocation = location;

                if (currentLocationMarker != null) currentLocationMarker.remove();

                //Place current location marker
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.clmarker));
                currentLocationMarker = mMap.addMarker((markerOptions));
                currentLocationMarker.setPosition(latLng);
                if (!firstTime) {
                    firstTime = true;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnresume) {
            viewFavorite();
            viewSavedCarpark();
            adapter.notifyDataSetChanged();
        } else {
            if (countOnResume >= 1) {
                shouldExecuteOnresume = true;
            } else {
                countOnResume++;
            }
        }
    }

    private void viewFavorite() {

        Cursor cursor = db.viewData();


        listItem.clear();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){

                String carParkNo = cursor.getString(0);
                listItem.add(carParkNo);

            }

            adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listItem);
            favoritelist.setAdapter(adapter);

            favoritelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String carParkNo = (String) parent.getAdapter().getItem(position);
                    Intent intent = new Intent(MapsActivity.this, InformationActivity.class);
                    intent.putExtra(CAR_PARK_NO, carParkNo);
                    startActivityForResult(intent, 1);
                }
            });
        }

    }
    @Override
    public void onBackPressed() {
        if(favoritelist.getVisibility()==View.VISIBLE){

            favoritelist.setVisibility(View.INVISIBLE);
            return;
        }
        else if (savedCarparkList.getVisibility()==View.VISIBLE){

            savedCarparkList.setVisibility(View.INVISIBLE);
            return;

        }
        super.onBackPressed();

        adapter.notifyDataSetChanged();
    }

    public void viewSavedCarpark(){
        Cursor cursor = db.viewSavedCarpark();

        listItem.clear();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }else{
            ArrayList<String> carParkNos = new ArrayList<String>();
            while (cursor.moveToNext()){

                String name = cursor.getString(0);
                carParkNos.add(cursor.getString(1));
                listItem.add(name);

            }

            adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listItem);
            savedCarparkList.setAdapter(adapter);

            savedCarparkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String carParkNo = carParkNos.get(position - 1);
                    Intent intent = new Intent(MapsActivity.this, SaveCarparkActivity.class);
                    intent.putExtra(CAR_PARK_NO, carParkNo);
                    startActivity(intent);
                }
            });
        }

    }

    /*
    To show all car parks on Google Maps
     */
    private void showAllCarparks(ArrayList<CarparkEntity> carparkList) {
        for (CarparkEntity e : carparkList) {
            double lat = Double.parseDouble(e.getInformation("xCoord"));
            double lon = Double.parseDouble(e.getInformation("yCoord"));
            String cpn = e.getInformation("carParkNo");
            LatLng latLng = new LatLng(lat, lon);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(cpn).icon(BitmapDescriptorFactory.fromResource(R.drawable.carpark));
            mMap.addMarker((markerOptions));
        }
    }

    /*
    To check whether the device has Internet connection
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /*
    To fetch the location of this device
     */
    private void fetchLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();

            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        location.setLatitude(1.276307);
                        location.setLongitude(103.840811);
                        currentLocation = location;
                        Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                                +","+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.google_map);
                    }
                }
            });
        } catch (Exception e) {
            Log.i("fetchLastLocation","Error in fetchLastLocation");
        }
    }

    /*
    To update the location of this device
     */
    private void updateLastLocation() {
        try {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            currentLocationMarker.setPosition(latLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } catch (Exception e) {
            Log.i("updateLastLocation", "Error in updateLastLocation");
        }
    }

    /*
    To show the location of this device on Google Maps
     */
    private void showLastLocation() {
        try {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.clmarker));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            currentLocationMarker = mMap.addMarker((markerOptions));
        } catch (Exception e) {
            Log.i("showLastLocation", "Error in showLastLocation");
        }
    }

    /*
    To retrieve all CarParkNo and car park coordinates for displaying car park locations on Google Maps
    Return an ArrayList of CarparkEntity
     */
    private ArrayList<CarparkEntity> getAllCarparks() {
        CarparkSQLControl con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        ArrayList<CarparkEntity> carparkList = null;
        try {
            carparkList = con.getAllCarparkLocations();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park locations!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return carparkList;
    }

    /*
    To zoom and move to the location of a specific car park
     */
    private void showCarPark(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if(favoritelist.getVisibility()==View.VISIBLE){
                    favoritelist.setVisibility(View.INVISIBLE);
                }
                String carParkNo = intent.getStringExtra(CAR_PARK_NO);
                String lat = intent.getStringExtra(CAR_PARK_LAT);
                String lon = intent.getStringExtra(CAR_PARK_LON);
                Toast.makeText(getApplicationContext(), carParkNo, Toast.LENGTH_SHORT).show();
                showCarPark(Double.parseDouble(lat), Double.parseDouble(lon));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Test", "Map is ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);

        locationRQ = new LocationRequest();
        locationRQ.setInterval(10000);
        locationRQ.setFastestInterval(3000);
        locationRQ.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (!isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Internet not connected.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<CarparkEntity> carparkList = null;
        try {
            carparkList = (ArrayList<CarparkEntity>)(new GetAllCarparks().execute().get());
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park locations!\nTask interrupted.", Toast.LENGTH_SHORT).show();
        }
        if (carparkList != null) {
            showAllCarparks(carparkList);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.requestLocationUpdates(locationRQ, locationCB, Looper.getMainLooper());
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                fetchLastLocation();
                updateLastLocation();
                showLastLocation();
            }
            else {
                fusedLocationProviderClient.requestLocationUpdates(locationRQ, locationCB, Looper.getMainLooper());
                //mMap.setMyLocationEnabled(true);
                fetchLastLocation();
                updateLastLocation();
                showLastLocation();
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
                ImageButton infobutton = findViewById(R.id.infobutton);
                TextView infotext = findViewById(R.id.infotext);
                bottomAppBar.setVisibility(View.GONE);
                infobutton.setVisibility(View.GONE);
                infotext.setVisibility(View.GONE);
                if (destinationMarker == null) {
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    destinationMarker = mMap.addMarker(options);
                }
                else {
                    destinationMarker.setPosition(latLng);
                }
                // Getting URL to the Google Directions API
                drawRoute(latLng);
            }
        });
    }

    private void drawRoute(LatLng latLng){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

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

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    /*
    When a car park marker on the map is clicked, the user can choose to view the detailed information
    of a car park.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        String carParkNo = marker.getTitle();
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        ImageButton infobutton = findViewById(R.id.infobutton);
        TextView infotext = findViewById(R.id.infotext);
        infobutton.setBackgroundTintList(null);
        bottomAppBar.setVisibility(View.VISIBLE);
        infobutton.setVisibility(View.VISIBLE);
        infotext.setVisibility(View.VISIBLE);
        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomAppBar.setVisibility(View.GONE);
                infobutton.setVisibility(View.GONE);
                infotext.setVisibility(View.GONE);
                Intent intent = new Intent(MapsActivity.this, InformationActivity.class);
                intent.putExtra(CAR_PARK_NO, carParkNo);
                MapsActivity.this.startActivityForResult(intent, 1);
            }
        });
        return true;
    }

    /*
    To retrieve all CarParkNo and car park coordinates for displaying car park locations on Google Maps
    Return an ArrayList of CarparkEntity
    This task requires Internet access, so AsyncTask is used to create a different thread
     */
    private class GetAllCarparks extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return getAllCarparks();
        }
    }

}
