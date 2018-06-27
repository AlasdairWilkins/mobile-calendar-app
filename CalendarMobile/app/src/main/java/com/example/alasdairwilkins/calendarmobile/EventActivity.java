package com.example.alasdairwilkins.calendarmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.util.Locale;

abstract class EventActivity extends AppCompatActivity {
    public EditText eventTitle;
    public EditText eventDescription;
    public TextView startDateTextView;
    public DatePickerDialog.OnDateSetListener startDateSetListener;
    public TextView startTimeTextView;
    public TimePickerDialog.OnTimeSetListener startTimeSetListener;
    public TextView endDateTextView;
    public DatePickerDialog.OnDateSetListener endDateSetListener;
    public TextView endTimeTextView;
    public TimePickerDialog.OnTimeSetListener endTimeSetListener;
    public CheckBox allDayCheckBox;
    public Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");

        eventTitle = (EditText) findViewById(R.id.title);
        eventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "Before Text Change: " + charSequence + i + i1 + i2);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "On Text Change: " + charSequence + i + i1 + i2);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSubmit();
            }
        });

        eventDescription = (EditText) findViewById(R.id.description);

        startDateTextView = (TextView) findViewById(R.id.startDate);
        startTimeTextView = (TextView) findViewById(R.id.startTime);
        endDateTextView = (TextView) findViewById(R.id.endDate);
        endTimeTextView = (TextView) findViewById(R.id.endTime);

        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(message.get("Year"), message.get("Month"), message.get("Day"));
        final Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY) + 1);

        String hintStartDate = dateString(startCalendar);
        String hintStartTime = timeString(startCalendar);
        String hintEndDate = dateString(endCalendar);
        String hintEndTime = timeString(endCalendar);

        startDateTextView.setText(hintStartDate);
        startTimeTextView.setHint(hintStartTime);
        endDateTextView.setText(hintEndDate);
        endTimeTextView.setHint(hintEndTime);

        allDayCheckBox = (CheckBox) findViewById(R.id.allDay);

        allDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startTimeTextView.setEnabled(false);
                    endTimeTextView.setEnabled(false);
                    if (startTimeTextView.getText() == "") {startTimeTextView.setText("N/A");}
                    if (endTimeTextView.getText() == "") {endTimeTextView.setText("N/A");}
                } else {
                    startTimeTextView.setEnabled(true);
                    endTimeTextView.setEnabled(true);
                    if (startTimeTextView.getText() == "N/A") {startTimeTextView.setText("");}
                    if (endTimeTextView.getText() == "N/A") {endTimeTextView.setText("");}
                }
            }
        });

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setEnabled(false);

        startDateTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = makeDatePickerDialog(startCalendar, startDateSetListener);
                dialog.show();

            }

        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                updateDate(startCalendar, endCalendar, startDateTextView, endDateTextView,
                        endTimeTextView, year, month, day, true);

                checkSubmit();

            }
        };

        startTimeTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = makeTimePickerDialog(startCalendar, startTimeSetListener);
                dialog.show();

            }

        });

        startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                updateTime(startCalendar, endCalendar, startTimeTextView, endTimeTextView,
                        endDateTextView, hour, minute, true);
                checkSubmit();

            }
        };

        endDateTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = makeDatePickerDialog(endCalendar, endDateSetListener);

                if (startDateTextView.getText() != "") {
                    dialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
                }

                dialog.show();

            }

        });

        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                updateDate(endCalendar, startCalendar, endDateTextView, startDateTextView,
                        startTimeTextView, year, month, day, false);

                checkSubmit();

            }
        };

        endTimeTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = makeTimePickerDialog(endCalendar, endTimeSetListener);
                dialog.show();

            }

        });

        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                updateTime(endCalendar, startCalendar, endTimeTextView, startTimeTextView,
                        startDateTextView, hour, minute, false);

                checkSubmit();
            }
        };

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
                    Log.e(CreateEventActivity.TAG, "Unexpected JSON exception", e);
                }
                Log.d(CreateEventActivity.TAG, eventObject.toString());

                RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(EventActivity.this);
                String url = "http://10.0.19.178:8000/events";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, eventObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObject) {
                                Log.d("EVENT ACTIVITY", "Response object: " + responseObject);
                                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("EVENT ACTIVITY", "Error: " + error);
                            }
                        });

                requestQueue.add(jsonObjectRequest);

            }
        });


    }

    public void checkSubmit() {
        if (submitValid()) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    public boolean submitValid() {
        if (eventTitle.getText().toString().length() != 0 && startDateTextView.getText() != "" && endDateTextView.getText() != "") {
            if (!allDayCheckBox.isChecked()) {
                if (startTimeTextView.getText() != "" && endTimeTextView.getText() != "") {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public String dateString(Calendar calendar) {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR);

    }

    public String timeString(Calendar calendar) {
        return ((calendar.get(Calendar.HOUR) == 0) ? 12 : calendar.get(Calendar.HOUR)) + ":" +
                ((calendar.get(Calendar.MINUTE) < 10) ? "0" : "") + calendar.get(Calendar.MINUTE) + " " +
                calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.ENGLISH);
    }

    public DatePickerDialog makeDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener dateSetListener) {
        DatePickerDialog dialog = new DatePickerDialog(
                EventActivity.this,
                android.R.style.Theme_DeviceDefault,
                dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public TimePickerDialog makeTimePickerDialog(Calendar calendar, TimePickerDialog.OnTimeSetListener timeSetListener) {
        TimePickerDialog dialog = new TimePickerDialog(
                EventActivity.this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public void updateDate(Calendar updatedCalendar, Calendar otherCalendar, TextView updatedDateTextView,
                           TextView otherDateTextView, TextView otherTimeTextView, int year, int month, int day, boolean isStart) {

        updatedCalendar.set(year, month, day);

        if (isStart ? updatedCalendar.after(otherCalendar) : otherCalendar.after(updatedCalendar)) {
            otherCalendar.set(year, month, day,
                    (isStart ? updatedCalendar.get(Calendar.HOUR_OF_DAY) + 1 : updatedCalendar.get(Calendar.HOUR_OF_DAY) - 1),
                    updatedCalendar.get(Calendar.MINUTE));

            if (otherTimeTextView.getText() != timeString(otherCalendar)) {
                otherTimeTextView.setText(timeString(otherCalendar));
            }

        }

        updatedDateTextView.setText(dateString(updatedCalendar));
        otherDateTextView.setText(dateString(otherCalendar));

    }

    public void updateTime(Calendar updatedCalendar, Calendar otherCalendar, TextView updatedTimeTextView,
                           TextView otherTimeTextView, TextView otherDateTextView, int hour, int minute, boolean isStart) {

        updatedCalendar.set(Calendar.HOUR_OF_DAY, hour);
        updatedCalendar.set(Calendar.MINUTE, minute);

        updatedTimeTextView.setText(timeString(updatedCalendar));

        if (isStart ? updatedCalendar.after(otherCalendar) : otherCalendar.after(updatedCalendar)) {
            otherCalendar.set(Calendar.HOUR_OF_DAY,
                    (isStart ? updatedCalendar.get(Calendar.HOUR_OF_DAY) + 1 : updatedCalendar.get(Calendar.HOUR_OF_DAY) - 1));
            otherCalendar.set(Calendar.MINUTE, updatedCalendar.get(Calendar.MINUTE));
            if (otherTimeTextView.getText() == "") {
                otherTimeTextView.setHint(timeString(otherCalendar));
            } else {
                otherTimeTextView.setText(timeString(otherCalendar));
            }
            if (otherDateTextView.getText() != dateString(otherCalendar)) {
                otherDateTextView.setText(dateString(otherCalendar));
            }

        }
    }


}
