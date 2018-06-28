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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class UpdateDeleteEventActivity extends EventSuperClass {


    private String TAG = "UpdateDeleteEventActivity";
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_update_delete_event);
        setContentView(R.layout.activity_update_delete_event);

        super.onCreate(savedInstanceState);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        activityHeader.setText("Update or Delete Event");

        submitButton = (Button) findViewById(R.id.submit);
        deleteButton = (Button) findViewById(R.id.delete);

        Intent intent = getIntent();

        String jsonString = intent.getStringExtra("updateEvent");

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);

            String title = jsonObject.getString("title");
            String description = jsonObject.getString("description");

            Boolean allDay = Boolean.parseBoolean(jsonObject.getString("all_day"));

            if (allDay) {
                allDayCheckBox.setChecked(true);
            }

            long startLong = jsonObject.getLong("start_time");
            long endLong = jsonObject.getLong("end_time");

            final int id = jsonObject.getInt("id");

            startCalendar.setTimeInMillis(startLong);
            endCalendar = (Calendar) startCalendar.clone();
            endCalendar.setTimeInMillis(endLong);

            String startDate = dateString(startCalendar);
            String startTime = timeString(startCalendar);
            String endDate = dateString(endCalendar);
            String endTime = timeString(endCalendar);

            eventTitle.setText(title);
            eventDescription.setText(description);

            startDateTextView.setText(startDate);
            startTimeTextView.setText(startTime);
            endDateTextView.setText(endDate);
            endTimeTextView.setText(endTime);

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JSONObject eventObject = new JSONObject();
                    try {
                        eventObject.put("user", "Alasdair Wilkins");
                        eventObject.put("title", eventTitle.getText());
                        eventObject.put("description", eventDescription.getText());
                        eventObject.put("start_time", startCalendar.getTimeInMillis());
                        eventObject.put("end_time", endCalendar.getTimeInMillis());
                        eventObject.put("all_day", Boolean.toString(allDayCheckBox.isChecked()));
                    } catch (JSONException e) {
                        Log.e(TAG, "Unexpected JSON exception", e);
                    }

                    RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(UpdateDeleteEventActivity.this);
                    String url = "http://calendar.alasdairwilkins.com/events/" + id;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.PUT, url, eventObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObject) {
                                    Intent intent = new Intent(UpdateDeleteEventActivity.this, MainActivity.class);
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

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(UpdateDeleteEventActivity.this);
                    String url = "http://calendar.alasdairwilkins.com/events/" + id;

                    StringRequest stringRequest = new StringRequest
                            (Request.Method.DELETE, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String responseString) {
                                    Intent intent = new Intent(UpdateDeleteEventActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "Error: " + error);
                                }
                            });

                    requestQueue.add(stringRequest);

                }
            });


        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }

    }
}
