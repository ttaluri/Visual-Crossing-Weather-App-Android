package org.ttaluri.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private List<HourlyForecast> hourlyForecastList;
    private Context context;
    private String city;
    boolean isCelcius = false;

    public HourlyWeatherAdapter(Context context, List<HourlyForecast> hourlyForecastList, String city) {
        this.hourlyForecastList = hourlyForecastList;
        this.context = context;
        this.city = city;
    }

    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_forecast, parent, false);
        return new HourlyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourlyWeatherViewHolder holder, int position) {
        HourlyForecast forecast = hourlyForecastList.get(position);

        String formattedTime = String.format("%s\n%s", forecast.getDayLabel(), forecast.getFormattedTime());
        holder.time.setText(formattedTime);
        holder.time.setGravity(Gravity.CENTER);

        holder.itemView.setBackground(getGradientBackground(forecast.getTemperature(), isCelcius));
        holder.temperature.setText(String.format(Locale.getDefault(), "%.1f°%s", forecast.getTemperature(), forecast.getUnit()));

        holder.description.setText(forecast.getDescription() != null ? forecast.getDescription() : "N/A");

        String icon = forecast.getWeatherIcon();
        int iconResource = getIconResource(icon);
        if (iconResource != 0) {
            holder.weatherIcon.setImageResource(iconResource);
        } else {
            Picasso.get()
                    .load(getIconUrl(icon))
                    .placeholder(R.drawable.partly_cloudy_day)
                    .into(holder.weatherIcon);
        }
    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView time, temperature, description;
        ImageView weatherIcon, unit;

        public HourlyWeatherViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            temperature = itemView.findViewById(R.id.temperature);
            description = itemView.findViewById(R.id.description);
            unit = itemView.findViewById(R.id.unitc);
        }
    }

    private int getIconResource(String iconName) {
        if (iconName != null) {
            iconName = iconName.replace("-", "_");
            return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        }
        return 0;
    }

    private String getIconUrl(String iconName) {
        return "https://example.com/icons/" + iconName + ".png";
    }

    private Drawable getGradientBackground(double temperature, boolean isCelcius) {
        int startColor = Color.parseColor("#F6196D1C"),
        endColor = Color.parseColor("#F6548E57");

        if (isCelcius) {
            if (temperature <= 0) {
                startColor = Color.parseColor("#0000FF"); // Cold - Blue
                endColor = Color.parseColor("#ADD8E6"); // Light Blue
            } else if (temperature <= 20) {
                startColor = Color.parseColor("#F6196D1C");
                endColor = Color.parseColor("#F6548E57"); // Light Green
            } else {
                startColor = Color.parseColor("#FF4500"); // Hot - Orange
                endColor = Color.parseColor("#FFA07A");
            }
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(8);
        return gradientDrawable;
    }


    public void updateHourlyForecastList(List<HourlyForecast> newHourlyForecastList) {
        this.hourlyForecastList.clear();
        this.hourlyForecastList.addAll(newHourlyForecastList);
        notifyDataSetChanged();
    }
}
