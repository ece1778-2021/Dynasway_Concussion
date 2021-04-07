package com.example.dynaswayconcussion.ui.calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.Tests.DynamicTest.camera.CameraActivity;
import com.example.dynaswayconcussion.Utils.DateUtils;
import com.example.dynaswayconcussion.ui.home.TeamsConnectionFragment;
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
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CalendarFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    BarChart barChartStatic;
    BarChart barChartDynamic;

    double[] baselineStatic = new double[5];
    double[] resultsStatic = new double[5];
    double[] baselineDynamic = new double[5];
    double[] resultsDynamic = new double[5];

    long[] baselineStaticTimestamps = new long[5];
    long[] resultsStaticTimestamps = new long[5];
    long[] baselineDynamicTimestamps = new long[5];
    long[] resultsDynamicTimestamps = new long[5];

    public CalendarFragment() {

    }

    public static CalendarFragment newInstance(String uid)
    {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null)
        {
            uid = getArguments().getString("uid");
        }
        else
        {
            uid = mAuth.getCurrentUser().getUid();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        CalendarView calendarView = view.findViewById(R.id.calendarView);
        barChartStatic = view.findViewById(R.id.barChartStatic);
        barChartDynamic = view.findViewById(R.id.barChartDynamic);

        initBarChart(barChartStatic);
        initBarChart(barChartDynamic);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                getTestResultsForDay(year, month + 1, dayOfMonth);
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        String day = (String) DateFormat.format("dd",   currentTime); // 20
        String monthNumber = (String) DateFormat.format("MM",   currentTime); // 06
        String year = (String) DateFormat.format("yyyy", currentTime); // 2013
        getTestResultsForDay(Integer.parseInt(year), Integer.parseInt(monthNumber), Integer.parseInt(day));
        return view;
    }

    private void UpdateBarChart(BarChart barChart, double[] baseline, double[] results)
    {
        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(500);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        barChart.animateX(500);

        //List<Double> group1 = new ArrayList<>(); // baseline values
        //List<Double> group2 = new ArrayList<>(); // test values for the date

        // need this to account for the initial "empty" label for formatting purposes
        /*group1.add(0, 0.0);
        group2.add(0, 0.0);

        for (int i = 0; i < 4; i++)
        {
            group1.add(rand.nextDouble() + 4);
            group2.add(rand.nextDouble() + 5);
        }*/

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();

        // fill the lists
        for(int i = 0; i < 5; i++) {
            entriesGroup1.add(new BarEntry(i, (float) baseline[i]));
            entriesGroup2.add(new BarEntry(i, (float) results[i]));
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
        Log.i("CALENDAR_INFO", "Inputted year: " + year);
        Log.i("CALENDAR_INFO", "Inputted month: " + month);
        Log.i("CALENDAR_INFO", "Inputted day: " + dayOfMonth);

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

        Log.i("CALENDAR_INFO", "Start time in millis: " + startTimeMillis);
        Log.i("CALENDAR_INFO", "End time in millis: " + endTimeMillis);

        for (int i = 0; i < 5; i++) {
            baselineStatic[i] = 0.0;
            resultsStatic[i] = 0.0;
            baselineDynamic[i] = 0.0;
            resultsDynamic[i] = 0.0;
            baselineStaticTimestamps[i] = -1;
            resultsStaticTimestamps[i] = -1;
            baselineDynamicTimestamps[i] = -1;
            resultsDynamicTimestamps[i] = -1;
        }

        CollectionReference testsRef = db.collection("test_results");

        Query timeQuery = testsRef.whereGreaterThan("timestamp", startTimeMillis).whereLessThan("timestamp", endTimeMillis);
        timeQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = 0;
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Log.i("CALENDAR_INFO", document.getId() + " => " + document.getData());
                    double testResult = document.getDouble("value");
                    boolean isBaseline = document.getBoolean("is_baseline");
                    boolean isFromUser = document.getString("user_uid").equals(uid);
                    String testType = document.getString("test_type");
                    long timestamp = document.getLong("timestamp");
                    Log.i("CALENDAR_INFO", "Test string type: " + testType);
                    if (!isBaseline && isFromUser) {
                        boolean correct = setResultValue(testResult, timestamp, testType);
                        if (!correct) {
                            Toast.makeText(getActivity(), "Error loading part of the data for the day (Errno: 3).",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            count++;
                        }
                    }
                }
                if (count > 0) {
                    testsRef.whereEqualTo("is_baseline", true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.i("CALENDAR_INFO", document.getId() + " => " + document.getData());
                                double testResult = document.getDouble("value");
                                boolean isBaseline = document.getBoolean("is_baseline");
                                boolean isFromUser = document.getString("user_uid").equals(uid);
                                String testType = document.getString("test_type");
                                long timestamp = document.getLong("timestamp");
                                if (isBaseline && isFromUser) {
                                    boolean correct = setBaselineValue(testResult, timestamp, testType);
                                    if (!correct) {
                                        Toast.makeText(getActivity(), "Error loading part of the data for the day (Errno: 5).",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            UpdateBarChart(barChartStatic, baselineStatic, resultsStatic);
                            UpdateBarChart(barChartDynamic, baselineDynamic, resultsDynamic);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error loading data for the day (Errno: 4).",
                                    Toast.LENGTH_SHORT).show();
                            Log.i("CALENDAR_INFO", e.toString());
                            UpdateBarChart(barChartStatic, baselineStatic, resultsStatic);
                            UpdateBarChart(barChartDynamic, baselineDynamic, resultsDynamic);
                        }
                    });
                }
                else {
                    //UpdateBarChart(barChartStatic, baselineStatic, resultsStatic);
                    //UpdateBarChart(barChartDynamic, baselineDynamic, resultsDynamic);
                    barChartStatic.invalidate();
                    barChartStatic.clear();
                    barChartDynamic.invalidate();
                    barChartDynamic.clear();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error loading data for the day (Errno: 2).",
                        Toast.LENGTH_SHORT).show();
                Log.i("CALENDAR_INFO", e.toString());
            }
        });
    }

    private boolean setResultValue(double value, long timestamp, String testTypeID) {
        final String REGULAR_STANDING_STRING = getResources().getString(R.string.static_test_regular_constant);
        final String TANDEM_STANDING_STRING = getResources().getString(R.string.static_test_tandem_constant);
        final String REGULAR_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_regular_dual_task_constant);
        final String TANDEM_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_tandem_dual_task_constant);

        final String REGULAR_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_regular_constant);
        final String TANDEM_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_tandem_constant);
        final String REGULAR_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_regular_dual_task_constant);
        final String TANDEM_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_tandem_dual_task_constant);

        boolean isTestTypeCorrect = true;
        if (testTypeID.equals(REGULAR_STANDING_STRING)) {
            if (timestamp > resultsStaticTimestamps[1]) {
                resultsStatic[1] = value;
                resultsStaticTimestamps[1] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_STANDING_STRING)) {
            if (timestamp > resultsStaticTimestamps[2]) {
                resultsStatic[2] = value;
                resultsStaticTimestamps[2] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_STANDING_COGNITIVE_STRING)) {
            if (timestamp > resultsStaticTimestamps[3]) {
                resultsStatic[3] = value;
                resultsStaticTimestamps[3] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_STANDING_COGNITIVE_STRING)) {
            if (timestamp > resultsStaticTimestamps[4]) {
                resultsStatic[4] = value;
                resultsStaticTimestamps[4] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_DYNAMIC_STRING)) {
            if (timestamp > resultsDynamicTimestamps[1]) {
                resultsDynamic[1] = value * 31.0 * 28.75 / 100.0;
                resultsDynamicTimestamps[1] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_DYNAMIC_STRING)) {
            if (timestamp > resultsDynamicTimestamps[2]) {
                resultsDynamic[2] = value * 31.0 * 28.75 / 100.0;
                resultsDynamicTimestamps[2] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_DYNAMIC_COGNITIVE_STRING)) {
            if (timestamp > resultsDynamicTimestamps[3]) {
                resultsDynamic[3] = value * 31.0 * 28.75 / 100.0;
                resultsDynamicTimestamps[3] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_DYNAMIC_COGNITIVE_STRING)) {
            if (timestamp > resultsDynamicTimestamps[4]) {
                resultsDynamic[4] = value * 31.0 * 28.75 / 100.0;
                resultsDynamicTimestamps[4] = timestamp;
            }
        }
        else {
            isTestTypeCorrect = false;
        }
        return isTestTypeCorrect;
    }

    private boolean setBaselineValue(double value, long timestamp, String testTypeID) {
        final String REGULAR_STANDING_STRING = getResources().getString(R.string.static_test_regular_constant);
        final String TANDEM_STANDING_STRING = getResources().getString(R.string.static_test_tandem_constant);
        final String REGULAR_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_regular_dual_task_constant);
        final String TANDEM_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_tandem_dual_task_constant);

        final String REGULAR_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_regular_constant);
        final String TANDEM_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_tandem_constant);
        final String REGULAR_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_regular_dual_task_constant);
        final String TANDEM_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_tandem_dual_task_constant);
        boolean isTestTypeCorrect = true;
        if (testTypeID.equals(REGULAR_STANDING_STRING)) {
            if (timestamp > baselineStaticTimestamps[1]) {
                baselineStatic[1] = value;
                baselineStaticTimestamps[1] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_STANDING_STRING)) {
            if (timestamp > baselineStaticTimestamps[2]) {
                baselineStatic[2] = value;
                baselineStaticTimestamps[2] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_STANDING_COGNITIVE_STRING)) {
            if (timestamp > baselineStaticTimestamps[3]) {
                baselineStatic[3] = value;
                baselineStaticTimestamps[3] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_STANDING_COGNITIVE_STRING)) {
            if (timestamp > baselineStaticTimestamps[4]) {
                baselineStatic[4] = value;
                baselineStaticTimestamps[4] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_DYNAMIC_STRING)) {
            if (timestamp > baselineDynamicTimestamps[1]) {
                baselineDynamic[1] = value * 31.0 * 28.75 / 100.0;
                baselineDynamicTimestamps[1] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_DYNAMIC_STRING)) {
            if (timestamp > baselineDynamicTimestamps[2]) {
                baselineDynamic[2] = value * 31.0 * 28.75 / 100.0;
                baselineDynamicTimestamps[2] = timestamp;
            }
        }
        else if (testTypeID.equals(REGULAR_DYNAMIC_COGNITIVE_STRING)) {
            if (timestamp > baselineDynamicTimestamps[3]) {
                baselineDynamic[3] = value * 31.0 * 28.75 / 100.0;
                baselineDynamicTimestamps[3] = timestamp;
            }
        }
        else if (testTypeID.equals(TANDEM_DYNAMIC_COGNITIVE_STRING)) {
            if (timestamp > baselineDynamicTimestamps[4]) {
                baselineDynamic[4] = value * 31.0 * 28.75 / 100.0;
                baselineDynamicTimestamps[4] = timestamp;
            }
        }
        else {
            isTestTypeCorrect = false;
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