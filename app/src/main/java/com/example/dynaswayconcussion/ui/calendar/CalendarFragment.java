package com.example.dynaswayconcussion.ui.calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.Tests.DynamicTest.camera.CameraActivity;
import com.example.dynaswayconcussion.Utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class CalendarFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    BarChart barChartStatic;
    BarChart barChartDynamic;

    Random rand = new Random();

    double staticRegularResult = 0.0;
    double staticTandemResult = 0.0;
    double staticRegularCognitiveResult = 0.0;
    double staticTandemCognitiveResult = 0.0;

    double dynamicRegularResult = 0.0;
    double dynamicTandemResult = 0.0;
    double dynamicRegularCognitiveResult = 0.0;
    double dynamicTandemCognitiveResult = 0.0;

    double staticBaselineRegular = 0.0;
    double staticBaselineTandem = 0.0;
    double staticBaselineRegularCognitive = 0.0;
    double staticBaselineTandemCognitive = 0.0;

    double dynamicBaselineRegular = 0.0;
    double dynamicBaselineTandem = 0.0;
    double dynamicBaselineRegularCognitive = 0.0;
    double dynamicBaselineTandemCognitive = 0.0;

    List<Double> baselineStatic = new ArrayList<>();
    List<Double> resultsStatic = new ArrayList<>();
    List<Double> baselineDynamic = new ArrayList<>();
    List<Double> resultsDynamic = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                getTestResultsForDay(year, month, dayOfMonth);

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

        // need this to account for the initial "empty" label for formatting purposes
        group1.add(0.0);
        group2.add(0.0);

        for (int i = 0; i < 5; i++)
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

        float groupSpace = 0.26f;
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
        barChart.setPinchZoom(false);
        barChart.getDescription().setEnabled(false);

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

        String[] labels = {"", "Regular", "Tandem", "Regular Dual", "Tandem Dual", ""};
        ValueFormatter xAxisFormatter = new LabelFormatter(barChart, labels);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(8);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisMinimum(1f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setAxisMaximum(labels.length - 1.1f);


//        //change the position of x-axis to the bottom
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        //set the horizontal distance of the grid line
//        xAxis.setGranularity(1f);
//        //hiding the x-axis line, default true if not set
//        xAxis.setDrawAxisLine(false);
//        //hiding the vertical grid lines, default true if not set
//        xAxis.setDrawGridLines(false);



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
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);

    }

    private void getTestResultsForDay(int year, int month, int dayOfMonth) {
        String dateFormatted = year + "-";
        if (month < 10) {
            dateFormatted += "0" + month + "-";
        }
        else {
            dateFormatted += month + "-";
        }

        if (dayOfMonth < 10) {
            dateFormatted += "0" + dayOfMonth;
        }
        else {
            dateFormatted += dayOfMonth;
        }
        long startTimeMillis = -1;
        long endTimeMillis = -1;
        try {
            startTimeMillis = DateUtils.getStartOfDayInMillis(dateFormatted);
            endTimeMillis = DateUtils.getEndOfDayInMillis(dateFormatted);
        }
        catch (ParseException pe) {
            Toast.makeText(getActivity(), "Error loading data for the day (Errno: 1).",
                    Toast.LENGTH_SHORT).show();
        }

        baselineStatic.clear();
        baselineDynamic.clear();
        resultsStatic.clear();
        resultsDynamic.clear();
        CollectionReference testsRef = db.collection("test_results");

        Query timeQuery = testsRef.whereGreaterThan("timestamp", startTimeMillis).whereLessThan("timestamp", endTimeMillis).whereEqualTo("user_uid", mAuth.getCurrentUser().getUid());
        timeQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Log.d("CALENDAR_INFO", document.getId() + " => " + document.getData());
                    double testResult = document.getDouble("value");
                    boolean isBaseline = document.getBoolean("is_baseline");
                    String testType = document.getString("test_type");
                    int testTypeID = getResId(testType, R.string.class);
                    if (!isBaseline) {
                        boolean correct = setResultValue(testResult, testTypeID);
                        if (!correct) {
                            Toast.makeText(getActivity(), "Error loading part of the data for the day (Errno: 3).",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    //TODO: call update graphs method with information
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error loading data for the day (Errno: 2).",
                        Toast.LENGTH_SHORT).show();
            }
        });

        testsRef.whereEqualTo("is_baseline", true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Log.d("CALENDAR_INFO", document.getId() + " => " + document.getData());
                    double testResult = document.getDouble("value");
                    boolean isBaseline = document.getBoolean("is_baseline");
                    String testType = document.getString("test_type");
                    int testTypeID = getResId(testType, R.string.class);
                    if (isBaseline) {
                        boolean correct = setBaselineValue(testResult, testTypeID);
                        if (!correct) {
                            Toast.makeText(getActivity(), "Error loading part of the data for the day (Errno: 5).",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    //TODO: call update graphs method with information
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error loading data for the day (Errno: 4).",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean setResultValue(double value, int testTypeID) {
        boolean isTestTypeCorrect = true;
        switch (testTypeID) {
            case R.string.static_test_regular_constant:
                staticRegularResult = value;
                break;
            case R.string.static_test_tandem_constant:
                staticTandemResult = value;
                break;
            case R.string.static_test_regular_dual_task_constant:
                staticRegularCognitiveResult = value;
                break;
            case R.string.static_test_tandem_dual_task_constant:
                staticTandemCognitiveResult = value;
                break;
            case R.string.dynamic_test_regular_constant:
                dynamicRegularResult = value;
                break;
            case R.string.dynamic_test_tandem_constant:
                dynamicTandemResult = value;
                break;
            case R.string.dynamic_test_regular_dual_task_constant:
                dynamicRegularCognitiveResult = value;
                break;
            case R.string.dynamic_test_tandem_dual_task_constant:
                dynamicTandemCognitiveResult = value;
                break;
            default:
                isTestTypeCorrect = false;
                break;
        }
        return isTestTypeCorrect;
    }

    private boolean setBaselineValue(double value, int testTypeID) {
        boolean isTestTypeCorrect = true;
        switch (testTypeID) {
            case R.string.static_test_regular_constant:
                staticBaselineRegular = value;
                break;
            case R.string.static_test_tandem_constant:
                staticBaselineTandem = value;
                break;
            case R.string.static_test_regular_dual_task_constant:
                staticBaselineRegularCognitive = value;
                break;
            case R.string.static_test_tandem_dual_task_constant:
                staticBaselineTandemCognitive = value;
                break;
            case R.string.dynamic_test_regular_constant:
                dynamicBaselineRegular = value;
                break;
            case R.string.dynamic_test_tandem_constant:
                dynamicBaselineTandem = value;
                break;
            case R.string.dynamic_test_regular_dual_task_constant:
                dynamicBaselineRegularCognitive = value;
                break;
            case R.string.dynamic_test_tandem_dual_task_constant:
                dynamicBaselineTandemCognitive = value;
                break;
            default:
                isTestTypeCorrect = false;
                break;
        }
        return isTestTypeCorrect;
    }

    private class LabelFormatter extends ValueFormatter {

        String[] labels;
        BarLineChartBase<?> chart;

        LabelFormatter(BarLineChartBase<?> chart, String[] labels) {
            this.chart = chart;
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            return labels[(int) value];
        }
    }
}