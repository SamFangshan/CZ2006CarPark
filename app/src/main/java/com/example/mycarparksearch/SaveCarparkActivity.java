package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class SaveCarparkActivity extends AppCompatActivity {
    private static final int TIME = 0;
    private static final int MINUTE = 1;
    private TimePickerDialog.OnTimeSetListener time_listener;
    private TimePickerDialog.OnTimeSetListener minute_listener;
    private static final int MON = 0;
    private static final int TUE = 1;
    private static final int WED = 2;
    private static final int THU = 3;
    private static final int FRI = 4;
    private static final int SAT = 5;
    private static final int SUN = 6;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_carpark);

        SQLiteControl sqLiteControl = new SQLiteControl(getApplicationContext());
        Intent intent = getIntent();
        String carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);

        TextView carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.append(carParkNo);

        TextView nameText = findViewById(R.id.nameText);
        TextView timeText = findViewById(R.id.timeText);
        ChipGroup days = findViewById(R.id.daysChips);
        TextView notifyText = findViewById(R.id.time2);
        ImageButton save = findViewById(R.id.saveButton);

        ArrayList<String> savedCarparkHistory = new ArrayList<String>();
        savedCarparkHistory = sqLiteControl.getSavedCarpark(carParkNo);
        if (savedCarparkHistory != null) {
            nameText.setText(savedCarparkHistory.get(0));
            timeText.setText(savedCarparkHistory.get(1));
            StringTokenizer st = new StringTokenizer(savedCarparkHistory.get(2));
            while(st.hasMoreTokens()){
                int daysIndex = Integer.parseInt(st.nextToken());
                Chip chip = (Chip) days.getChildAt(daysIndex);
                chip.setChecked(true);
            }
            notifyText.setText(savedCarparkHistory.get(3));
        }

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(TIME).show();
            }
        });

        notifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(MINUTE).show();
            }
        });


        time_listener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String hourText = String.valueOf(hour);
                String minuteText = String.valueOf(minute);
                if (hour < 10) {
                    hourText = "0" + hour;
                }
                if (minute < 10) {
                    minuteText = "0" + minute;
                }
                String time = hourText + ":" + minuteText;
                timeText.setText(time);
            }
        };

        minute_listener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String hourText = String.valueOf(hour);
                String minuteText = String.valueOf(minute);
                if (hour < 10) {
                    hourText = "0" + hour;
                }
                if (minute < 10) {
                    minuteText = "0" + minute;
                }
                String time = hourText + ":" + minuteText;
                notifyText.setText(time);
            }
        };

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String daysClickedString = "";
                ArrayList<Integer> daysClicked = new ArrayList<Integer>();
                for (int i = 0; i < days.getChildCount(); i++) {
                    Chip chip = (Chip) days.getChildAt(i);
                    if (chip.isChecked()) {
                        daysClicked.add(i);
                        daysClickedString += i + " ";
                    }
                }
                if (nameText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please input a name!", Toast.LENGTH_SHORT).show();
                } else if (timeText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter the time when you depart!", Toast.LENGTH_SHORT).show();
                } else if (daysClicked.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one day!", Toast.LENGTH_SHORT).show();
                } else if (notifyText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter by how much time you want to notified!", Toast.LENGTH_SHORT).show();
                } else {
                    sqLiteControl.updateSavedCarpark(carParkNo, nameText.getText().toString(),
                            timeText.getText().toString(), daysClickedString, notifyText.getText().toString());
                    finish();
                }
            }
        });
    }

    protected Dialog createDialog(int id) {

        // Get the calander
        Calendar c = Calendar.getInstance();

        // From calander get the year, month, day, hour, minute\
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (id) {
            case TIME:

                // Open the timepicker dialog
                return new TimePickerDialog(SaveCarparkActivity.this, time_listener, hour,
                        minute, false);
            case MINUTE:

                // Open the timepicker dialog
                return new TimePickerDialog(SaveCarparkActivity.this, minute_listener, 0,
                        0, true);

        }
        return null;
    }
}
