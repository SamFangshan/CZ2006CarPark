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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    boolean firstTime = false; // First-time makes sure that the app zooms in on your current location only once

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
        locationRQ.setSmallestDisplacement((float)10.0);
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
                showLastLocation();
            }
            else {
                fusedLocationProviderClient.requestLocationUpdates(locationRQ, locationCB, Looper.getMainLooper());
                mMap.setMyLocationEnabled(true);
            }
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                bottomAppBar.setVisibility(View.GONE);
                infobutton.setVisibility(View.GONE);
                infotext.setVisibility(View.GONE);
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