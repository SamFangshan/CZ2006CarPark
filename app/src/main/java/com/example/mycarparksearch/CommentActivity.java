package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        String carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);

        TextView carParkNoText = findViewById(R.id.carParkNo);
        carParkNoText.setText(carParkNo);

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView commentText = findViewById(R.id.commentText);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "Please provide rating!", Toast.LENGTH_SHORT).show();
                } else if (commentText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please provide comments!", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });
    }
}
