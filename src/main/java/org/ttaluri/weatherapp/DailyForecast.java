package org.ttaluri.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyForecast {
    private long datetimeEpoch;
    private double tempMax;
    private double tempMin;
    private String description;
    private double precipProb;
    private int uvIndex;
    private String icon;
    private double morningTemp;
    private double afternoonTemp;
    private double eveningTemp;
    private double nightTemp;

    public DailyForecast(long datetimeEpoch, double tempMax, double tempMin, String description,
                         double precipProb, int uvIndex, String icon,
                         double morningTemp, double afternoonTemp, double eveningTemp, double nightTemp) {
        this.datetimeEpoch = datetimeEpoch;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.description = description;
        this.precipProb = precipProb;
        this.uvIndex = uvIndex;
        this.icon = icon;
        this.morningTemp = morningTemp;
        this.afternoonTemp = afternoonTemp;
        this.eveningTemp = eveningTemp;
        this.nightTemp = nightTemp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        return sdf.format(new Date(datetimeEpoch * 1000L));
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    public String getDescription() {
        return description;
    }

    public double getPrecipProb() {
        return precipProb;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public String getIcon() {
        return icon;
    }

    public double getMorningTemp() {
        return morningTemp;
    }

    public double getAfternoonTemp() {
        return afternoonTemp;
    }

    public double getEveningTemp() {
        return eveningTemp;
    }

    public double getNightTemp() {
        return nightTemp;
    }
}
