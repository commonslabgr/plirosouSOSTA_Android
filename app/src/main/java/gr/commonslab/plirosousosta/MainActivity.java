package gr.commonslab.plirosousosta;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.widget.TimePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Calendar BeginWork;
    private static Calendar EndWork;
    private Calendar mCalendar;
    private int year, month, day;
    private static boolean BeginWorkSet = false;
    private boolean b_Overwrite = false;
    static DialogFragment timeFragmentStart;
    static DialogFragment timeFragmentStop;
    DialogFragment dateFragment;
    DialogFragment dialog;
    private TextView text_datetime;
    private TextView text_hours;
    private TextView text_info_month;
    private TextView text_info_worked;
    private TextView text_info_paid;
    private TextView text_info_entitled;
    private ImageButton button_clock;
    private ImageButton button_add_shift;
    private ImageButton button_info;
    private Button button_payed_correctly;
    private boolean CountWork = false;
    Handler hand = new Handler();
    int count = 0;
    private long startTime;
    private Calendar cal;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("dd/M/yyyy HH:mm");
    BroadcastReceiver _broadcastReceiver;
    DBHelper dbHelper;
    SQLiteDatabase sqldb;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this.getBaseContext());
        sqldb = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        BeginWork = Calendar.getInstance();
        EndWork = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        year = mCalendar.get(Calendar.YEAR);
        month = mCalendar.get(Calendar.MONTH);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);

        button_clock = (ImageButton) findViewById(R.id.Button_clock);
        button_add_shift = (ImageButton) findViewById(R.id.Button_add_shift);
        button_info = (ImageButton) findViewById(R.id.Button_info);
        button_payed_correctly = (Button) findViewById(R.id.button_payed_correctly);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cal = Calendar.getInstance();

        timeFragmentStart = new TimePickerFragment();
        timeFragmentStop = new TimePickerFragment();
        addListenerOnButtonAddShift();
        addListenerOnButtonClock();
        addListenerOnButtonInfo();
        addListenerOnPaidRight();

        //Check if application is running for the first time.
        //Or it's initial data have been cleared
        prefs = getSharedPreferences("gr.commonslab.plirosousosta", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            //Initialize Shared Preferences: Salary per hour, years of experience etc.
            initializePreferences();
        }
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            long diff = System.currentTimeMillis() - startTime;
            int hours = 0;
            int mins = 0;

            if (diff > 3600000) {
                hours = (int) diff / 3600000;
            }
            int remainder = (int) diff - (hours*3600000);
            if (remainder > 60000) {
                mins = remainder / 60000;
            }
            remainder = remainder - (mins * 60000);
            int secs = remainder/1000;
            String display = String.format("%02d:%02d:%02d", hours, mins, secs);
            text_hours = (TextView) findViewById(R.id.text_hours);
            text_hours.setText(display);
            hand.postDelayed(this, 1000);
        }
    };

    //Listener for actions when CLOCK button is pressed
    public void addListenerOnButtonClock() {
        //Clock button start counting work time
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountWork = !CountWork;
                //START counting
                if (CountWork) {
                    //Start counter
                    startTime = System.currentTimeMillis();
                    hand.postDelayed(run, 1000);
                    //Change icon
                    button_clock.setImageResource(R.drawable.ic_clock2);
                //STOP counting
                } else {
                    hand.removeCallbacks(run);
                    button_clock.setImageResource(R.drawable.ic_clock);
                    BeginWork.setTime(new Date(startTime));
                    //EndWork.setTime(new Date(System.currentTimeMillis()+14400000));
                    EndWork.setTime(new Date(System.currentTimeMillis()));
                    addWorkday(BeginWork, EndWork);
                }
            }
        });
    }

    //Listener for actions when INFO button is pressed
    public void addListenerOnButtonInfo() {
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to entitled activity
                Intent intent_entitled = new Intent(v.getContext(), EntitledActivity.class);
                v.getContext().startActivity(intent_entitled);
            }
        });
    }

    //Listener for actions when INFO button is pressed
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

    //Add shift button to show date and time pickers
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
        dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        } else if (id == R.id.nav_shifts) {
            //TODO: Handle the Shifts Activity
        } else if (id == R.id.nav_about) {
            //TODO: Handle the About Activity
            //dbHelper.onUpgrade(sqldb,0,1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        /**/
        Typeface font_light = Typeface.createFromAsset(getAssets(), "fonts/PFDinTextCondPro-Light.ttf");
        Typeface font_regular = Typeface.createFromAsset(getAssets(), "fonts/PFDinTextCondPro-Regular.ttf");
        Typeface font_medium = Typeface.createFromAsset(getAssets(), "fonts/PFDinTextCondPro-Medium.ttf");
        /**/

        //Set Month
        text_info_month = (TextView) findViewById(R.id.text_info_month);
        text_info_month.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        text_info_month.setTypeface(font_light);
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
        text_datetime = (TextView) findViewById(R.id.text_datetime);
        text_datetime.setText(_sdfWatchTime.format(new Date()));
    }

    /* TimePickerFragment
    Display a time picker for user to select start and end of shift.
     */
    public class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker


            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog tp = new TimePickerDialog(getActivity(), R.style.MySpinnerPickerStyle , this, 9, 0, DateFormat.is24HourFormat(getActivity()));

            if (BeginWorkSet != true) {
                tp.setTitle(getString(R.string.picker_title_shift_start));
            } else {
                tp.setTitle(getString(R.string.picker_title_shift_end));
            }
            return tp;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (BeginWorkSet != true) {
                BeginWork.set(Calendar.HOUR_OF_DAY, hourOfDay);
                BeginWork.set(Calendar.MINUTE, minute);
                BeginWorkSet = true;
                //add_wh_begin = BeginWork.getTime();
                //debug.setText(BeginWork.getTime().toString());
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
                addWorkday(BeginWork,EndWork);
            }
        }
    }

    /* DatePickerFragment
    Display a calendar for the user to choose the date for adding a shift.
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
            timeFragmentStart.show(getFragmentManager(), "timePicker");
        }
    }

    public void setWorkingHourBegin (Date begin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT, dateFormat.format(begin));
        sqldb.insert(dbHelper.WORKINGHOURS_TABLE_NAME,null,contentValues);
    }

    public void setWorkingHourEnd (Date end, Date begin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.WORKINGHOURS_COLUMN_ENDSHIFT, dateFormat.format(end));
        sqldb.update(dbHelper.WORKINGHOURS_TABLE_NAME,contentValues,dbHelper.WORKINGHOURS_COLUMN_BEGINSHIFT+"= ?",new String[] {dateFormat.format(begin)});
    }

    public void setEntitledPayment(Date begin, Date end) {
        //Calculate and store entitled Payment
        dbHelper.getEntitledPayment(begin, end);
    }

    public class OverwriteWorkingHours extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_overwrite_hours)
                    .setPositiveButton(R.string.overwrite, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            b_Overwrite = true;
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            b_Overwrite = false;
                        }
                    });
            return builder.create();
        }
    }

    public void addWorkday(Calendar cal_new_begin, Calendar cal_new_end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal_rec_begin = Calendar.getInstance();
        Calendar cal_rec_end = Calendar.getInstance();
        //Write values to DB by default
        b_Overwrite = true;
        sqldb = dbHelper.getReadableDatabase();
        String sql = "select * from "+DBHelper.WORKINGHOURS_TABLE_NAME+" WHERE datetime("+
                DBHelper.WORKINGHOURS_COLUMN_BEGINSHIFT + ", 'start of day') == datetime('" + dateFormat.format(cal_new_begin.getTime()) + "', 'start of day')";
        Cursor cursor =  sqldb.rawQuery(sql, null );

        try {
            while (cursor.moveToNext()) {
                try {
                    cal_rec_begin.setTime(dateFormat.parse(cursor.getString(0)));
                    cal_rec_end.setTime(dateFormat.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (((cal_new_begin.before(cal_rec_end) && cal_new_begin.after(cal_rec_begin)) ||
                        (cal_new_end.before(cal_rec_end) && cal_new_end.after(cal_rec_begin))) )                {
                    dialog = new OverwriteWorkingHours();
                    dialog.show(getFragmentManager(), "Overwrite?");
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (!b_Overwrite) {
            return;
        }

        setWorkingHourBegin(cal_new_begin.getTime());
        setWorkingHourEnd(cal_new_end.getTime(), cal_new_begin.getTime());
        setEntitledPayment(cal_new_begin.getTime(), cal_new_end.getTime());
        UpdateValuesOnScreen();
    }

    public void UpdateValuesOnScreen() {
        int minutes = 0;
        float workinghours = 0f;
        //Set text_info_worked
        workinghours = dbHelper.getWorkingHoursinMonth(month);
        minutes = (int)((workinghours % 1)*60);

        text_info_worked = (TextView) findViewById(R.id.text_info_worked);
        text_info_worked.setText(String.format("%.0f%s %d%s",workinghours,getString(R.string.short_hour), minutes,getString(R.string.short_minute)));
        //Set text_info_paid
        text_info_paid = (TextView) findViewById(R.id.text_info_paid);
        text_info_paid.setText(String.format("%s%.2f",getString(R.string.currency),dbHelper.getPaymentinMonth(month)));
        //Set text_info_entitled
        text_info_entitled = (TextView) findViewById(R.id.text_info_entitled);
        text_info_entitled.setText(String.format("%s%.2f",getString(R.string.currency),dbHelper.getEntitledPaymentinMonth(month)));
    }

    private void initializePreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        prefs.edit().putBoolean("firstrun", false).commit();
        dbHelper.setHolidays();
    }
}
