package engineering.griffith.edu.lnd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {


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


        /**
         * Draw the LND Level chart
         */
        PieChart lndPieChart = (PieChart) findViewById(R.id.activity_result_LND_chart);

        ArrayList<PieEntry> lndEntries = new ArrayList<>();
        lndEntries.add(new PieEntry((float)LND_Level[0], "Low", 0));
        lndEntries.add(new PieEntry((float)LND_Level[1], "Medium", 1));
        lndEntries.add(new PieEntry((float)LND_Level[2], "High", 2));

        PieDataSet lndDataset = new PieDataSet(lndEntries, "# of Calls");

        PieData lndData = new PieData();
        lndData.setDataSet(lndDataset);
        lndDataset.setColors(ColorTemplate.COLORFUL_COLORS); //

        lndPieChart.setCenterText("LND Level");
        lndPieChart.setData(lndData);

        lndPieChart.animateY(2000);

        lndPieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image


        /**
         * Draw the Spill_risk chart
         */
        PieChart spillPieChart = (PieChart) findViewById(R.id.activity_result_spill_chart);

        ArrayList<PieEntry> spillEntries = new ArrayList<>();
        spillEntries.add(new PieEntry((float)SPILL_RISK[0], "Low", 0));
        spillEntries.add(new PieEntry((float)SPILL_RISK[1], "Medium", 1));
        spillEntries.add(new PieEntry((float)SPILL_RISK[2], "High", 2));

        PieDataSet spillDataset = new PieDataSet(spillEntries, "# of Calls");

        PieData spillData = new PieData();
        spillData.setDataSet(lndDataset);
        spillDataset.setColors(ColorTemplate.COLORFUL_COLORS); //

        spillPieChart.setCenterText("LND Level");
        spillPieChart.setData(spillData);

        spillPieChart.animateY(2000);

        spillPieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image


    }
}
