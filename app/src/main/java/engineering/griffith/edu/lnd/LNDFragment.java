package engineering.griffith.edu.lnd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Hector on 22/6/17.
 */
public class LNDFragment extends Fragment {

    private Button button;


    private int currentMonth;
    private double LF;
    private double NF;
    private double HF;
    private double LNDLev;
    private double WTPIntake;



    private EditText lndLevelEt;
    private EditText forestLFEt;
    private EditText forestNFEt;
    private EditText forestHFEt;
    private EditText wtpIntakeEt;

    private Activity mainActivity;



    ProgressDialog mProgressDialog;
    private double lFData, nFData, hFData;



    public static LNDFragment newInstance() {
        LNDFragment lndFragment =
                new LNDFragment();
        return lndFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lnd, container, false);
        // Create an ArrayAdapter using the string array and a default spinner layout


        // Create an ArrayAdapter using the string array and a default spinner layout
        Spinner spinner = (Spinner) view.findViewById(R.id.activity_main_month_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new ItemSelectListener());

        // submit buttonß
        button = (Button) view.findViewById(R.id.activity_main_submit_btn);
        button.setOnClickListener(new ViewOnclickListener());


        lndLevelEt = (EditText) view.findViewById(R.id.activity_main_lnd_level_input_et);
        forestLFEt = (EditText) view.findViewById(R.id.activity_main_bom_forest_input_1_et);
        forestNFEt = (EditText) view.findViewById(R.id.activity_main_bom_forest_input_2_et);
        forestHFEt = (EditText) view.findViewById(R.id.activity_main_bom_forest_input_3_et);
        wtpIntakeEt = (EditText) view.findViewById(R.id.activity_main_wtp_input_et);


        //Fetch the gov data

        new FetchGovDataTask().execute();


        return view;
    }


    /**
     * item selected for the spinner
     */
    private class ItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currentMonth = position + 1;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            currentMonth = 1;
        }
    }


    private class ViewOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.activity_main_submit_btn:
                    if(forestLFEt.getText().toString().trim().length() == 0 ||
                            forestNFEt.getText().toString().trim().length() == 0 ||
                            forestHFEt.getText().toString().trim().length() == 0 ||
                            lndLevelEt.getText().toString().trim().length() == 0 ||
                            wtpIntakeEt.getText().toString().trim().length() == 0){
                        Toast.makeText(getActivity(), "Please Enter all the required values", Toast.LENGTH_LONG).show();

                        return ;
                    }
                    LF = Double.valueOf(forestLFEt.getText().toString());
                    NF = Double.valueOf(forestNFEt.getText().toString());
                    HF = Double.valueOf(forestHFEt.getText().toString());
                    LNDLev = Double.valueOf(lndLevelEt.getText().toString());
                    WTPIntake = Double.valueOf(wtpIntakeEt.getText().toString());


                    LND lnd = new LND();
                    lnd.execute_LND(currentMonth, LF, NF, HF , LNDLev, WTPIntake);

                    Intent intent = new Intent(getActivity(), ResultsActivity.class);
                    intent.putExtra("LNDFIN", lnd.LNDFIN);
                    intent.putExtra("LND_Level", lnd.LND_Level);
                    intent.putExtra("SPILL_RISK", lnd.SPILL_RISK);
                    intent.putExtra("AverageWTP", lnd.AverageWTP);


                    startActivity(intent);

                    break;
            }
        }
    }


    // Description AsyncTask
    private class FetchGovDataTask extends AsyncTask<Void, Void, Boolean> {
        // Description AsyncTask
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mainActivity);
            mProgressDialog.setTitle("Fetching government water data");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connection.Response response = null;
            int monthInt = 0;
            String month = "";
            String url = "http://www.bom.gov.au/water/ssf/north_east_coast/south_coast/fc/";
            String year = Calendar.getInstance().get(Calendar.YEAR) + "";
            Elements dataEles = null;
            Document document = null;

            try {

                monthInt = Calendar.getInstance().get(Calendar.MONTH) + 1;
                if(monthInt < 10) {
                    month += "0" + monthInt;
                }else{
                    month += monthInt;
                }
                url += year + "/" + month + "/146014A_FC_6_" + year + "_" + month + "_summary.html";

                // Connect to the web site
                response = Jsoup.connect(url).execute();

            } catch (IOException e) {
                monthInt = monthInt - 1;
                month = "";

                if (monthInt < 10) {
                    month += "0" + monthInt;
                } else {

                    month += monthInt;
                }

                url = "http://www.bom.gov.au/water/ssf/north_east_coast/south_coast/fc/";
                url += year + "/" + month + "/146014A_FC_6_" + year + "_" + month + "_summary.html";
                try {
                    response = Jsoup.connect(url).execute();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            try {

                document = response.parse();
                // Using Elements to get the Meta data
                dataEles = document
                        .select(".data");

                // Locate the content attribute
                if (dataEles.size() == 6) {
                    lFData = Double.valueOf(dataEles.get(1).html());
                    nFData = Double.valueOf(dataEles.get(3).html());
                    hFData = Double.valueOf(dataEles.get(5).html());
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                // Set description into TextView
                String lFString = lFData + "";
                String nFString = nFData + "";
                String hFString = hFData + "";

                forestLFEt.setText(lFString.substring(0, lFString.length()));
                forestNFEt.setText(nFString.substring(0, lFString.length()));
                forestHFEt.setText(hFString.substring(0, lFString.length()));

            }

            mProgressDialog.dismiss();
        }
    }
}
