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
import android.widget.Spinner;

/**
 * Created by Hector on 22/6/17.
 */
public class Tool3Fragment extends Fragment {

    private Button button;


    private class ItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }



    public static Tool3Fragment newInstance() {
        Tool3Fragment lndFragment =
                new Tool3Fragment();
        return lndFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tool3, container, false);
        // Create an ArrayAdapter using the string array and a default spinner layout


        return view;
    }


    private class ViewOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){

            }
        }
    }

}
