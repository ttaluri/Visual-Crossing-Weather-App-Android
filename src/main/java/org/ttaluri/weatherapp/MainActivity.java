package org.ttaluri.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import android.net.Uri;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;

import com.github.mikephil.charting.utils.ColorTemplate;

import android.Manifest;

import android.content.pm.PackageManager;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String API_KEY = "NB7D2TRUN4MQM6LJJ9K7P7AU9";
    private static final String API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/{location}?key=" + API_KEY;

    private TextView temperatureTextView, feelsLikeTextView, cityNameTextView, windTextView, humidityTextView, uvIndexTextView, visibilityTextView, weatherDescriptionTextView, cloudCoverTextView;
    private ImageView weatherIcon, locationIcon;
    private RecyclerView hourlyRecyclerView;
    private RequestQueue requestQueue;
    private String currentUnit = "F";
    private Location currentLocation; // Declare currentLocation
    private boolean isCelsius = false;
    private HourlyWeatherAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        requestQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();

        locationIcon.setOnClickListener(v -> getDeviceLocation());

        ImageView unitToggleIcon = findViewById(R.id.unitc);
        unitToggleIcon.setOnClickListener(v -> toggleTemperatureUnit());

        if (hasNetworkConnection()) {
            getDeviceLocation();
        } else {
            showNoNetworkMessage();
        }

        //map icon
        ImageView mapIcon = findViewById(R.id.map);
        mapIcon.setOnClickListener(v -> {
            String location = cityNameTextView.getText().toString().split(",")[0];;

            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(MainActivity.this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
            }
        });

        //Target Icon
        ImageView targetsIcon = findViewById(R.id.targets);
        targetsIcon.setOnClickListener(v -> getDeviceLocation());

        //Share Icon
        ImageView shareIcon = findViewById(R.id.share);
        shareIcon.setOnClickListener(v -> {
            String weatherContent = "Weather in " + cityNameTextView.getText().toString() +
                    ": " + temperatureTextView.getText().toString() +
                    "\nFeels Like: " + feelsLikeTextView.getText().toString() +
                    "\nConditions: " + weatherDescriptionTextView.getText().toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, weatherContent);
            startActivity(Intent.createChooser(shareIntent, "Share Weather"));
        });

        //callendar Icon
        ImageView calendarIcon = findViewById(R.id.calender);
        calendarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyForecastActivity.class);
            intent.putExtra("location", cityNameTextView.getText().toString());
            startActivity(intent);
        });

        //Location Icon
        ImageView locationIcon = findViewById(R.id.location);
        locationIcon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Enter a location")
                    .setMessage("For US locations, enter as 'City', or 'City, state'. For international, enter as 'City, Country'.")
                    .setCancelable(false);

            final EditText input = new EditText(MainActivity.this);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String location = input.getText().toString();
                if (!location.isEmpty()) {
                    fetchWeatherData(location, currentUnit);
                } else {
                    Toast.makeText(MainActivity.this, "Location cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });



    }

    private void initializeViews() {
        temperatureTextView = findViewById(R.id.temperature);
        feelsLikeTextView = findViewById(R.id.feelsLike);
        cityNameTextView = findViewById(R.id.resolvedAddress);
        windTextView = findViewById(R.id.wind);
        humidityTextView = findViewById(R.id.humidity);
        uvIndexTextView = findViewById(R.id.uvIndex);
        visibilityTextView = findViewById(R.id.visibility);
        weatherDescriptionTextView = findViewById(R.id.weatherDescription);
        cloudCoverTextView = findViewById(R.id.cloudCover);
        weatherIcon = findViewById(R.id.weatherIcon);
        locationIcon = findViewById(R.id.location);
        hourlyRecyclerView = findViewById(R.id.hourlyRecyclerView);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void toggleTemperatureUnit() {
        if (currentLocation != null) {
            isCelsius = !isCelsius;  // Toggle the unit
            currentUnit = isCelsius ? "C" : "F";

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                Log.e("WeatherApp", "Adapter is not initialized");
            }

            ImageView unitToggleIcon = findViewById(R.id.unitc);
            unitToggleIcon.setImageResource(isCelsius ? R.drawable.units_c : R.drawable.units_f);

            updateWeatherForLocation(currentLocation);
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWeatherForLocation(Location location) {
        currentLocation = location;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty() && addresses.get(0).getLocality() != null) {
                String city = addresses.get(0).getLocality();
                fetchWeatherData(city, currentUnit);
            } else {
                throw new Exception("Geocoding failed, using coordinates.");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error geocoding location: " + e.getMessage());
            fetchWeatherData(location.getLatitude() + "," + location.getLongitude(), currentUnit);
        }
    }

    public void fetchWeatherData(String location, String unit) {

        String url = API_URL.replace("{location}", location);
        Log.d("WeatherApp", "Fetching data from: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("WeatherApp", "Response: " + response.toString());
                    parseWeatherData(response, unit);
                },
                error -> {
                    Log.e("WeatherApp", "Error fetching weather data", error);
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    private void parseWeatherData(JSONObject response, String unit) {
        try {
            String resolvedAddress = response.getString("resolvedAddress");
            JSONArray daysArray = response.getJSONArray("days");
            JSONObject currentDay = daysArray.getJSONObject(0);

            String city = getCityFromResolvedAddress(resolvedAddress);
            cityNameTextView.setText(city);

            updateWeatherUI(currentDay, city, unit);

            String timezone = response.getString("timezone");
            TimeZone timeZone = TimeZone.getTimeZone(timezone);

            Calendar calendar = Calendar.getInstance(timeZone);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            sdf.setTimeZone(timeZone);
            String resolvedTime = sdf.format(calendar.getTime());
            String cityNameWithTime = city + ", " + resolvedTime;
            cityNameTextView.setText(cityNameWithTime);

            List<HourlyForecast> hourlyForecasts = parseHourlyForecast(currentDay.getJSONArray("hours"), timeZone);
            cityNameTextView.setText(city);
            HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(this, hourlyForecasts, city);
            hourlyRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            TreeMap<String, Double> temperatureData = parseHourlyTemperatureData(currentDay);

            JSONArray hours = currentDay.getJSONArray("hours");
            long currentTimeMillis = System.currentTimeMillis();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault());
            for (int i = 0; i < hours.length(); i++) {
                JSONObject hour = hours.getJSONObject(i);
                String timeString = hour.getString("datetime");

                LocalDate today = LocalDate.now();
                LocalDateTime dateTime = LocalDateTime.of(today, LocalTime.parse(timeString, formatter));
                long hourTimeMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                if (hourTimeMillis > currentTimeMillis) {
                    double temp = hour.getDouble("temp");
                    temperatureData.put(timeString, temp);
                }
            }

            if (resolvedAddress == null || resolvedAddress.isEmpty()) {
                showErrorDialog("Location Error", "The specified location could not be resolved. Please try again later.");
                return;
            }


            /*
            ChartMaker chartMaker = new ChartMaker(this, findViewById(R.id.chartContainer));
            chartMaker.makeChart(temperatureData, System.currentTimeMillis());
*/
            //TreeMap<String, Double> temperatureData = new TreeMap<>();
            temperatureData.put("6AM", 65.0);
            temperatureData.put("7AM", 70.0);
            temperatureData.put("8AM", 75.0);
            temperatureData.put("12PM", 78.0);
            temperatureData.put("6PM", 64.0);
            temperatureData.put("9PM", 63.0);

            ChartMaker chartMaker = new ChartMaker(this, findViewById(R.id.chartContainer));
            chartMaker.makeChart(temperatureData, System.currentTimeMillis());

        } catch (JSONException e) {
            Log.e("WeatherApp", "Error parsing weather data", e);
            showErrorDialog("Weather Data Error", "Failed to parse the weather data. Please try again later.");

        }
    }
    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private TreeMap<String, Double> parseHourlyTemperatureData(JSONObject currentDay) throws JSONException {
        TreeMap<String, Double> temperatureData = new TreeMap<>();
        JSONArray hoursArray = currentDay.getJSONArray("hours");

        for (int i = 0; i < hoursArray.length(); i++) {
            JSONObject hour = hoursArray.getJSONObject(i);
            String time = hour.getString("datetime");
            double temp = hour.getDouble("temp");

            if (isFutureHour(time)) {
                temperatureData.put(time, temp);
            }
        }
        return temperatureData;
    }

    private boolean isFutureHour(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date hourDate = sdf.parse(time);

            long currentTimeMillis = System.currentTimeMillis();

            return hourDate != null && hourDate.getTime() > currentTimeMillis;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void updateWeatherUI(JSONObject currentDay, String resolvedAddress, String unit) throws JSONException {
        double temp = currentDay.getDouble("temp");
        double feelsLike = currentDay.getDouble("feelslike");
        String iconCode = currentDay.getString("icon");
        String weatherDescription = currentDay.getString("conditions");
        String cloudCover = currentDay.getString("cloudcover");

        long sunsetEpoch = currentDay.getLong("sunsetEpoch");
        long sunriseEpoch = currentDay.getLong("sunriseEpoch");

        String sunsetTime = formatEpochToTime(sunsetEpoch);
        String sunriseTime = formatEpochToTime(sunriseEpoch);

        TextView sunriseTextView = findViewById(R.id.sunrise);
        TextView sunsetTextView = findViewById(R.id.sunset);
        sunriseTextView.setText("Sunrise: " + sunriseTime);
        sunsetTextView.setText("Sunset: " + sunsetTime);

        double windSpeed = currentDay.getDouble("windspeed");
        double windDirection = currentDay.getDouble("winddir");
        String windDirectionStr = getWindDirection(windDirection);
        String windDetails = String.format("Wind: %s at %.1f mph", windDirectionStr, windSpeed);

        int humidity = currentDay.getInt("humidity");
        int uvIndex = currentDay.getInt("uvindex");
        int visibility = currentDay.getInt("visibility");

        humidityTextView.setText(String.format("Humidity: %d%%", humidity));
        uvIndexTextView.setText(String.format("UV Index: %d", uvIndex));
        visibilityTextView.setText(String.format("Visibility: %d miles", visibility));
        weatherDescriptionTextView.setText(weatherDescription);
        cloudCoverTextView.setText(String.format("Cloud Cover: %s%%", cloudCover));
        windTextView.setText(windDetails);

        setGradientBackground(temp, unit);
        setWeatherIcon(iconCode);

        if (isCelsius) {
            temp = (temp - 32) * 5 / 9;
            feelsLike = (feelsLike - 32) * 5 / 9;
            temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f°C", temp));
            feelsLikeTextView.setText(String.format(Locale.getDefault(), "Feels like %.1f°C", feelsLike));
        } else {
            temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f°F", temp));
            feelsLikeTextView.setText(String.format(Locale.getDefault(), "Feels like %.1f°F", feelsLike));
        }
    }

    private String formatEpochToTime(long epochTime) {
        Date date = new Date(epochTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    private void setWeatherIcon(String iconCode) {
        int iconResId = getResources().getIdentifier(iconCode, "drawable", getPackageName());
        weatherIcon.setImageResource(iconResId);
    }

    private String getWindDirection(double degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) ((degrees + 11.25) / 22.5);
        return directions[index % 16];
    }

    private void setGradientBackground(double temperature, String unit) {

        /*if (isCelsius) {
            //temperature = (temperature * 9 / 5) + 32;
            temperature = (temperature - 32) * 5 / 9;  // Convert Fahrenheit to Celsius
            ColorMaker.setColorGradient(getWindow().getDecorView(), temperature, unit);

        }
        //else {
            //temperature = (temperature * 9 / 5) + 32;
            //temperature = (temperature - 32) * 5 / 9;
        //}
        else*/
        if(!isCelsius) ColorMaker.setColorGradient(getWindow().getDecorView(), temperature, unit);
    }


    private String getCityFromResolvedAddress(String resolvedAddress) {
        String city = resolvedAddress.split(",")[0].trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        String currentDate = dateFormat.format(new Date());

        return city + ", " + currentDate.toLowerCase(Locale.getDefault());
    }

    private List<HourlyForecast> parseHourlyForecast(JSONArray hours, TimeZone timeZone) throws JSONException {
        List<HourlyForecast> forecasts = new ArrayList<>();

        for (int i = 0; i < hours.length(); i++) {
            JSONObject hour = hours.getJSONObject(i);

            long epoch = hour.optLong("epoch", 0);
            Date date = new Date(epoch * 1000);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

            dayFormat.setTimeZone(timeZone);
            timeFormat.setTimeZone(timeZone);

            String dayLabel = DateUtils.isToday(date.getTime()) ? "Today" : dayFormat.format(date);
            String time = timeFormat.format(date);
            // Log the current hour's data for debugging
            Log.d("WeatherApp", "Hour data: " + hour.toString());


            String icon = hour.optString("icon", "default_icon");
            double temp = hour.optDouble("temp", 0.0);

            if (isCelsius) {
                temp = (temp - 32) * 5 / 9;
            }
            String conditions = hour.optString("conditions", "No conditions available");

            forecasts.add(new HourlyForecast(dayLabel, time, icon, temp, conditions, isCelsius ? "C" : "F"));
        }
        return forecasts;
    }

    private void getDeviceLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String resolvedAddress = address.getLocality();

                                cityNameTextView.setText(resolvedAddress);

                                fetchWeatherData(resolvedAddress, currentUnit);
                            } else {
                                Toast.makeText(MainActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e("WeatherApp", "Error getting address: " + e.getMessage());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    private void showNoNetworkMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Network Error")
                .setMessage("No network connection. Please check your internet settings.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE);
    }

}
