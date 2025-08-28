package org.ttaluri.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder> {

    private Context context;
    private List<DailyForecast> dailyForecastList;
    boolean isCelcius = false;

    public DailyForecastAdapter(Context context, List<DailyForecast> dailyForecastList) {
        this.context = context;
        this.dailyForecastList = dailyForecastList;
    }

    @Override
    public DailyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_forecast, parent, false);
        return new DailyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyForecastViewHolder holder, int position) {
        DailyForecast forecast = dailyForecastList.get(position);

        holder.dayDate.setText(forecast.getFormattedDate());
        holder.highLowTemp.setText(String.format(Locale.getDefault(), "H: %.1f°C / L: %.1f°C", forecast.getTempMax(), forecast.getTempMin()));
        holder.description.setText(forecast.getDescription());
        holder.precipProb.setText(String.format(Locale.getDefault(), "Precip: %.0f%%", forecast.getPrecipProb()));
        holder.uvIndex.setText(String.format(Locale.getDefault(), "UV: %d", forecast.getUvIndex()));
        holder.morningTemp.setText(String.format(Locale.getDefault(), "%.1f°C", forecast.getMorningTemp()));
        holder.afternoonTemp.setText(String.format(Locale.getDefault(), "%.1f°C", forecast.getAfternoonTemp()));
        holder.eveningTemp.setText(String.format(Locale.getDefault(), "%.1f°C", forecast.getEveningTemp()));
        holder.nightTemp.setText(String.format(Locale.getDefault(), "%.1f°C", forecast.getNightTemp()));

        String iconUrl = "https://example.com/icons/" + forecast.getIcon() + ".png";
        Picasso.get().load(iconUrl).placeholder(R.drawable.partly_cloudy_day).into(holder.weatherIcon);

        holder.itemView.setBackground(getGradientBackground(forecast.getTempMax(), isCelcius));
    }

    @Override
    public int getItemCount() {
        return dailyForecastList.size();
    }

    public static class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dayDate, highLowTemp, description, precipProb, uvIndex, morningTemp, afternoonTemp, eveningTemp, nightTemp;
        ImageView weatherIcon;

        public DailyForecastViewHolder(View itemView) {
            super(itemView);
            dayDate = itemView.findViewById(R.id.dayDate);
            highLowTemp = itemView.findViewById(R.id.highLowTemp);
            description = itemView.findViewById(R.id.description);
            precipProb = itemView.findViewById(R.id.precipProb);
            uvIndex = itemView.findViewById(R.id.uvIndex);
            morningTemp = itemView.findViewById(R.id.morningTemp);
            afternoonTemp = itemView.findViewById(R.id.afternoonTemp);
            eveningTemp = itemView.findViewById(R.id.eveningTemp);
            nightTemp = itemView.findViewById(R.id.nightTemp);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }

    private Drawable getGradientBackground(double temperature, boolean isCelcius) {
        int startColor = Color.parseColor("#F6196D1C"),
        endColor = Color.parseColor("#F6548E57");

        if(isCelcius){
            if (temperature <= 0) {
                startColor = Color.parseColor("#0000FF"); // Cold - Blue
                endColor = Color.parseColor("#ADD8E6"); // Light Blue
            } else if (temperature <= 20) {
                startColor = Color.parseColor("#F6196D1C");
                endColor = Color.parseColor("#F6548E57");
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
}
