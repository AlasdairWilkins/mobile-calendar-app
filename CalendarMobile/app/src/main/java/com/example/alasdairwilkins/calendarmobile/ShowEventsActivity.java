package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.HashMap;

public class ShowEventsActivity extends AppCompatActivity {

    private String TAG = "SHOWEVENTSACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        Intent intent = getIntent();
        intent.getExtras();


        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");
        String jsonString = intent.getStringExtra("events");

        Log.d(TAG, "Extras received: " + message + ", " + jsonString);



        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            int jsonArrayLength = jsonArray.length();

            if (jsonArrayLength > 0) {

                ScrollView scrollView = new ScrollView(this);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(linearLayout);


                for (int i = 0; i < jsonArray.length(); i++) {
                    TextView textView = new TextView(this);
                    textView.setText(jsonArray.getJSONObject(i).getString("title"));
                    linearLayout.addView(textView);
                }
                this.setContentView(scrollView);
            }

        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);

        }




    }
}
