package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends TimeManipulationSuperClass {

    private static final String TAG = "MainActivity";

    private TextView displayMonthTextView;
    private TextView previousMonthTextView;
    private TextView nextMonthTextView;
    private Button createEventButton;
    private Button viewEventButton;

    private int displayMonth = 0;
    private int displayYear = 0;
    private String lastClickedID = "";

    private JSONObject eventsObject;

    private Boolean noDaySelected;


    Calendar calendar = Calendar.getInstance();

    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayMonthTextView = (TextView) findViewById(R.id.displayMonth);
        previousMonthTextView = (TextView) findViewById(R.id.previousMonth);
        nextMonthTextView = (TextView) findViewById(R.id.nextMonth);
        createEventButton = (Button) findViewById(R.id.createEvent);
        viewEventButton = (Button) findViewById(R.id.viewEvent);

        displayMonth = calendar.get(Calendar.MONTH);
        displayYear = calendar.get(Calendar.YEAR);

        long startMillis = getMinimumDate(calendar);
        long endMillis = getMaximumDate(calendar);

        getEvents(startMillis, endMillis);

    }

    public void getEvents(long startMillis, long endMillis) {

        RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(MainActivity.this);

        String url = "http://calendar.alasdairwilkins.com/events?start=" + startMillis + "&end=" + endMillis;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        buildDisplay(responseObject);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    protected void buildDisplay(JSONObject responseObject) {
        eventsObject = responseObject;
        Log.d(TAG, "Response object: " + eventsObject);
        buildButtons();
        buildMonth(eventsObject, displayMonth, displayYear);

        noDaySelected = true;


        previousMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                displayMonth = calendar.get(Calendar.MONTH);
                displayYear = calendar.get(Calendar.YEAR);
                long startMillis = getMinimumDate(calendar);
                long endMillis = getMaximumDate(calendar);
                getEvents(startMillis, endMillis);
            }
        });

        nextMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                displayMonth = calendar.get(Calendar.MONTH);
                displayYear = calendar.get(Calendar.YEAR);
                long startMillis = getMinimumDate(calendar);
                long endMillis = getMaximumDate(calendar);
                getEvents(startMillis, endMillis);
            }
        });
    }

    public void buildButtons() {
        createEventButton.setText("Create an event");
        viewEventButton.setText("View all events");
    }

    public void buildMonthViews(Calendar calendar, int year, int month) {
        Calendar buildCal = (Calendar) calendar.clone();
        buildCal.set(Calendar.MONTH, month - 1);
        previousMonthTextView.setText(buildCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
        buildCal.set(Calendar.MONTH, month + 1);
        nextMonthTextView.setText(buildCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
        buildCal.set(Calendar.MONTH, month);
        displayMonthTextView.setText(buildCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + year);
    }

    public void buildMonth(JSONObject eventsObject, int month, int year) {

        final Calendar buildCal = Calendar.getInstance();
        buildCal.set(year, month, 1, 0, 0, 0);
        buildCal.set(Calendar.MILLISECOND, 0);
        int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
        int monthLength = calendar.getActualMaximum(calendar.DAY_OF_MONTH);

        buildMonthViews(buildCal, year, month);

        for (int week = 1; week < 7; week++) {
            for (int day = 1; day < 8; day++) {
                int id = getResources().getIdentifier("week" + week + "Day" + day, "id", getPackageName());
                TextView textView = (TextView) findViewById(id);
                textView.setText("");
            }
        }

        int number = 1;
        for (int week = 1; week < 7; week++) {
            for (int day = (week == 1) ? dayOfWeek : 1;
                 day < 8; day++) {
                if (number > monthLength) {
                    break;
                }
                int id = getResources().getIdentifier("week" + week + "Day" + day, "id", getPackageName());

                final int displayNumber = number;
                buildCal.set(Calendar.DAY_OF_MONTH, displayNumber);

                final TextView textView = (TextView) findViewById(id);


                String dateKey = Long.toString(buildCal.getTimeInMillis());
                try {
                    JSONArray eventsArray = (JSONArray) eventsObject.get(dateKey);
                    int eventsArrayLength = eventsArray.length();
                    String date = Integer.toString(number);
                    if (eventsArrayLength > 0) {
                        String events = Integer.toString(eventsArrayLength);
                        textView.setText(Html.fromHtml("<b>" + date + "</b><br/><em>" + events + "</em>"));
                    } else {
                        textView.setText(Html.fromHtml("<b>" + date + "</b>"));
                    }
                } catch (org.json.JSONException error){
                    Log.e(TAG, "Error: " + error);
                }

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lastClickedID.equals(getResources().getResourceEntryName(view.getId()))) {
                            lastClickedID = "";
                            noDaySelected = true;
                            Log.d(TAG, "Ahoy hoy");
                            calendar.set(Calendar.DAY_OF_MONTH, currentDay);
                            buildButtons();
                        } else {
                            lastClickedID = getResources().getResourceEntryName(view.getId());
                            noDaySelected = false;
                            calendar.set(Calendar.DAY_OF_MONTH, displayNumber);
                            createEventButton.setText("Create event for " +
                                    buildCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + displayNumber);
                            viewEventButton.setText("View " + buildCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + displayNumber);
                            Log.d(TAG, "BUTTON PRESS" + buildCal.get(Calendar.DAY_OF_MONTH));
                        }


                    }
                });
                number = number + 1;



            }
        }
    }

    public void createEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        HashMap<String,Integer> hashMap = makeHashMap(calendar);
        intent.putExtra("map", hashMap);
        startActivity(intent);
    }

    public void viewEvent(View view) {
        if (noDaySelected) {
            viewAll(view);
        } else {
            viewDay(view);
        }
    }

    public void viewAll(View view) {

        RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(MainActivity.this);
        String url = "http://calendar.alasdairwilkins.com/events";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        Intent intent = new Intent(MainActivity.this, AllEventsActivity.class);
                        intent.putExtra("events", responseArray.toString());
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    public void viewDay(View view) {
        Intent intent = new Intent(this, DayViewActivity.class);
        HashMap<String, Integer> message = makeHashMap(calendar);
        intent.putExtra("map", message);

        Long dateLong = getMinimumTime(calendar);
        String dateKey = Long.toString(dateLong);
        try {
            JSONArray eventsArray = (JSONArray) eventsObject.get(dateKey);
            intent.putExtra("events", eventsArray.toString());
            startActivity(intent);
        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }
    }


}
