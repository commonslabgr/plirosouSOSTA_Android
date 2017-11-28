package gr.commonslab.plirosousosta;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class InfoActivity extends AppCompatActivity {
    private TextView Salary;
    private TextView Sunday;
    private TextView Saturday;
    private TextView Holidays;
    private TextView HolidayBonus;
    private TextView Easter;
    private TextView Christmas;
    private TextView Night;
    private TextView Strike;
    private TextView Discrimination;
    private TextView Pregnancy;
    private TextView Layoff;
    private TextView Overtime;
    private TextView Overwork;
    private TextView WhatToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final AlertDialog infodialog = new AlertDialog.Builder(InfoActivity.this).create();

        infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Salary = (TextView) findViewById(R.id.square_info_salary);
        Salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_salary));
                infodialog.show();
            }
        });

        Sunday = (TextView) findViewById(R.id.square_info_sunday);
        Sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_sunday));
                infodialog.show();
            }
        });

        Saturday = (TextView) findViewById(R.id.square_info_saturday);
        Saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_saturday));
                infodialog.show();
            }
        });
        Holidays = (TextView) findViewById(R.id.square_info_holidays);
        Holidays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_holidays));
                infodialog.show();
            }
        });
        Easter = (TextView) findViewById(R.id.square_info_easter);
        Easter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_easter));
                infodialog.show();
            }
        });
        Night = (TextView) findViewById(R.id.square_info_night);
        Night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_night));
                infodialog.show();
            }
        });
        HolidayBonus = (TextView) findViewById(R.id.square_info_holidaybonus);
        HolidayBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_holidaybonus));
                infodialog.show();
            }
        });
        Pregnancy = (TextView) findViewById(R.id.square_info_pregnant);
        Pregnancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_pregnancy));
                infodialog.show();
            }
        });
        Christmas = (TextView) findViewById(R.id.square_info_xmas);
        Christmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_christmas));
                infodialog.show();
            }
        });
        Strike = (TextView) findViewById(R.id.square_info_strike);
        Strike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_strike));
                infodialog.show();
            }
        });
        Discrimination = (TextView) findViewById(R.id.square_info_discrimination);
        Discrimination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_discrimination));
                infodialog.show();
            }
        });
        Layoff = (TextView) findViewById(R.id.square_info_layoff);
        Layoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_layoff));
                infodialog.show();
            }
        });
        Overtime = (TextView) findViewById(R.id.square_info_overtime);
        Overtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_overtime));
                infodialog.show();
            }
        });
        Overwork = (TextView) findViewById(R.id.square_info_overwork);
        Overwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_overwork));
                infodialog.show();
            }
        });
        WhatToDo = (TextView) findViewById(R.id.square_info_whattodo);
        WhatToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(getString(R.string.text_info_whatcanido));
                infodialog.show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
