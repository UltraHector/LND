package engineering.griffith.edu.lnd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity  extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * set up the bottom menu
         */
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomMenuOnclickListener());
        navigation.setVisibility(View.INVISIBLE);

        // display the friend fragment by default
        Fragment fragment = new LNDFragment();
        displayFrament(fragment);
    }



    private class BottomMenuOnclickListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // update the main content by replacing fragments
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.bottom_navigation_lnd:
                    fragment = new LNDFragment();
                    displayFrament(fragment);
                    return true;
                case R.id.bottom_navigation_about:
                    fragment = new AboutFragment();
                    displayFrament(fragment);
                    return true;
            }
            return false;

        }
    };

    private void displayFrament(Fragment fragment){
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_content_frame, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

}