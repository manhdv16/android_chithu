package com.example.quanlichitieu.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class FragmentChart extends Fragment implements View.OnClickListener {

    private PieChart pieChart, pieChart1;
    private Firebase firebase;
    private LinearLayout lnDate;
    private TextView tvDate;
    private List<Transaction> list;
    private List<Transaction> list1= new ArrayList<>();
    private List<Transaction> list2= new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = new Firebase();
        pieChart = view.findViewById(R.id.pieChart);
        pieChart1 = view.findViewById(R.id.pieChart1);
        tvDate = view.findViewById(R.id.tvDate);
        lnDate = view.findViewById(R.id.lnDate);
        lnDate.setOnClickListener(this);
        String currentDate = tvDate.getText().toString();
        try {
            updateChart(currentDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateChart(String selectedDate) throws ParseException {
        firebase.getTransactionByDate(selectedDate, new Firebase.TransactionCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                list = transactions;
                setChart(list);
            }
            @Override
            public void onTransactionsError(DatabaseError databaseError) {

            }
        });
    }
    private void setChart(List<Transaction> list) {
        list1.clear();
        list2.clear();
        for(int i =0;i<list.size();i++){
            if(list.get(i).getStatus().equals("Chi tiêu")){
                list1.add(list.get(i));
            }else{
                list2.add(list.get(i));
            }
        }
        createPieChart(getView(), list1, pieChart);
        createPieChart(getView(), list2, pieChart1);

    }

    private void createPieChart(View view, List<Transaction> list,PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        float totalAmount = 0;
        for (Transaction t : list) {
            float amount = (float) t.getAmount();
            totalAmount += amount;
        }
        Map<String,Double> map = new TreeMap<>();
        for (Transaction t : list) {
            if(map.containsKey(t.getType())){
                double total = map.get(t.getType())+t.getAmount();
                map.put(t.getType(),total);
            }else{
                map.put(t.getType(),t.getAmount());
            }
        }
        List<PieEntryData> entryDataList = new ArrayList<>();
        for(String key : map.keySet()){
            double amount1 = map.get(key);
            float amount = (float) amount1;
            float percentage = (amount / totalAmount) * 100;
            String label = String.format("%s (%.1f%%)", key, percentage);
            entryDataList.add(new PieEntryData(amount, label, percentage));
        }
        entryDataList.sort((e1,e2) -> Float.compare(e2.percentage,e1.percentage));
        for (PieEntryData entryData : entryDataList) {
            entries.add(new PieEntry(entryData.amount, entryData.label));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Thông kê");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        setPieChart(dataSet, totalAmount,pieChart);
    }
    private void setPieChart(PieDataSet dataSet, float totalAmount,PieChart pieChart){
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(totalAmount / 1000000 + "tr");
        pieChart.animateXY(1000, 1000);
        pieChart.setDrawEntryLabels(false);
        pieChart.invalidate();

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setHoleRadius(40f);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setExtraOffsets(55, 0, 0, 0);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setFormSize(10f);
        l.setTextSize(10f);
        l.setTextColor(Color.BLUE);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setYOffset(60f);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
    }

    @Override
    public void onClick(View view) {
        if (view == lnDate) {
            new RackMonthPicker(getContext())
                    .setLocale(Locale.ENGLISH)
                    .setPositiveButton(new DateMonthDialogListener() {
                        @Override
                        public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                            String selectedDate = String.format("%d-%02d", year, month);
                            tvDate.setText(selectedDate);
                            try {
                                updateChart(selectedDate);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .setNegativeButton(new OnCancelMonthDialogListener() {
                        @Override
                        public void onCancel(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
    private static class PieEntryData {
        float amount;
        String label;
        float percentage;

        PieEntryData(float amount, String label, float percentage) {
            this.amount = amount;
            this.label = label;
            this.percentage = percentage;
        }
    }
}
