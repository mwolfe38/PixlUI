package com.neopixl.pixluisample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

/**
 * Created by mwolfe on 6/11/14.
 */
public class StyledActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openRegularActivity(View button) {
        onBackPressed();
    }
}
