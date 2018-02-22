package gr.commonslab.plirosousosta;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.widget.TimePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *  Name: MainActivity.java
 *  Description: Implements the main screen on the PlirosouSOSTA Android App.
 *  It displays the hours and amount for the current month and three buttons, add a shift, view history and view entitled payment.
 *  It also calls a navigation drawer with other application options.
 *
 *  Company: commons|lab
 *  Author: Dimitris Koukoulakis
 *  License: General Public Licence v3.0 GPL
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Calendar BeginWork;
    private static Calendar EndWork;
    private int month;
    private static boolean BeginWorkSet = false;
    private static boolean b_Overwrite = false;

    private TextView button_history;
    private TextView button_add_shift;
    private Button button_payed_correctly;
    private Calendar cal;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault());
    BroadcastReceiver _broadcastReceiver;
    DBHelper dbHelper;
    SQLiteDatabase sqldb;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar mCalendar;

        dbHelper = new DBHelper(this.getBaseContext());
        sqldb = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        BeginWork = Calendar.getInstance();
        EndWork = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        month = mCalendar.get(Calendar.MONTH);

        button_payed_correctly = findViewById(R.id.button_payed_correctly);
        button_add_shift = findViewById(R.id.square_add_shift);
        button_history = findViewById(R.id.square_history);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cal = Calendar.getInstance();
        addListenerOnButtonAddShift();
        addListenerOnButtonHistory();
        addListenerOnPaidRight();

        //Check if application is running for the first time.
        //Or it's initial data have been cleared
        prefs = getSharedPreferences("gr.commonslab.plirosousosta", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            //Initialize Shared Preferences: Salary per hour, years of experience etc.
            initializePreferences();
        }

    }

    /**
     * Listener for actions when HISTORY button is pressed
     */
    public void addListenerOnButtonHistory() {
        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to entitled activity
                Intent intent_history = new Intent(v.getContext(), HistoryActivity.class);
                v.getContext().startActivity(intent_history);
            }
        });
    }

    /**
     * Listener for actions when PAID CORRECTLY button is pressed
     */
    public void addListenerOnPaidRight() {
        button_payed_correctly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to entitled activity
                Intent intent_entitled = new Intent(v.getContext(), EntitledActivity.class);
                v.getContext().startActivity(intent_entitled);
            }
        });
    }

    /**
     * Listener for Add shift button to show date and time pickers
     */
    public void addListenerOnButtonAddShift() {
        button_add_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShift_showPickers();
            }
        });
    }

    public void addShift_showPickers() {
        //Show DatePicker
        //After date is selected, Timepicker is shown twice
        //once for start shift time and once for end shift time
        //then working hours are added to DB
        //and values on UI are updated
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent_settings = new Intent(getApplicationContext(), SettingsActivity.class);
            intent_settings.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent_settings.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent_settings = new Intent(getApplicationContext(), SettingsActivity.class);
            intent_settings.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent_settings.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent_settings);
        } else if (id == R.id.nav_history) {
            Intent intent_history = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent_history);
        } else if (id == R.id.nav_info) {
            Intent intent_additionalInfo = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(intent_additionalInfo);
        //} else if (id == R.id.nav_shifts) { //Shifts functionality to be added later
        } else if (id == R.id.nav_about) {
            Intent intent_about = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent_about);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Set Month
        TextView text_info_month;
        text_info_month = findViewById(R.id.text_info_month);
        text_info_month.setText(cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        //Set Date and Time
        SetCurrentDateTime();
        //Update Clock every minute
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    SetCurrentDateTime();
            }
        };
        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        UpdateValuesOnScreen();
    }

    //Set Current Date and time on main activity
    public void SetCurrentDateTime() {
        TextView text_datetime;
        text_datetime = findViewById(R.id.text_datetime);
        text_datetime.setText(_sdfWatchTime.format(new Date()));
    }

    /**
     *  TimePickerFragment
     *  Display a time picker for user to select start and end of shift.
     */
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog tp = new TimePickerDialog(getActivity(), R.style.MySpinnerPickerStyle , this, 9, 0, DateFormat.is24HourFormat(getActivity()));

            if (!BeginWorkSet) {
                tp.setTitle(getString(R.string.picker_title_shift_start));
            } else {
                tp.setTitle(getString(R.string.picker_title_shift_end));
            }
            return tp;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (!BeginWorkSet) {
                BeginWork.set(Calendar.HOUR_OF_DAY, hourOfDay);
                BeginWork.set(Calendar.MINUTE, minute);
                BeginWorkSet = true;

                @SuppressLint("ValidFragment")
                TimePickerFragment timeFragmentStop = new TimePickerFragment();
                timeFragmentStop.show(getFragmentManager(), "timePicker");
            } else {
                BeginWorkSet = false;
                EndWork = (Calendar)BeginWork.clone();
                EndWork.set(Calendar.HOUR_OF_DAY, hourOfDay);
                EndWork.set(Calendar.MINUTE, minute);
                if (BeginWork.getTime().after(EndWork.getTime())) {
                    EndWork.add(Calendar.HOUR_OF_DAY, 24);
                }

                //Store working hours to DB
                ((MainActivity) getActivity()).addWorkday(BeginWork,EndWork);
                //addWorkday(BeginWork,EndWork);
            }
        }

        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            BeginWorkSet = false;
        }
    }

    /**
     *  DatePickerFragment
     *  Display a calendar for the user to choose the date for adding a shift.
     */
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dp = new DatePickerDialog(getActivity(), R.style.MySpinnerPickerStyle , this, year, month, day);
            // TODO: REMOVE SET MAX
            // dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dp;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            BeginWork.set(Calendar.HOUR, 0);
            BeginWork.set(Calendar.MINUTE, 0);
            BeginWork.set(Calendar.SECOND, 0);
            BeginWork.set(Calendar.YEAR, year);
            BeginWork.set(Calendar.MONTH, month);
            BeginWork.set(Calendar.DATE, day);
            //Show TimePicker
            TimePickerFragment timeFragmentStart = new TimePickerFragment();
            timeFragmentStart.show(getFragmentManager(), "timePicker");
        }
    }

    public void setWorkingHourBegin (Date begin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT, dateFormat.format(begin));
        sqldb.insert(dbHelper.WORKINGHOURS_TABLE_NAME,null,contentValues);
    }

    public void deleteHoursandPayments(Calendar begin, Calendar end, boolean overwrite) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        if (!overwrite) {
            String sql = "DELETE FROM " + dbHelper.WORKINGHOURS_TABLE_NAME + " WHERE datetime(" +
                    dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ") == datetime('" + dateFormat.format(begin.getTime()) + "')";
            sqldb.execSQL(sql);
            sql = "DELETE FROM " + dbHelper.PAYMENT_TABLE_NAME + " WHERE datetime(" +
                    dbHelper.PAYMENT_COLUMN_BEGINSHIFT + ") == datetime('" + dateFormat.format(begin.getTime()) + "')";
            sqldb.execSQL(sql);
        } else {
            String sql = "DELETE FROM " + dbHelper.WORKINGHOURS_TABLE_NAME + " WHERE (datetime("+
                    dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(begin.getTime()) + "') AND datetime(" +
                    dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(begin.getTime()) + "') ) OR (datetime(" +
                    dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(end.getTime()) + "') AND datetime(" +
                    dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(end.getTime()) + "') )";
            sqldb.execSQL(sql);
            sql = "DELETE FROM " + dbHelper.PAYMENT_TABLE_NAME + " WHERE (datetime("+
                    dbHelper.PAYMENT_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(begin.getTime()) + "') AND datetime(" +
                    dbHelper.PAYMENT_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(begin.getTime()) + "') ) OR (datetime(" +
                    dbHelper.PAYMENT_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(end.getTime()) + "') AND datetime(" +
                    dbHelper.PAYMENT_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(end.getTime()) + "') )";
            sqldb.execSQL(sql);
        }
        UpdateValuesOnScreen();
    }

    public void setWorkingHourEnd (Date end, Date begin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT, dateFormat.format(end));
        sqldb.update(dbHelper.WORKINGHOURS_TABLE_NAME,contentValues,dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT+"= ?",new String[] {dateFormat.format(begin)});
    }

    public void setEntitledPayment(Date begin, Date end) {
        //Calculate and store entitled Payment
        dbHelper.getEntitledPayment(begin, end);
    }

    public static class OverwriteWorkingHours extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_overwrite_hours)
                    .setPositiveButton(R.string.overwrite, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            b_Overwrite = true;
                            ((MainActivity) getActivity()).deleteHoursandPayments(BeginWork, EndWork, b_Overwrite);
                            ((MainActivity) getActivity()).setWorkingHourBegin(BeginWork.getTime());
                            ((MainActivity) getActivity()).setWorkingHourEnd(EndWork.getTime(), BeginWork.getTime());
                            ((MainActivity) getActivity()).setEntitledPayment(BeginWork.getTime(), EndWork.getTime());
                            ((MainActivity) getActivity()).UpdateValuesOnScreen();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            b_Overwrite = false;
                            ((MainActivity) getActivity()).deleteHoursandPayments(BeginWork, EndWork, b_Overwrite);
                            ((MainActivity) getActivity()).setWorkingHourBegin(BeginWork.getTime());
                            ((MainActivity) getActivity()).setWorkingHourEnd(EndWork.getTime(), BeginWork.getTime());
                            ((MainActivity) getActivity()).setEntitledPayment(BeginWork.getTime(), EndWork.getTime());
                            ((MainActivity) getActivity()).UpdateValuesOnScreen();
                        }
                    });
            return builder.create();
        }
    }

    public void addWorkday(Calendar cal_new_begin, Calendar cal_new_end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Write values to DB by default
        sqldb = dbHelper.getReadableDatabase();
        //Check for existing entries between the same datetimes.
        String sql = "SELECT * FROM " + dbHelper.WORKINGHOURS_TABLE_NAME + " WHERE (datetime("+
                dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(cal_new_begin.getTime()) + "') AND datetime(" +
                dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(cal_new_begin.getTime()) + "') ) OR (datetime(" +
                dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ") <= datetime('" + dateFormat.format(cal_new_end.getTime()) + "') AND datetime(" +
                dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT + ") >= datetime('" + dateFormat.format(cal_new_end.getTime()) + "') )";
        Cursor cursor =  sqldb.rawQuery(sql, null );

        try {
            while (cursor.moveToNext()) {
                DialogFragment dialog = new OverwriteWorkingHours();
                dialog.show(getFragmentManager(), "Overwrite?");
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        //deleteHoursandPayments(cal_new_begin, cal_new_end, b_Overwrite);
        setWorkingHourBegin(cal_new_begin.getTime());
        setWorkingHourEnd(cal_new_end.getTime(), cal_new_begin.getTime());
        setEntitledPayment(cal_new_begin.getTime(), cal_new_end.getTime());
        UpdateValuesOnScreen();
        b_Overwrite = false;
    }

    public void UpdateValuesOnScreen() {
        TextView text_info_worked;
        TextView text_info_entitled;
        int minutes;
        float workinghours;
        //Set text_info_worked
        workinghours = dbHelper.getWorkingHoursinMonth(month);
        minutes = (int)((workinghours % 1)*60);

        text_info_worked = findViewById(R.id.text_info_worked);
        text_info_worked.setText(String.format(Locale.getDefault(),"%.0f%s %d%s",workinghours,getString(R.string.short_hour), minutes,getString(R.string.short_minute)));

        //Set text_info_entitled
        text_info_entitled = findViewById(R.id.text_info_entitled);
        text_info_entitled.setText(String.format(Locale.getDefault(),"%s%.2f",getString(R.string.currency),dbHelper.getEntitledPaymentinMonth(month,-1)));
    }

    private void initializePreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        prefs.edit().putBoolean("firstrun", false).apply();
        dbHelper.setHolidays();
    }
}
