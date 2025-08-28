package org.ttaluri.weatherapp;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.ttaluri.weatherapp.DailyForecast;


public class DailyForecastActivity extends AppCompatActivity {

    private RecyclerView dailyRecyclerView;
    private DailyForecastAdapter dailyForecastAdapter;
    private List<DailyForecast> dailyForecastList = new ArrayList<>();
    private static final String TAG = "DailyForecastActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        dailyRecyclerView = findViewById(R.id.dailyRecyclerView);
        dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dailyForecastAdapter = new DailyForecastAdapter(this, dailyForecastList);
        dailyRecyclerView.setAdapter(dailyForecastAdapter);

        fetchDailyForecast();
    }

    private void fetchDailyForecast() {
        String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Chicago?unitGroup=metric&key=NB7D2TRUN4MQM6LJJ9K7P7AU9";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray days = response.getJSONArray("days");
                            for (int i = 0; i < days.length(); i++) {
                                JSONObject day = days.getJSONObject(i);
                                DailyForecast forecast = new DailyForecast(
                                        day.getLong("datetimeEpoch"),
                                        day.getDouble("tempmax"),
                                        day.getDouble("tempmin"),
                                        day.optString("description", "N/A"),
                                        day.optDouble("precipprob", 0.0),
                                        day.optInt("uvindex", 0),
                                        day.optString("icon", "unknown"),
                                        day.getJSONArray("hours").getJSONObject(8).getDouble("temp"),
                                        day.getJSONArray("hours").getJSONObject(13).getDouble("temp"),
                                        day.getJSONArray("hours").getJSONObject(17).getDouble("temp"),
                                        day.getJSONArray("hours").getJSONObject(23).getDouble("temp")
                                );
                                dailyForecastList.add(forecast);
                            }
                            dailyForecastAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing JSON response", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error fetching data", error);
            }
        });

        queue.add(jsonObjectRequest);
    }
}
