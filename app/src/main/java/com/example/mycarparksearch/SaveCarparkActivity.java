package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Interface class for saving car parks for notification
 */
public class SaveCarparkActivity extends AppCompatActivity {
    public static final String DAYS = "com.example.mycarparksearch.DAYS";
    public static final String NAME = "com.example.mycarparksearch.NAME";
    public static final String TIME_LEFT = "com.example.mycarparksearch.TIME_LEFT";
    public static final String TIME_TRIGGER = "com.example.mycarparksearch.TIME_TRIGGER";
    private static final int TIME = 0;
    private static final int MINUTE = 1;
    private TimePickerDialog.OnTimeSetListener time_listener;
    private TimePickerDialog.OnTimeSetListener minute_listener;
    private SQLiteControl sqLiteControl;
    private String carParkNo;
    private TextView carParkNoText;
    private TextView nameText;
    private TextView timeText;
    private ChipGroup days;
    private TextView notifyText;
    private ImageButton save;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_carpark);

        sqLiteControl = new SQLiteControl(getApplicationContext());
        Intent intent = getIntent();
        carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);

        setUpUIElements();
    }

    private void setUpUIElements() {
        carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.append(carParkNo);

        nameText = findViewById(R.id.nameText);
        timeText = findViewById(R.id.timeText);
        days = findViewById(R.id.daysChips);
        notifyText = findViewById(R.id.time2);
        save = findViewById(R.id.saveButton);

        ArrayList<String> savedCarparkHistory = new ArrayList<String>();
        savedCarparkHistory = sqLiteControl.getSavedCarpark(carParkNo);
        if (savedCarparkHistory != null) {
            showPreviousSetting(savedCarparkHistory);
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
                setTimeString(hour, minute);
            }
        };

        minute_listener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                setMinuteString(hour, minute);
            }
        };

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSave();
            }
        });
    }

    private void setNotification(String carParkNo, String name, String time, String daysClicked, String notifyBy) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date timeDate;
        Date notifyByDate;
        try {
            timeDate = formatter.parse(time);
            notifyByDate = formatter.parse(notifyBy);
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Operation failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        long diff = timeDate.getTime() - notifyByDate.getTime();
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffMinutes = diff / (60 * 1000) % 60;

        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY, (int)diffHours);
        calendar.set(Calendar.MINUTE, (int)diffMinutes);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        intent.putExtra(SaveCarparkActivity.NAME, name);
        intent.putExtra(SaveCarparkActivity.DAYS, daysClicked);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private Dialog createDialog(int id) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (id) {
            case TIME:
                return new TimePickerDialog(SaveCarparkActivity.this, time_listener, hour,
                        minute, false);
            case MINUTE:
                return new TimePickerDialog(SaveCarparkActivity.this, minute_listener, 0,
                        0, true);
        }
        return null;
    }

    private void showPreviousSetting(ArrayList<String> savedCarparkHistory) {
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

    private void setTimeString(int hour, int minute) {
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

    private void setMinuteString(int hour, int minute) {
        if (hour > 0) {
            Toast.makeText(getApplicationContext(), "The time set should not exceed an hour!", Toast.LENGTH_SHORT).show();
            notifyText.setText("");
            return;
        }
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

    private void performSave() {
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
            setNotification(carParkNo, nameText.getText().toString(),
                    timeText.getText().toString(), daysClickedString, notifyText.getText().toString());
            finish();
        }
    }
}
