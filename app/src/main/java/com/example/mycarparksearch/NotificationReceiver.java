package com.example.mycarparksearch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

public class NotificationReceiver extends BroadcastReceiver {
    private String carParkNo;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);
        String name = intent.getStringExtra(SaveCarparkActivity.NAME);
        String days = intent.getStringExtra(SaveCarparkActivity.DAYS);
        this.context = context;

        boolean isDayToSend = checkIsDayToSend(days);
        if (!isDayToSend) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = "Car Park";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, "Car Park Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Car park notification that updates on lot availability information.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent informationIntent = new Intent(context, InformationActivity.class);
        informationIntent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
        informationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, informationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentText;
        try {
            contentText = (String)(new GetContentText().execute().get());
        } catch (ExecutionException | InterruptedException e) {
            contentText = "Failed to fetch availability";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.carpark)
                .setContentTitle(name + " (" + carParkNo + ")")
                .setContentText(contentText)
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());
    }

    private String getContentText() {
        String result = "";
        CarparkSQLControl con = new CarparkSQLControl(context.getString(R.string.sshHost),
                context.getString(R.string.sshUsername), context.getString(R.string.sshPassword),
                context.getString(R.string.dbHost), Integer.parseInt(context.getString(R.string.dbPort)),
                context.getString(R.string.dbName), context.getString(R.string.dbUsername),
                context.getString(R.string.dbPassword), context);
        int car = 0, motor = 0, heavy = 0;
        try {
            CarparkEntity carparkEntity = con.queryCarparkFullInfo(carParkNo);
            car = carparkEntity.getLotsAvailable(context.getString(R.string.carLotAvail));
            motor = carparkEntity.getLotsAvailable(context.getString(R.string.motorLotAvail));
            heavy = carparkEntity.getLotsAvailable(context.getString(R.string.heavyLotAvail));
        } catch (SQLException e) { result += e.getMessage() + "\n";}
        result += "Car Lots: " + car + "\nMotor Lots: " + motor + "\nHeavy Lots: " + heavy;
        return result;
    }

    private boolean checkIsDayToSend(String days) {
        boolean isDayToSend = false;
        Calendar calendar = Calendar.getInstance();
        int dayRaw = calendar.get(Calendar.DAY_OF_WEEK);
        int day = (dayRaw + 5) % 7;

        StringTokenizer st = new StringTokenizer(days);
        while(st.hasMoreTokens()){
            int dayIndex = Integer.parseInt(st.nextToken());
            if (dayIndex == day) {
                isDayToSend = true;
                break;
            }
        }
        return isDayToSend;
    }

    private class GetContentText extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return getContentText();
        }
    }
}
