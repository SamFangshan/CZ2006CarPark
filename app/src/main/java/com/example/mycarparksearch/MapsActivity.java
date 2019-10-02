package com.example.mycarparksearch;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText et;
    FloatingActionButton fab;
    ImageButton clbutton;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Sets button colour to null
        fab = findViewById(R.id.floatingActionButtonMapActivityOptions);
        fab.setBackgroundTintList(null);
        et = findViewById(R.id.locationEditText);

        clbutton = findViewById(R.id.clbutton);
        clbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLastLocation();
                showLastLocation();
            }
        });

        fetchLastLocation();
    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void fetchLastLocation() {
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
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    private void showLastLocation() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.clmarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.addMarker((markerOptions));
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        showLastLocation();

        if (!isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Internet not connected.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<CarparkEntity> carparkList = null;
        try {
            carparkList = (ArrayList<CarparkEntity>)(new GetAllCarparks().execute().get());
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park locations!", Toast.LENGTH_SHORT).show();
        }
        if (carparkList != null) {
            showAllCarparks(carparkList);
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

    @Override
    public void onClick(View v) {
        if (v == et) {
            et.setText("");
            Intent intent = new Intent(this, SearchForAddressActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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
                MapsActivity.this.startActivity(intent);
            }
        });
        return true;
    }

    // task that requires Internet access
    private class GetAllCarparks extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return getAllCarparks();
        }
    }
}

















