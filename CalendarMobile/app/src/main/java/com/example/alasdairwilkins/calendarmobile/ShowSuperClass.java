package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

abstract class ShowSuperClass extends TimeManipulationSuperClass {
    private String TAG = "ShowSuperClass";

    public ScrollView showEventsScrollView;
    public TextView showEventsTitle;
    public Button createEventButton;


    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        intent = getIntent();
        intent.getExtras();

        String jsonString = intent.getStringExtra("events");

        showEventsTitle = new TextView(this);

        showEventsScrollView = findViewById(R.id.showEventsScrollView);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        showEventsScrollView.addView(linearLayout);

        linearLayout.addView(showEventsTitle);

        createEventButton = new Button(this);
        createEventButton.setText("Create event");

        linearLayout.addView(createEventButton);


        try {

            final JSONArray jsonArray = new JSONArray(jsonString);

            int jsonArrayLength = jsonArray.length();

            if (jsonArrayLength > 0) {


                for (int i = 0; i < jsonArray.length(); i++) {
                    final TextView textView = new TextView(this);
                    String title = jsonArray.getJSONObject(i).getString("title");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    Boolean allDay = Boolean.parseBoolean(jsonArray.getJSONObject(i).getString("all_day"));
                    Long startLong = jsonArray.getJSONObject(i).getLong("start_time");
                    Long endLong = jsonArray.getJSONObject(i).getLong("end_time");
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTimeInMillis(startLong);
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTimeInMillis(endLong);
                    String startDate = dateString(startCal);
                    String endDate = dateString(endCal);
                    if (allDay) {
                        if (startLong.equals(endLong)) {
                            textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description
                                    + "</em><br/>" + startDate));
                        } else {
                            textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description
                                    + "</em><br/>" + startDate + " to " + endDate));
                        }
                    } else {
                        String startTime = timeString(startCal);
                        String endTime = timeString(endCal);
                        if (startDate.equals(endDate)) {
                            textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description
                                    + "</em><br/>" + startTime + " to " + endTime + " on " + startDate + "<br/>"));
                        } else {
                            textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description
                                    + "</em><br/>" + startTime + " on " + startDate + " to " + endTime + " on "
                                    + endDate + "<br/>"));
                        }
                    }

                    textView.setTag(i);
                    linearLayout.addView(textView);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                int i = (int) textView.getTag();
                                Intent intent = new Intent(ShowSuperClass.this, UpdateDeleteEventActivity.class);
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                intent.putExtra("updateEvent", jsonObject.toString());
                                startActivity(intent);
                            } catch (org.json.JSONException error) {
                                Log.e(TAG, "Error: " + error);

                            }

                        }
                    });

                }
            }

        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }


    }
}
