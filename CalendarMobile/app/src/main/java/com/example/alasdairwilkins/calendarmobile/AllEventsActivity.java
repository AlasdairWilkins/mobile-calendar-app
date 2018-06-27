package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import java.util.Calendar;
import java.util.HashMap;

public class AllEventsActivity extends ShowSuperClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_events);
        super.onCreate(savedInstanceState);

        showEventsTitle.setText(Html.fromHtml("<big>All Events</big><br/>"));

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Intent intent = new Intent(AllEventsActivity.this, CreateEventActivity.class);
                HashMap message = makeHashMap(calendar);
                intent.putExtra("map", message);
                startActivity(intent);
            }
        });

    }
}
