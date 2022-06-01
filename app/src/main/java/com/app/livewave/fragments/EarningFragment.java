package com.app.livewave.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.AverageEarnings;
import com.app.livewave.models.ResponseModels.MonthlyEarnings;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import retrofit2.Response;

public class EarningFragment extends Fragment {

    BarChart barChart;
    PieChart pieChart;
    SwipeRefreshLayout swipe_to_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earning, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        getAverageEarnings();
        getMonthlyEarnings();
        swipe_to_refresh.setOnRefreshListener(() -> {
            getAverageEarnings();
            getMonthlyEarnings();
            swipe_to_refresh.setRefreshing(false);
        });
    }

    private void getMonthlyEarnings() {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getMonthlyEarnings(), getContext(), new ApiResponseHandlerWithFailure<MonthlyEarnings>() {
            @Override
            public void onSuccess(Response<ApiResponse<MonthlyEarnings>> data) {
                assert data.body() != null;
                if (data.body().getData() != null) {
                    setupBarChart();
                    loadBarChartData(data.body().getData());
                }
            }
            @Override
            public void onFailure(String failureCause) {
            }
        });
    }

    private void getAverageEarnings() {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getAverageEarnings(), getContext(), new ApiResponseHandlerWithFailure<AverageEarnings>() {
            @Override
            public void onSuccess(Response<ApiResponse<AverageEarnings>> data) {
                assert data.body() != null;
                if (data.body().getData() != null) {
                    setupPieChart();
                    loadPieChartData(data.body().getData());
                }
            }
            @Override
            public void onFailure(String failureCause) {
            }
        });
    }

    private void loadBarChartData(MonthlyEarnings month) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, month.getJanuary()));
        entries.add(new BarEntry(1, month.getFebruary()));
        entries.add(new BarEntry(2, month.getMarch()));
        entries.add(new BarEntry(3, month.getApril()));
        entries.add(new BarEntry(4, month.getMay()));
        entries.add(new BarEntry(5, month.getJune()));
        entries.add(new BarEntry(6, month.getJuly()));
        entries.add(new BarEntry(7, month.getAugust()));
        entries.add(new BarEntry(8, month.getSeptember()));
        entries.add(new BarEntry(9, month.getOctober()));
        entries.add(new BarEntry(10, month.getNovember()));
        entries.add(new BarEntry(11, month.getDecember()));

        BarDataSet dataSet = new BarDataSet(entries, "Transfers");
        int[] colors = new int[]{getResources().getColor(R.color.buttercup)};
        dataSet.setColors(colors);
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    private void setupBarChart() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Jan");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        xLabels.add("May");
        xLabels.add("Jun");
        xLabels.add("Jul");
        xLabels.add("Aug");
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Dec");
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }

    private void loadPieChartData(AverageEarnings data1) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (data1.getStreams() > 0)
            entries.add(new PieEntry(data1.getStreams(), "Streams"));
        if (data1.getEvents() > 0)
            entries.add(new PieEntry(data1.getEvents(), "EVENT"));
        if (data1.getTip() > 0)
            entries.add(new PieEntry(data1.getTip(), "TIP"));
        if (data1.getPosts() > 0)
            entries.add(new PieEntry(data1.getPosts(), "POST"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Transactions");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(2000, Easing.EaseInOutQuad);
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(8);
        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setCenterText("");
        pieChart.setCenterTextSize(16);


        pieChart.getDescription().setEnabled(false);
        Legend l = pieChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }
}