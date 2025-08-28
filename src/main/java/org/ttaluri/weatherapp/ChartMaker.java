package org.ttaluri.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ChartMaker {

    private Context context;
    private LinearLayout chartContainer;

    public ChartMaker(Context context, LinearLayout chartContainer) {
        this.context = context;
        this.chartContainer = chartContainer;
    }

    public void makeChart(TreeMap<String, Double> temperatureData, long currentTimeMillis) {
        LineChart lineChart = new LineChart(context);
        chartContainer.removeAllViews();

        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Double> entry : temperatureData.entrySet()) {
            entries.add(new Entry(index++, entry.getValue().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);


        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawCircles(true);


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);


        Description description = new Description();
        description.setText("Temperature Trends");
        lineChart.setDescription(description);
        lineChart.invalidate();


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lineChart.setLayoutParams(params);


        chartContainer.addView(lineChart);
    }
}