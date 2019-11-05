package com.example.mycarparksearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Interface class to display information regarding a car park.
 */
public class InformationActivity extends AppCompatActivity {
    private static final int CAR = 0;
    private static final int MOTOR = 1;
    private static final int HEAVY = 2;
    private static final String NA = "N.A.";
    private Context context;
    private String carParkNo;
    private CarparkEntity carpark;
    private ImageButton viewMapButton;
    private SQLiteControl sqLiteControl;
    private ImageButton favoriteButton;
    private Drawable likeRedDrawable;
    private Drawable likeDrawable;
    private ImageButton commentButton;
    private ImageButton directionsButton;
    private ImageButton saveButton;
    private TextView carParkNoText;
    private TextView addressText;
    private TextView carParkTypeText;
    private TextView typeOfParkingSystemText;
    private TextView shortTermParkingText;
    private TextView freeParking;
    private TextView nightParking;
    private TextView carParkDecksText;
    private TextView gantryHeightText;
    private TextView carParkBasementText;
    private TextView ratesText;
    private TextView lotsText;
    private TabLayout ratesSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        context = getApplicationContext();

        Intent intent = getIntent();
        carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);
        carpark = null;
        try {
            carpark = new InformationActivity.GetFullInformation().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park information!\nTask interrupted.", Toast.LENGTH_SHORT).show();
        }
        if (carpark != null) {
            setUpUIElements();
            sqLiteControl.close();
        }
    }

    private void setUpUIElements() {
        showFullInformation();

        //CarparkEntity finalCarpark = carpark;
        viewMapButton = findViewById(R.id.viewMapButton);
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performViewMapButton();
            }
        });

        sqLiteControl = new SQLiteControl(getApplicationContext());
        favoriteButton = findViewById(R.id.favoriteButton);
        likeRedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_red);
        likeDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.like);
        if (sqLiteControl.getFavorite(carParkNo)) {
            favoriteButton.setImageDrawable(likeRedDrawable);
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFavoriteButton();
            }
        });

        commentButton = findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performCommentButton();
            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSaveButton();
            }
        });

        directionsButton = findViewById(R.id.directionsButton);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { performDirectionsButton(); }
        });

    }

    private void performViewMapButton() {
        Intent intent = new Intent();
        intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        intent.putExtra(MapsActivity.CAR_PARK_LAT, carpark.getInformation(context.getString(R.string.xCoord)));
        intent.putExtra(MapsActivity.CAR_PARK_LON, carpark.getInformation(context.getString(R.string.yCoord)));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void performFavoriteButton() {
        if (favoriteButton.getDrawable().getConstantState().equals(likeRedDrawable.getConstantState())) {
            sqLiteControl.deleteFavorite(carParkNo);
            favoriteButton.setImageDrawable(likeDrawable);
        } else {
            favoriteButton.setImageDrawable(likeRedDrawable);
            sqLiteControl.updateFavorite(carParkNo, true);
            Toast.makeText(getApplicationContext(), "Favorite!", Toast.LENGTH_SHORT).show();
        }
    }

    private void performCommentButton() {
        Intent intent = new Intent(InformationActivity.this, CommentActivity.class);
        intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        InformationActivity.this.startActivityForResult(intent, 1);
    }

    private void performSaveButton() {
        //temp
        Intent intent = new Intent(InformationActivity.this, SaveCarparkActivity.class);
        intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        InformationActivity.this.startActivityForResult(intent, 1);
    }

    private void performDirectionsButton() {
        Intent intent = new Intent(InformationActivity.this, ShowDirectionsActivity.class);
        intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        intent.putExtra(MapsActivity.CAR_PARK_LAT, carpark.getInformation(context.getString(R.string.xCoord)));
        intent.putExtra(MapsActivity.CAR_PARK_LON, carpark.getInformation(context.getString(R.string.yCoord)));
        InformationActivity.this.startActivityForResult(intent, 1);
    }

    /*
    To retrieve detailed car park information of a car park with a specific carParkNo from MySQL database
    Return a CarparkEntity
     */
    private CarparkEntity getFullInformation() {
        CarparkSQLControl con = new CarparkSQLControl(context.getString(R.string.sshHost),
                context.getString(R.string.sshUsername), context.getString(R.string.sshPassword),
                context.getString(R.string.dbHost), Integer.parseInt(context.getString(R.string.dbPort)),
                context.getString(R.string.dbName), context.getString(R.string.dbUsername),
                context.getString(R.string.dbPassword), context);
        CarparkEntity carpark;
        try {
            carpark = con.queryCarparkFullInfo(carParkNo);
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park information!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return carpark;
    }

    /*
    To display detailed car park information of a car park with a specific carParkNo on the screen
     */
    private void showFullInformation() {
        carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.setText(carpark.getInformation(context.getString(R.string.carParkNo)));

        addressText = findViewById(R.id.address);
        addressText.setText(carpark.getInformation(context.getString(R.string.address)));

        carParkTypeText = findViewById(R.id.carParkType);
        carParkTypeText.append(carpark.getInformation(context.getString(R.string.carParkType)));

        typeOfParkingSystemText = findViewById(R.id.typeOfParkingSystem);
        typeOfParkingSystemText.append(carpark.getInformation(context.getString(R.string.typeOfParkingSystem)));

        shortTermParkingText = findViewById(R.id.shortTermParking);
        shortTermParkingText.append(carpark.getInformation(context.getString(R.string.shortTermParking)));

        freeParking = findViewById(R.id.freeParking);
        freeParking.append(carpark.getInformation(context.getString(R.string.freeParking)));

        nightParking = findViewById(R.id.nightParking);
        nightParking.append(carpark.getInformation(context.getString(R.string.nightParking)));

        carParkDecksText = findViewById(R.id.carParkDecks);
        carParkDecksText.append(carpark.getInformation(context.getString(R.string.carParkDecks)));

        gantryHeightText = findViewById(R.id.gantryHeight);
        gantryHeightText.append(carpark.getInformation(context.getString(R.string.gantryHeight)));

        carParkBasementText = findViewById(R.id.carParkBasement);
        carParkBasementText.append(carpark.getInformation(context.getString(R.string.carParkBasement)));

        showRatesAndLots();
    }

    /*
    To show the rates and lots information of a CarparkEntity
     */
    private void showRatesAndLots() {
        ratesText = findViewById(R.id.rates);
        ratesText.setText(carpark.getInformation(context.getString(R.string.carRates)));

        lotsText = findViewById(R.id.lots);
        String lots = "Available lots: " + carpark.getLotsAvailable(context.getString(R.string.carLotAvail))
                + "/" + carpark.getInformation(context.getString(R.string.carLotNum));
        lotsText.setText(lots);

        ratesSwitch = findViewById(R.id.ratesSwitch);
        ratesSwitch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void switchTab(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        if (pos == CAR) {
            switchRatesAndLots(context.getString(R.string.car));
        } else if (pos == MOTOR) {
            switchRatesAndLots(context.getString(R.string.motor));
        } else if (pos == HEAVY) {
            switchRatesAndLots(context.getString(R.string.heavy));
        }
    }

    /*
    To switch between the rates and lots information of a CarparkEntity based on lot type
     */
    private void switchRatesAndLots(String type) {
        String rates = carpark.getInformation(type + context.getString(R.string.rateSuffix));
        if (rates != null) {
            ratesText.setText(rates);
        } else {
            ratesText.setText(NA);
        }
        String lots = "Available lots: " + carpark.getLotsAvailable(type + context.getString(R.string.lotAvailSuffix))
                + "/" + carpark.getInformation(type + context.getString(R.string.lotNumSuffix));
        lotsText.setText(lots);
    }

    /*
    To retrieve detailed car park information of a car park with a specific carParkNo from MySQL database
    Return a CarparkEntity
    This task requires Internet access, so AsyncTask is used to create a different thread
     */
    private class GetFullInformation extends AsyncTask<Void, Void, CarparkEntity> {
        @Override
        protected CarparkEntity doInBackground(Void... voids) {
            return getFullInformation();
        }
    }
}
