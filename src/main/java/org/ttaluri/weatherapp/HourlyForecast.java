package org.ttaluri.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourlyForecast {
    private String dayLabel;
    private String time;
    private String weatherIcon;
    private double temperature;
    private String description;
    private String unit;

    public HourlyForecast(String dayLabel, String time, String weatherIcon, double temperature, String description, String unit) {
        this.dayLabel = dayLabel;
        this.time = time;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.description = description;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getDayLabel() {
        return dayLabel.length() > 3 ? dayLabel.substring(0, 3) : dayLabel;
    }

    public String getTime() {
        return time;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }


    public String getFormattedTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }
}
