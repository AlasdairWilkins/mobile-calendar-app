package com.example.alasdairwilkins.calendarmobile;

import android.os.Bundle;

import java.util.HashMap;

public class DayViewActivity extends ShowSuperClass {

    private String TAG = "DayViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_show_events);
        super.onCreate(savedInstanceState);

        HashMap<String,Integer> message = (HashMap<String,Integer>) intent.getSerializableExtra("map");


    }

}
