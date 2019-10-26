package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SaveCarparkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_carpark);

        Intent intent = getIntent();
        String carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);

        TextView carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.append(carParkNo);
    }
}
