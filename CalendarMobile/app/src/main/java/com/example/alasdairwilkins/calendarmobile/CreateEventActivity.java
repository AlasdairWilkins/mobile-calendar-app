package com.example.alasdairwilkins.calendarmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CreateEventActivity extends EventActivity {

    private String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        activityHeader.setText("Create Event");

        eventTitle.setHint("Title");
        eventDescription.setHint("Description (optional)");
        submitButton.setText("Submit");

        Intent intent = getIntent();
        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");

        startCalendar.set(message.get("Year"), message.get("Month"), message.get("Day"));
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
                    eventObject.put("start_time", startCalendar.getTimeInMillis());
                    eventObject.put("end_time", endCalendar.getTimeInMillis());
                    eventObject.put("all_day", allDayCheckBox.isChecked());
                } catch (JSONException e) {
                    Log.e(TAG, "Unexpected JSON exception", e);
                }
                Log.d(TAG, eventObject.toString());

                RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(CreateEventActivity.this);
                String url = "http://10.0.17.212:8000/events";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, eventObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObject) {
                                Log.d(TAG, "Response object: " + responseObject);
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