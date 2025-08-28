package org.ttaluri.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class DataHandler {

    public List<HourlyForecast> parseHourlyForecast(JSONArray daysArray) {
        List<HourlyForecast> hourlyForecastList = new ArrayList<>();

        try {

            JSONObject firstDay = daysArray.getJSONObject(0);
            JSONArray hoursArray = firstDay.getJSONArray("hours");

            long currentTime = System.currentTimeMillis() / 1000;

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);

            String dayLabel = "Today";


            for (int i = 0; i < hoursArray.length(); i++) {
                JSONObject hourData = hoursArray.getJSONObject(i);
                long datetimeEpoch = hourData.getLong("datetimeEpoch");


                if (datetimeEpoch > currentTime) {
                    Date date = new Date(datetimeEpoch * 1000);


                    if (i == 0) {
                        dayLabel = "Today";
                    } else {
                        dayLabel = dayFormat.format(date);
                    }

                    String timeFormatted = timeFormat.format(date);
                    String icon = hourData.getString("icon");
                    double temp = hourData.getDouble("temp");
                    String conditions = hourData.getString("conditions");
                    String unit = hourData.getString("unit");


                    HourlyForecast forecast = new HourlyForecast(dayLabel, timeFormatted, icon, temp, conditions, unit);
                    hourlyForecastList.add(forecast);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hourlyForecastList;
    }

    public TreeMap<String, Double> createTemperatureMap(JSONArray hoursArray) {
        TreeMap<String, Double> temperatureData = new TreeMap<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);

        long currentTime = System.currentTimeMillis() / 1000;

        try {
            for (int i = 0; i < hoursArray.length(); i++) {
                JSONObject hourData = hoursArray.getJSONObject(i);
                long datetimeEpoch = hourData.getLong("datetimeEpoch");

                if (datetimeEpoch > currentTime) {
                    Date date = new Date(datetimeEpoch * 1000);
                    String timeFormatted = timeFormat.format(date);
                    double temp = hourData.getDouble("temp");
                    temperatureData.put(timeFormatted, temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temperatureData;
    }
}
