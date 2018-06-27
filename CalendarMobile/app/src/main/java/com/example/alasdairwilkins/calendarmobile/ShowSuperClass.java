package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

abstract class ShowActivity extends AppCompatActivity {
    private String TAG = "ShowActivity";

    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        intent = getIntent();
        intent.getExtras();

        String jsonString = intent.getStringExtra("events");

        try {

            final JSONArray jsonArray = new JSONArray(jsonString);

            Log.d(TAG, "JSON Array: " + jsonArray);

            int jsonArrayLength = jsonArray.length();

            if (jsonArrayLength > 0) {

                ScrollView scrollView = new ScrollView(this);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(linearLayout);


                for (int i = 0; i < jsonArray.length(); i++) {
                    final TextView textView = new TextView(this);
                    String title = jsonArray.getJSONObject(i).getString("title");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    Boolean allDay = Boolean.parseBoolean(jsonArray.getJSONObject(i).getString("all_day"));
                    Long startLong = jsonArray.getJSONObject(i).getLong("start_time");
                    Long endLong = jsonArray.getJSONObject(i).getLong("start_time");

                    if (allDay) {
                        textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description + "</em><br/>"));
                    } else {
                        textView.setText(Html.fromHtml("<b>" + title + "</b><br/><em>" + description + "</em><br/>" + startLong + " to " + endLong + "<br/>"));
                    }

                    textView.setTag(i);
                    linearLayout.addView(textView);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                int i = (int) textView.getTag();
                                Intent intent = new Intent(ShowActivity.this, UpdateDeleteEventActivity.class);
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                intent.putExtra("updateEvent", jsonObject.toString());
                                startActivity(intent);
                            } catch (org.json.JSONException error) {
                                Log.e(TAG, "Error: " + error);

                            }

                        }
                    });

                }
                this.setContentView(scrollView);
            }

        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }




    }
}
