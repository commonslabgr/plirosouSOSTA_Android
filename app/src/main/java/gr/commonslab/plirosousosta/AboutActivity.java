package gr.commonslab.plirosousosta;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 *  Name: AboutActivity.java
 *  Description: Implements the "About" screen on the PlirosouSOSTA Android App.
 *  A scrollable text with information about the app.
 *
 *  Company: Commonslab
 *  Author: Dimtris Koukoulakis
 *  License: General Public Licence v3.0 GPL
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
