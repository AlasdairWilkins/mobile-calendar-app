package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
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

            final JSONArray jsonArray = new JSONArray(jsonString);

            int jsonArrayLength = jsonArray.length();

            if (jsonArrayLength > 0) {

                ScrollView scrollView = new ScrollView(this);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(linearLayout);


                for (int i = 0; i < jsonArray.length(); i++) {
                    final TextView textView = new TextView(this);
                    textView.setText(jsonArray.getJSONObject(i).getString("title"));
                    textView.setTag(i);
                    linearLayout.addView(textView);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                int i = (int) textView.getTag();
                                Intent intent = new Intent(ShowEventsActivity.this, UpdateDeleteEventActivity.class);
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
