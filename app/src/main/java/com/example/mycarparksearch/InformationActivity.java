package com.example.mycarparksearch;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
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

public class InformationActivity extends AppCompatActivity {
    private static final int CAR = 0;
    private static final int MOTOR = 1;
    private static final int HEAVY = 2;
    private static final String NA = "N.A.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent intent = getIntent();
        String carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);
        CarparkEntity carpark = null;
        try {
            carpark = new InformationActivity.GetFullInformation().execute(carParkNo).get();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park information!\nTask interrupted.", Toast.LENGTH_SHORT).show();
        }
        if (carpark != null) {
            showFullInformation(carpark);

            ImageButton viewMapButton = findViewById(R.id.viewMapButton);
            CarparkEntity finalCarpark = carpark;
            viewMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
                    intent.putExtra(MapsActivity.CAR_PARK_LAT, finalCarpark.getInformation("xCoord"));
                    intent.putExtra(MapsActivity.CAR_PARK_LON, finalCarpark.getInformation("yCoord"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            ImageButton favoriteButton = findViewById(R.id.favoriteButton);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drawable likeRedDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_red);
                    Drawable likeDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.like);
                    if (favoriteButton.getDrawable().getConstantState().equals(likeRedDrawable.getConstantState())) {
                        favoriteButton.setImageDrawable(likeDrawable);
                    } else {
                        favoriteButton.setImageDrawable(likeRedDrawable);
                        Toast.makeText(getApplicationContext(), "Favorite!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ImageButton commentButton = findViewById(R.id.commentButton);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InformationActivity.this, CommentActivity.class);
                    intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
                    InformationActivity.this.startActivityForResult(intent, 1);
                }
            });
        }
    }

    private CarparkEntity getFullInformation(String carParkNo) {
        CarparkSQLControl con = new CarparkSQLControl("172.21.148.165", "VMadmin", "cz2006ala",
                "localhost", 3306, "cz2006", "cz2006", "cz2006ala");
        CarparkEntity carpark;
        try {
            carpark = con.queryCarparkFullInfo(carParkNo);
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Failed to get car park information!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return carpark;
    }

    private void showFullInformation(CarparkEntity carpark) {
        TextView carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.setText(carpark.getInformation("carParkNo"));

        TextView addressText = findViewById(R.id.address);
        addressText.setText(carpark.getInformation("address"));

        TextView carParkTypeText = findViewById(R.id.carParkType);
        carParkTypeText.append(carpark.getInformation("carParkType"));

        TextView typeOfParkingSystemText = findViewById(R.id.typeOfParkingSystem);
        typeOfParkingSystemText.append(carpark.getInformation("typeOfParkingSystem"));

        TextView shortTermParkingText = findViewById(R.id.shortTermParking);
        shortTermParkingText.append(carpark.getInformation("shortTermParking"));

        TextView freeParking = findViewById(R.id.freeParking);
        freeParking.append(carpark.getInformation("freeParking"));

        TextView nightParking = findViewById(R.id.nightParking);
        nightParking.append(carpark.getInformation("nightParking"));

        TextView carParkDecksText = findViewById(R.id.carParkDecks);
        carParkDecksText.append(carpark.getInformation("carParkDecks"));

        TextView gantryHeightText = findViewById(R.id.gantryHeight);
        gantryHeightText.append(carpark.getInformation("gantryHeight"));

        TextView carParkBasementText = findViewById(R.id.carParkBasement);
        carParkBasementText.append(carpark.getInformation("carParkBasement"));

        showRatesAndLots(carpark);
    }

    private void showRatesAndLots(CarparkEntity carpark) {
        TextView ratesText = findViewById(R.id.rates);
        ratesText.setText(carpark.getInformation("carRates"));

        TextView lotsText = findViewById(R.id.lots);
        String lots = "Available lots: " + carpark.getLotsAvailable("carLotAvail")
                + "/" + carpark.getInformation("carLotNum");
        lotsText.setText(lots);

        TabLayout ratesSwitch = findViewById(R.id.ratesSwitch);
        ratesSwitch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == CAR) {
                    switchRatesAndLots(carpark, ratesText, lotsText, "car");
                } else if (pos == MOTOR) {
                    switchRatesAndLots(carpark, ratesText, lotsText, "motor");
                } else if (pos == HEAVY) {
                    switchRatesAndLots(carpark, ratesText, lotsText, "heavy");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void switchRatesAndLots(CarparkEntity carpark, TextView ratesText, TextView lotsText, String type) {
        String rates = carpark.getInformation(type + "Rates");
        if (rates != null) {
            ratesText.setText(rates);
        } else {
            ratesText.setText(NA);
        }
        String lots = "Available lots: " + carpark.getLotsAvailable(type + "LotAvail")
                + "/" + carpark.getInformation(type + "LotNum");
        lotsText.setText(lots);
    }

    // task that requires Internet access
    private class GetFullInformation extends AsyncTask<String, Void, CarparkEntity> {
        @Override
        protected CarparkEntity doInBackground(String[] carParkNos) {
            return getFullInformation(carParkNos[0]);
        }
    }
}
