package com.example.alasdairwilkins.calendarmobile;

import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

abstract class TimeManipulationSuperClass extends AppCompatActivity {

    public long getMinimumDate(Calendar cal) {
        Calendar minCal = Calendar.getInstance();
        minCal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                1);
        return getMinimumTime(minCal);
    }

    public long getMinimumTime(Calendar minCal) {
        minCal.set(Calendar.HOUR_OF_DAY, 0);
        minCal.set(Calendar.MINUTE, 0);
        minCal.set(Calendar.SECOND, 0);
        minCal.set(Calendar.MILLISECOND, 0);
        return minCal.getTimeInMillis();
    }

    public long getMaximumDate(Calendar cal) {
        Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        maxCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        maxCal.set(Calendar.DAY_OF_MONTH, maxCal.getActualMaximum(maxCal.DAY_OF_MONTH));
        return getMaximumTime(maxCal);
    }

    public long getMaximumTime(Calendar maxCal) {
        maxCal.set(Calendar.HOUR_OF_DAY, 23);
        maxCal.set(Calendar.MINUTE, 59);
        maxCal.set(Calendar.SECOND, 59);
        maxCal.set(Calendar.MILLISECOND, 999);
        return maxCal.getTimeInMillis();
    }

    public String dateString(Calendar calendar) {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR);

    }

    public String timeString(Calendar calendar) {
        return ((calendar.get(Calendar.HOUR) == 0) ? 12 : calendar.get(Calendar.HOUR)) + ":" +
                ((calendar.get(Calendar.MINUTE) < 10) ? "0" : "") + calendar.get(Calendar.MINUTE) + " " +
                calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.ENGLISH);
    }

    public HashMap<String,Integer> makeHashMap(Calendar calendar){
        HashMap newMap = new HashMap<String,Integer>();
        newMap.put("Year", calendar.get(Calendar.YEAR));
        newMap.put("Month", calendar.get(Calendar.MONTH));
        newMap.put("Day", calendar.get(Calendar.DAY_OF_MONTH));
        return newMap;
    }

}
