package engineering.griffith.edu.lnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    class LikelyHoodPair{
        public double likelyHood;
        public int count;
    }


    private double[]  LNDFIN, LND_Level, SPILL_RISK;
    private double AverageWTP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();

        LNDFIN = intent.getDoubleArrayExtra("LNDFIN");
        LND_Level = intent.getDoubleArrayExtra("LND_Level");
        SPILL_RISK = intent.getDoubleArrayExtra("SPILL_RISK");
        AverageWTP = intent.getDoubleExtra("AverageWTP", 0);



        // private avg intake
        TextView avgTv =  (TextView) findViewById(R.id.activity_result_avg_intake);
        avgTv.setText(AverageWTP + "");


        /**
         * First we need to compute the count for each probability
         * And then load the data for the chart
         */
        ArrayList<LikelyHoodPair> likelyHoods = new ArrayList<LikelyHoodPair>();
        for(int i = 0 ; i < 100; i++){
            LikelyHoodPair newPair = new LikelyHoodPair();
            newPair.likelyHood = 120 + i * 0.5;
            newPair.count = 0;
            likelyHoods.add(newPair);
        }

        BarChart barChart = (BarChart) findViewById(R.id.activity_result_bar_chart);


        // Set the bar char data
        for(int i = 0 ; i < LNDFIN.length; i++){
            int likelyHoodIndex = (int)((LNDFIN[i] - 120) / 0.5);
            if(likelyHoodIndex >= 100 || likelyHoodIndex < 0)
            {
                continue;
            }
            likelyHoods.get(likelyHoodIndex).count ++;
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for(int i = 0 ; i < 100; i++){
            barEntries.add(new BarEntry((float)likelyHoods.get(i).likelyHood, likelyHoods.get(i).count));
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "");

        // set x and y axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ColorTemplate.getHoloBlue());
        xAxis.setEnabled(true);
        xAxis.disableGridDashedLine();
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        // - Y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(1000f);
        leftAxis.setAxisMinValue(0f); // to set minimum yAxis
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawGridLines(true);
        barChart.getAxisRight().setEnabled(false);


        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        // display the bar chart
        BarData data = new BarData(barDataset);
        barChart.setData(data);

        barChart.animateY(2000);
        barChart.invalidate(); // refresh


        /**
         * Draw the LND Level chart
         */
        PieChart lndPieChart = (PieChart) findViewById(R.id.activity_result_LND_chart);

        ArrayList<PieEntry> lndEntries = new ArrayList<>();
        lndEntries.add(new PieEntry((float)LND_Level[0], "LND below 158m", 0));
        lndEntries.add(new PieEntry((float)LND_Level[1], "LND Medium", 1));
        lndEntries.add(new PieEntry((float)LND_Level[2], "LND above 166m", 2));

        PieDataSet lndDataset = new PieDataSet(lndEntries, "");

        PieData lndData = new PieData();
        lndData.setDataSet(lndDataset);
        lndDataset.setColors(ColorTemplate.COLORFUL_COLORS); //

        lndPieChart.setCenterText("");
        lndPieChart.setData(lndData);

        lndPieChart.animateY(2000);
        lndPieChart.invalidate(); // refresh

        /**
         * Draw the Spill_risk chart
         */
        PieChart spillPieChart = (PieChart) findViewById(R.id.activity_result_spill_chart);

        ArrayList<PieEntry> spillEntries = new ArrayList<>();
        spillEntries.add(new PieEntry((float)SPILL_RISK[0], "No Spill", 0));
        spillEntries.add(new PieEntry((float)SPILL_RISK[1], "Medium Spill", 1));
        spillEntries.add(new PieEntry((float)SPILL_RISK[2], "High Spill", 2));

        PieDataSet spillDataset = new PieDataSet(spillEntries, "");

        PieData spillData = new PieData();
        spillData.setDataSet(spillDataset);
        spillDataset.setColors(ColorTemplate.COLORFUL_COLORS); //

        spillPieChart.setCenterText("");
        spillPieChart.setData(spillData);

        spillPieChart.animateY(2000);

        spillPieChart.invalidate(); // refresh
    }

    // back button clicked
    public void btnBackOnClick(View view) {
        onBackPressed();
    }

}
