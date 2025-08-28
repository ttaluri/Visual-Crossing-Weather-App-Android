package org.ttaluri.weatherapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class ColorMaker {

    public static void setColorGradient(View view, double temperature, String tempUnit) {
        int startColor;
        int endColor;

        /*if (temperature <= 0) {
            startColor = Color.parseColor("#0000FF"); // Cold - Blue
            endColor = Color.parseColor("#ADD8E6"); // Light Blue
        } else if (temperature <= 20) {
            startColor = Color.parseColor("#00FF00"); // Mild - Green
            endColor = Color.parseColor("#ADFF2F"); // Light Green
        } else {
            startColor = Color.parseColor("#FF4500"); // Hot - Orange
            endColor = Color.parseColor("#FFA07A"); // Light Orange
        }*/

        if (temperature <= 32) { // Freezing
            startColor = Color.parseColor("#0000FF");
            endColor = Color.parseColor("#ADD8E6");
        } else if (temperature <= 60) { // Cool to mild
            startColor = Color.parseColor("#F6196D1C");
            endColor = Color.parseColor("#F6548E57");
        } else if (temperature <= 80) { // Warm
            startColor = Color.parseColor("#FFD700");
            endColor = Color.parseColor("#FFA500");
        } else { // Hot
            startColor = Color.parseColor("#FF4500");
            endColor = Color.parseColor("#FF6347");
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );

        view.setBackground(gradientDrawable);
    }
}
