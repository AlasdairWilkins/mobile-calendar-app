package com.example.alasdairwilkins.calendarmobile;


import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class CreateEventActivity extends EventSuperClass {

    private String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setContentView(R.layout.activity_create_event);
        setContentView(R.layout.activity_create_event);

        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        activityHeader.setText("Create Event");

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setEnabled(false);

        eventTitle.setHint("Title");
        eventDescription.setHint("Description (optional)");

        Intent intent = getIntent();
        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");

        startCalendar.set(message.get("Year"), message.get("Month"), message.get("Day"));
        endCalendar = (Calendar) startCalendar.clone();
        endCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY) + 1);

        String hintStartDate = dateString(startCalendar);
        String hintStartTime = timeString(startCalendar);
        String hintEndDate = dateString(endCalendar);
        String hintEndTime = timeString(endCalendar);

        startDateTextView.setText(hintStartDate);
        startTimeTextView.setHint(hintStartTime);
        endDateTextView.setText(hintEndDate);
        endTimeTextView.setHint(hintEndTime);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject eventObject = new JSONObject();
                try {
                    eventObject.put("user", "Alasdair Wilkins");
                    eventObject.put("title", eventTitle.getText());
                    eventObject.put("description", eventDescription.getText());
                    eventObject.put("all_day", Boolean.toString(allDayCheckBox.isChecked()));

                    if (allDayCheckBox.isChecked()) {
                        eventObject.put("start_time", getMinimumTime(startCalendar));
                        eventObject.put("end_time", getMinimumTime(endCalendar));
                    } else {
                        eventObject.put("start_time", startCalendar.getTimeInMillis());
                        eventObject.put("end_time", endCalendar.getTimeInMillis());
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Unexpected JSON exception", e);
                }

                RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(CreateEventActivity.this);
                String url = "http://calendar.alasdairwilkins.com/events";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, eventObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObject) {
                                Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Error: " + error);
                            }
                        });

                requestQueue.add(jsonObjectRequest);

            }
        });





    }
}