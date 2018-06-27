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

public class DayViewActivity extends ShowActivity {

    private String TAG = "DayViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_show_events);
        super.onCreate(savedInstanceState);

        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");


    }

}
