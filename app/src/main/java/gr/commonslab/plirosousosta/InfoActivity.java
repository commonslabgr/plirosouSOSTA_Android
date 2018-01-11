package gr.commonslab.plirosousosta;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Name: InfoActivity.java
 * Description: Implements the "Useful information" screen on the PlirosouSOSTA Android App.
 * A series of textview buttons that spawn alert dialogs (pop ups) with extra information.
 *
 *  Company: commons|lab
 *  Author: Dimitris Koukoulakis
 * License: General Public Licence v3.0 GPL
*/

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView Salary;
        TextView Sunday;
        TextView Saturday;
        TextView Holidays;
        TextView HolidayBonus;
        TextView Easter;
        TextView Christmas;
        TextView Night;
        TextView Discrimination;
        TextView Pregnancy;
        TextView Layoff;
        TextView Overtime;
        TextView Overwork;
        TextView Symvasi;
        TextView WhatToDo;
        setContentView(R.layout.activity_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final AlertDialog infodialog = new AlertDialog.Builder(InfoActivity.this, R.style.AlertDialogCustom).create();

        infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Salary = findViewById(R.id.square_info_salary);
        Salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_salary)));
                infodialog.show();
            }
        });

        Sunday = findViewById(R.id.square_info_sunday);
        Sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_sunday)));
                infodialog.show();
            }
        });

        Saturday = findViewById(R.id.square_info_saturday);
        Saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_saturday)));
                infodialog.show();
            }
        });
        Holidays = findViewById(R.id.square_info_holidays);
        Holidays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_holidays)));
                infodialog.show();
            }
        });
        Easter = findViewById(R.id.square_info_easter);
        Easter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_easter)));
                infodialog.show();
            }
        });
        Night = findViewById(R.id.square_info_night);
        Night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_night)));
                infodialog.show();
            }
        });
        HolidayBonus = findViewById(R.id.square_info_holidaybonus);
        HolidayBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_holidaybonus)));
                infodialog.show();
            }
        });
        Pregnancy = findViewById(R.id.square_info_pregnant);
        Pregnancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_pregnancy)));
                infodialog.show();
            }
        });
        Christmas = findViewById(R.id.square_info_xmas);
        Christmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_christmas)));
                infodialog.show();
            }
        });
        Discrimination = findViewById(R.id.square_info_discrimination);
        Discrimination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_discrimination)));
                infodialog.show();
            }
        });
        Layoff = findViewById(R.id.square_info_layoff);
        Layoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_layoff)));
                infodialog.show();
            }
        });
        Overtime = findViewById(R.id.square_info_overtime);
        Overtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_overtime)));
                infodialog.show();
            }
        });
        Overwork = findViewById(R.id.square_info_overwork);
        Overwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_overwork)));
                infodialog.show();
            }
        });
        Symvasi = findViewById(R.id.square_info_symvasi);
        Symvasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_symvasi)));
                infodialog.show();
            }
        });
        WhatToDo = findViewById(R.id.square_info_whattodo);
        WhatToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_whatcanido)));
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
