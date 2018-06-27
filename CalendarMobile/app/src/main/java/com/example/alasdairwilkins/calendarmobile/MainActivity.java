package com.example.alasdairwilkins.calendarmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView displayMonthTextView;
    private TextView previousMonthTextView;
    private TextView nextMonthTextView;
    private Button createEventButton;
    private Button viewEventButton;

    private long startRange = 0;
    private long endRange = 0;

    private int displayMonth = 0;
    private int displayYear = 0;
    private String lastClickedID = "";

    private JSONObject eventsObject;

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
        startRange = startMillis;
        endRange = endMillis;

        getEvents(startMillis, endMillis);

    }

    public void getEvents(long startMillis, long endMillis) {

        RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(MainActivity.this);
        String url = "http://10.0.17.212:8000/events?start=" + startMillis + "&end=" + endMillis;

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



        previousMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setMinimumDate(calendar, displayMonth);
//                displayMonth = calendar.get(Calendar.MONTH);
//                displayYear = calendar.get(Calendar.YEAR);
//                if (startRange <= calendar.getTimeInMillis()) {
//                    Log.d(TAG, "Current Info: Not yet!");
//                    buildMonth(eventsObject, displayMonth, displayYear);
//                } else {
//                    Calendar prevCal = (Calendar) calendar.clone();
////                    setMaximumDate(prevCal, displayMonth);
//                    long endMillis = prevCal.getTimeInMillis();
////                    setMinimumDate(prevCal, displayMonth);
//                    long startMillis = prevCal.getTimeInMillis();
//                    startRange = startMillis;
//                    endRange = endMillis;
//                    Log.d(TAG, "Range: " + startMillis + ", " + endMillis);
//                    getEvents(startMillis, endMillis);
//                }
            }
        });

        nextMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                displayMonth = calendar.get(Calendar.MONTH);
                displayYear = calendar.get(Calendar.YEAR);
                buildMonth(eventsObject, displayMonth, displayYear);
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
                    textView.setText(Integer.toString(number) + (eventsArray.length() > 0 ? "\n" + eventsArray.length() : ""));
                } catch (org.json.JSONException error){
                    Log.e(TAG, "Error: " + error);
                }

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lastClickedID.equals(getResources().getResourceEntryName(view.getId()))) {
                            lastClickedID = "";
                            Log.d(TAG, "Ahoy hoy");
                            calendar.set(Calendar.DAY_OF_MONTH, currentDay);
                            buildButtons();
                        } else {
                            lastClickedID = getResources().getResourceEntryName(view.getId());
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
        Intent intent = new Intent(this, ShowEventsActivity.class);
        HashMap<String, Integer> message = makeHashMap(calendar);
        intent.putExtra("map", message);

        setMinimumTime(calendar);
        String dateKey = Long.toString(calendar.getTimeInMillis());
        try {
            JSONArray eventsArray = (JSONArray) eventsObject.get(dateKey);
            intent.putExtra("events", eventsArray.toString());
            startActivity(intent);
        } catch (org.json.JSONException error) {
            Log.e(TAG, "Error: " + error);
        }
    }

    public HashMap<String,Integer> makeHashMap(Calendar calendar){
        HashMap newMap = new HashMap<String,Integer>();
        newMap.put("Year", calendar.get(Calendar.YEAR));
        newMap.put("Month", calendar.get(Calendar.MONTH));
        newMap.put("Day", calendar.get(Calendar.DAY_OF_MONTH));
        return newMap;
    }

    public void setMinimumDate(Calendar cal, int month) {
//        , int month
//        cal.set(Calendar.MONTH, month - 1);
//        Log.d(TAG, "Current Info: " + cal.get(Calendar.MONTH) + ", " + cal.get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_MONTH, 1);
    }

    public void setMinimumTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public void setMaximumDate(Calendar cal) {
        //        , int month
//        cal.set(Calendar.MONTH, month + 1);
//        cal.get(Calendar.YEAR);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    public void setMaximumTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    public long getMinimumDate(Calendar cal) {
        Calendar minCal = Calendar.getInstance();
        minCal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) - 1,
                1, 0, 0, 0);
        minCal.set(Calendar.MILLISECOND, 0);
        return minCal.getTimeInMillis();
    }

    public long getMaximumDate(Calendar cal) {
        Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        maxCal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        maxCal.set(Calendar.DAY_OF_MONTH, maxCal.getActualMaximum(maxCal.DAY_OF_MONTH));
        maxCal.set(Calendar.HOUR_OF_DAY, 23);
        maxCal.set(Calendar.MINUTE, 59);
        maxCal.set(Calendar.SECOND, 59);
        maxCal.set(Calendar.MILLISECOND, 999);
        return maxCal.getTimeInMillis();
    }
}
