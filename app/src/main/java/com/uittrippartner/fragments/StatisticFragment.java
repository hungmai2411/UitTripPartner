package com.uittrippartner.fragments;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uittrippartner.R;
import com.uittrippartner.hotel.Booking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class StatisticFragment extends Fragment {

    HorizontalBarChart barChart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Long startDate, endDate;
    TextView txtDate;
    float numberBooked, numberCancelled;

    public StatisticFragment() {
        // Required empty public constructor
    }

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDate = view.findViewById(R.id.txtDate);
        barChart = view.findViewById(R.id.bar_chart);

        startDate = MaterialDatePicker.todayInUtcMilliseconds();

        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        endDate = gc.getTimeInMillis();

        Log.d("date-calender", String.valueOf(endDate) + "-" + DateFormat.format("dd/MM", new Date(endDate)).toString());

        String from = DateFormat.format("dd/MM", new Date(startDate)).toString();
        String end = DateFormat.format("dd/MM", new Date(endDate)).toString();

        SpannableString content = new SpannableString(from + " - " + end);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtDate.setText(content);

        db.collection("Hotels/" + 1428 + "/booked")
                .whereEqualTo("startDate", startDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String end =  DateFormat.format("dd/MM", new Date(document.getLong("endDate"))).toString();
                                String endTmp = DateFormat.format("dd/MM", new Date(endDate)).toString();

                                if (end.equals(endTmp)) {
                                    if (document.getString("status").equals("Booked"))
                                        numberBooked++;
                                    else
                                        numberCancelled++;
                                }
                            }

                            setChart(numberBooked, numberCancelled);
                        } else {
                        }
                    }
                });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                        MaterialDatePicker.Builder.dateRangePicker()
                                .setSelection(
                                        new Pair(
                                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                                MaterialDatePicker.todayInUtcMilliseconds()
                                        ))
                                .setTitleText("Chọn ngày")
                                .build();
                dateRangePicker.show(getChildFragmentManager(), "11");

                dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        startDate = selection.first;
                        endDate = selection.second;

                        Log.d("date", String.valueOf(endDate) + "-" + DateFormat.format("dd/MM", new Date(endDate)).toString());

                        String from = DateFormat.format("dd/MM", new Date(startDate)).toString();
                        String end = DateFormat.format("dd/MM", new Date(endDate)).toString();

                        SpannableString content = new SpannableString(from + " - " + end);
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        txtDate.setText(content);

                        numberBooked = 0;
                        numberCancelled = 0;

                        db.collection("Hotels/" + 1428 + "/booked")
                                .whereEqualTo("startDate", startDate)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String end =  DateFormat.format("dd/MM", new Date(document.getLong("endDate"))).toString();
                                                String endTmp = DateFormat.format("dd/MM", new Date(endDate)).toString();

                                                if (end.equals(endTmp)) {
                                                    if (document.getString("status").equals("Booked"))
                                                        numberBooked++;
                                                    else
                                                        numberCancelled++;
                                                }
                                            }

                                            setChart(numberBooked, numberCancelled);
                                        } else {
                                        }
                                    }
                                });
                    }
                });
            }
        });


    }

    void setChart(float booked, float cancelled) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        BarEntry barEntryBooked = new BarEntry(0, booked);
        barEntries.add(barEntryBooked);

        BarEntry barEntryCancelled = new BarEntry(1, cancelled);
        barEntries.add(barEntryCancelled);

        String[] labels = new String[]{"Đặt phòng", "Huỷ phòng"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Trạng thái");

        List<Integer> listColor = new ArrayList<Integer>() {
        };

        listColor.add(getResources().getColor(R.color.booked_text));
        listColor.add(getResources().getColor(R.color.cancelled_text));

        barDataSet.setColors(listColor);

        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(13f);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.getDescription().setText("");
        barChart.setData(barData);
        barChart.animateY(1000);
    }
}