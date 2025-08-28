package org.ttaluri.weatherapp;

import java.util.List;

public class WeatherResponse {
    private String resolvedAddress;
    private CurrentConditions currentConditions;
    private List<Day> days;

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    public List<Day> getDays() {
        return days;
    }
}
