package engineering.griffith.edu.lnd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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




    public static LNDFragment newInstance() {
        LNDFragment lndFragment =
                new LNDFragment();
        return lndFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

}
