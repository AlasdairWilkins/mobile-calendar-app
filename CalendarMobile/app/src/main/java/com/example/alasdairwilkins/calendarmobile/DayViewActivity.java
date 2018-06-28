package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.HashMap;

public class DayViewActivity extends ShowSuperClass {

    private String TAG = "DayViewActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_show_events);
        super.onCreate(savedInstanceState);

        final HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");

        Calendar calendar = Calendar.getInstance();
        calendar.set(message.get("Year"), message.get("Month"), message.get("Day"));

        showEventsTitle.setText(Html.fromHtml("<big>Events for " + dateString(calendar) + "</big><br/>"));

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DayViewActivity.this, CreateEventActivity.class);
                intent.putExtra("map", message);
                startActivity(intent);
            }
        });

    }

}
