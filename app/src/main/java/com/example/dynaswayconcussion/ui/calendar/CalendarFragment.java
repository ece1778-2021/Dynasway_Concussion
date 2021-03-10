package com.example.dynaswayconcussion.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dynaswayconcussion.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CalendarFragment extends Fragment {

    BarChart barChartStatic;
    BarChart barChartDynamic;

    Random rand = new Random();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        barChartStatic = view.findViewById(R.id.barChartStatic);
        barChartDynamic = view.findViewById(R.id.barChartDynamic);

        initBarChart(barChartStatic);
        initBarChart(barChartDynamic);

        UpdateBarChart(barChartStatic);
        UpdateBarChart(barChartDynamic);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                UpdateBarChart(barChartStatic);
                UpdateBarChart(barChartDynamic);
            }
        });

        return view;
    }

    private void UpdateBarChart(BarChart barChart)
    {
        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(500);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        barChart.animateX(500);

        List<Double> group1 = new ArrayList<>();
        List<Double> group2 = new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            group1.add(rand.nextDouble() + 4);
            group2.add(rand.nextDouble() + 5);
        }

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();

        // fill the lists
        for(int i = 0; i < group1.size(); i++) {
            entriesGroup1.add(new BarEntry(i, group1.get(i).floatValue()));
            entriesGroup2.add(new BarEntry(i, group2.get(i).floatValue()));
        }

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Baseline");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Test");

        set1.setColor(getContext().getColor(R.color.chart_baseline));
        set2.setColor(getContext().getColor(R.color.chart_test));

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.35f; // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(data);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping

        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);

        barChart.invalidate(); // refresh
    }

    private void initBarChart(BarChart barChart){
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

//        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
//        barChart.animateY(1000);
//        //setting animation for x-axis, the bar will pop up separately within the time we set
//        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        final ArrayList<String> xLabel = new ArrayList<>();
        xLabel.add("Regular");
        xLabel.add("Tandem");
        xLabel.add("Regular-Dual");
        xLabel.add("Tandem-Dual");

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int) value);
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);

    }
}