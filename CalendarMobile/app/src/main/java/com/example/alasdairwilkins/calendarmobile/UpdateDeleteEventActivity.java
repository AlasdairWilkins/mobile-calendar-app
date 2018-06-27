package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

public class UpdateDeleteEventActivity extends EventActivity {

    private String TAG = "UpdateDeleteEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();

        String jsonString = intent.getStringExtra("updateEvent");

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);

            String title = jsonObject.getString("title");
            String description = jsonObject.getString("description");

            Boolean allDay = jsonObject.getBoolean("all_day");

            long startLong = jsonObject.getLong("start_time");
            long endLong = jsonObject.getLong("end_time");

            startCalendar.setTimeInMillis(startLong);
            endCalendar.setTimeInMillis(endLong);

            String startDate = dateString(startCalendar);
            String startTime = timeString(startCalendar);
            String endDate = dateString(endCalendar);
            String endTime = timeString(endCalendar);

            eventTitle.setText(title);
            eventTitle.setText(description);

            startDateTextView.setText(startDate);
            startTimeTextView.setHint(startTime);
            endDateTextView.setText(endDate);
            endTimeTextView.setHint(endTime);

        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }
    }
}
