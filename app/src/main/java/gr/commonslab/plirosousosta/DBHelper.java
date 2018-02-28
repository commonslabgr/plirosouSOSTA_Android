package gr.commonslab.plirosousosta;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *  Name: DBHelper.java
 *  Description:
 *
 *  Company: commons|lab
 *  Author: Dimitris Koukoulakis
 *  License: General Public Licence v3.0 GPL
*/

public class DBHelper extends SQLiteOpenHelper {
    //DATABASE
    private static final String DATABASE_NAME;
    private static final int DATABASE_VERSION;
    private static int overwork_start; //hours per week
    private static String NIGHT;

    public static final String WORKINGHOURS_TABLE_NAME;
    public static final String WORKINGHOURS_COLUMN_BEGINSHIFT;
    public static final String WORKINGHOURS_COLUMN_ENDSHIFT;
    public static final String WORKINGHOURS_COLUMN_SATURDAY;
    public static final String WORKINGHOURS_COLUMN_SUNDAY;
    public static final String WORKINGHOURS_COLUMN_NIGHT;
    public static final String WORKINGHOURS_COLUMN_OVERTIME;
    public static final String WORKINGHOURS_COLUMN_OVERWORK;
    public static final String PAYMENT_TABLE_NAME;
    public static final String PAYMENT_COLUMN_BEGINSHIFT;
    public static final String PAYMENT_COLUMN_ENDSHIFT;
    public static final String PAYMENT_COLUMN_ACTUALAMOUNT;
    public static final String PAYMENT_COLUMN_ENTITLED_AMOUNT;
    public static final String PAYMENT_COLUMN_SATURDAY;
    public static final String PAYMENT_COLUMN_SUNDAY;
    public static final String PAYMENT_COLUMN_NIGHT;
    public static final String PAYMENT_COLUMN_OVERTIME;
    public static final String PAYMENT_COLUMN_OVERWORK;
    public static final String SETTINGS_TABLE_NAME;
    public static final String SETTINGS_COLUMN_AMOUNT;
    public static final String SETTINGS_COLUMN_DATE;
    public static final String SETTINGS_COLUMN_TYPE;
    public static final String HOLIDAYS_TABLE_NAME;
    public static final String HOLIDAY_COLUMN;

    static {
        DATABASE_NAME = "plirosousosta.db";
        DATABASE_VERSION = 1;
        overwork_start = 40;
        NIGHT = "NIGHT";
        HOLIDAY_COLUMN = "holiday_date";
        HOLIDAYS_TABLE_NAME = "holidays";
        WORKINGHOURS_TABLE_NAME = "workinghours";
        WORKINGHOURS_COLUMN_BEGINSHIFT = "begin";
        WORKINGHOURS_COLUMN_ENDSHIFT = "end";
        WORKINGHOURS_COLUMN_SATURDAY = "saturday";
        WORKINGHOURS_COLUMN_SUNDAY = "sunday";
        WORKINGHOURS_COLUMN_NIGHT = "night";
        WORKINGHOURS_COLUMN_OVERTIME = "overtime";
        WORKINGHOURS_COLUMN_OVERWORK = "overwork";
        PAYMENT_TABLE_NAME = "payment";
        PAYMENT_COLUMN_BEGINSHIFT = "beginshift";
        PAYMENT_COLUMN_ENDSHIFT = "endshift";
        PAYMENT_COLUMN_ACTUALAMOUNT = "actual_amount";
        PAYMENT_COLUMN_ENTITLED_AMOUNT = "entitled_amount";
        PAYMENT_COLUMN_SATURDAY = "saturday";
        PAYMENT_COLUMN_SUNDAY = "sunday";
        PAYMENT_COLUMN_NIGHT = "night";
        PAYMENT_COLUMN_OVERTIME = "overtime";
        PAYMENT_COLUMN_OVERWORK = "overwork";
        SETTINGS_TABLE_NAME = "settings";
        SETTINGS_COLUMN_AMOUNT = "amount";
        SETTINGS_COLUMN_DATE = "date";
        SETTINGS_COLUMN_TYPE = "type";
    }

    private static Float multiplier_overwork = 0.2f; //%
    private static Float multiplier_overtime = 0.8f; //%
    private static Float multiplier_holidays = 0.75f; //%
    private static Float multiplier_nightshifts = 0.25f; //%
    private static Float multiplier_saturdays = 0.3f; //%

    private SQLiteDatabase sqldb;
    private Context context;
    private Calendar nightshift_start = Calendar.getInstance();
    private Calendar nightshift_end = Calendar.getInstance();
    private float[] LegalPaymentsOver25 = new float[4];
    private float[] LegalPaymentsUnder25 = new float[2];
    private boolean Below25 = false;
    private int YearsExperience = 0;
    private boolean FnBIndustryWorker = false;
    private float hourly_wage = 0f;
    private float daily_wage = 0f;
    private float monthly_wage = 0f;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "create table " + WORKINGHOURS_TABLE_NAME +
                        " ("+WORKINGHOURS_COLUMN_BEGINSHIFT+" text primary key, " +
                        WORKINGHOURS_COLUMN_ENDSHIFT+" text, " +
                        WORKINGHOURS_COLUMN_SATURDAY+" real, " +
                        WORKINGHOURS_COLUMN_SUNDAY+" real, " +
                        WORKINGHOURS_COLUMN_NIGHT+" real, " +
                        WORKINGHOURS_COLUMN_OVERTIME+" real, " +
                        WORKINGHOURS_COLUMN_OVERWORK+" real )"
        );
        database.execSQL(
                "create table " + PAYMENT_TABLE_NAME +
                        " ("+PAYMENT_COLUMN_BEGINSHIFT+" text primary key, " +
                        PAYMENT_COLUMN_ENDSHIFT+" text, " +
                        PAYMENT_COLUMN_ACTUALAMOUNT+" real, " +
                        PAYMENT_COLUMN_ENTITLED_AMOUNT+" real, " +
                        PAYMENT_COLUMN_SATURDAY+" real, " +
                        PAYMENT_COLUMN_SUNDAY+" real, " +
                        PAYMENT_COLUMN_NIGHT+" real, " +
                        PAYMENT_COLUMN_OVERTIME+" real, " +
                        PAYMENT_COLUMN_OVERWORK+" real)"
        );
        //Create settings table
        database.execSQL(
                "create table " + SETTINGS_TABLE_NAME+
                        " ("+SETTINGS_COLUMN_DATE+" text primary key, "+SETTINGS_COLUMN_AMOUNT+ " real, "+SETTINGS_COLUMN_TYPE+" text)"
        );

        database.execSQL(
                "create table " + HOLIDAYS_TABLE_NAME+
                        " ("+HOLIDAY_COLUMN+" text primary key)"
        );

        //Set Holidays
        //setHolidays(database);
        //setHourWage();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WORKINGHOURS_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + PAYMENT_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + HOLIDAYS_TABLE_NAME);

        onCreate(database);
    }

    public void setHolidays() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //Add holidays
        sqldb = this.getWritableDatabase();
        //database.beginTransaction();
        ContentValues contentValues = new ContentValues();
        //HOLIDAYS IN GREECE 2018 - 2022
        //ΠΡΩΤΟΧΡΟΝΙΑ
        try {
            contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-01-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
            contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-01-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
            contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-01-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
            contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-01-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
            contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-01-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΘΕΟΦΑΝΕΙΑ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-01-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-01-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-01-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-01-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-01-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΚΑΘΑΡΑ ΔΕΥΤΕΡΑ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-02-19")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-03-11")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-03-02")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-03-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-03-07")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //25η ΜΑΡΤΙΟΥ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-03-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-03-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-03-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-03-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-03-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΜΕΓΑΛΗ ΠΑΡΑΣΚΕΥΗ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-04-06")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-04-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-04-17")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-04-30")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-04-22")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΜΕΓΑΛΟ ΣΑΒΒΑΤΟ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-04-07")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-04-27")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-04-18")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-05-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-04-23")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΔΕΥΤΕΡΑ ΤΟΥ ΠΑΣΧΑ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-04-09")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-04-29")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-04-20")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-05-03")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-04-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //1η ΜΑΙΟΥ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-05-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-05-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-05-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //contentValues.put(this.HOLIDAY_COLUMN, dateFormat.format("2021-05-01"))); Same as easter
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-05-01")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //15 ΑΥΓΟΥΣΤΟΥ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-08-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-08-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-08-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-08-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-08-15")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //28η ΟΚΤΩΒΡΙΟΥ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-10-28")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-10-28")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-10-28")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-10-28")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-10-28")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΧΡΙΣΤΟΥΓΕΝΝA
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-12-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-12-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-12-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-12-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-12-25")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //ΕΠΟΜΕΝΗ ΧΡΙΣΤΟΥΓΕΝΝΩΝ
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2018-12-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2019-12-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2020-12-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2021-12-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        contentValues.put(HOLIDAY_COLUMN, dateFormat.format(date.parse("2022-12-26")));
            sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setHourWage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        /*
        String paidby = prefs.getString("list_paidby_values","0");
        int salary = Integer.parseInt(paidby);
        switch (salary) {
            case 0:
                hourly_wage = Float.parseFloat(prefs.getString("paid_hour_key","0"));
                break;
            case 1:
                hourly_wage = (Float.parseFloat(prefs.getString("paid_hour_key","0")))/8;
                break;
            case 2:
                hourly_wage = (Float.parseFloat(prefs.getString("paid_hour_key","0")))/166;
                break;
        }
        /**/
        hourly_wage = Float.parseFloat(prefs.getString("paid_hour_key","0"));
    }

    public Calendar setStartofDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        return cal;
    }

    public Calendar setEndofDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal;
    }

    public void initializeNightshiftHours() {
        Calendar c = Calendar.getInstance();
        nightshift_start.set(Calendar.HOUR_OF_DAY, 22);
        nightshift_start.set(Calendar.MINUTE, 0);
        nightshift_start.set(Calendar.SECOND, 0);
        nightshift_end.add(Calendar.DAY_OF_YEAR, 1);
        nightshift_end.set(Calendar.HOUR_OF_DAY, 6);
        nightshift_end.set(Calendar.MINUTE, 0);
        nightshift_end.set(Calendar.SECOND, 0);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    public Float getNightShiftHours(Date from, Date to) {
        Float nightshifthours = 0f;
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        float millisinhours = 3600000;

        //Initialize calendars for nightshifts
        nightshift_start = (Calendar)beginwork.clone();
        nightshift_end = (Calendar)endwork.clone();
        initializeNightshiftHours();
        //TODO: Check if it saturday or sunday for extra multiplier
        //Calculate night shift hours
        if (nightshift_start.getTimeInMillis() > beginwork.getTimeInMillis()) {
            //If begin working hour is before the night shift starting hour
            //Calculate the night shift working hour by subtracting the hour of the start of night shift
            if (nightshift_end.getTimeInMillis() > endwork.getTimeInMillis()) {
                nightshifthours = ((endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
            } else {
                nightshifthours = ((nightshift_end.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
            }
        } else { //Begin work is after night shift start
            if (nightshift_end.getTimeInMillis() > endwork.getTimeInMillis()) {
                nightshifthours = ((endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
            } else {
                nightshifthours = ((nightshift_end.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
            }
        }
        if (nightshifthours < 0)
            nightshifthours = 0f;
        nightshifthours = round(nightshifthours,2);
        return nightshifthours;
    }

    //Calculate the hours that count as overtime
    public Float getOvertimeHours(Date from, Date to, String flag) {
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        Calendar cal = Calendar.getInstance();
        Float overtimehours;
        float millisinhours = 3600000;
        //Overtime in weekdays starts after the 9th hour of work
        int weekDayOvertime = 9;
        //Overtime in weekend starts after the 8th hour of work
        int weekEndOvertime = 8;
        boolean weekDay = isWeekDay(beginwork);

        if (weekDay) {
            if ((endwork.getTimeInMillis() > nightshift_start.getTimeInMillis()) && (flag == NIGHT)) {
                //overtimehours = ((endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
                cal = (Calendar)beginwork.clone();
                cal.add(Calendar.HOUR, weekDayOvertime);
                overtimehours = ((endwork.getTimeInMillis() - cal.getTimeInMillis()) / millisinhours);
            } else {
                cal = (Calendar)beginwork.clone();
                cal.add(Calendar.HOUR, weekDayOvertime);
                overtimehours = ((endwork.getTimeInMillis() - cal.getTimeInMillis()) / millisinhours);
            }
        } else {
            if ((endwork.getTimeInMillis() > nightshift_start.getTimeInMillis()) && (flag == NIGHT)) {
                //overtimehours = ((endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
                cal = (Calendar)beginwork.clone();
                cal.add(Calendar.HOUR, weekEndOvertime);
                overtimehours = ((endwork.getTimeInMillis() - cal.getTimeInMillis()) / millisinhours);
            } else {
                cal = (Calendar)beginwork.clone();
                cal.add(Calendar.HOUR, weekEndOvertime);
                overtimehours = ((endwork.getTimeInMillis() - cal.getTimeInMillis()) / millisinhours);
            }
        }

        if (overtimehours < 0) {
            overtimehours = 0f;
        }
        return round(overtimehours, 2);
    }

    //Calculate the hours that are in SATURDAY
    public Float getSaturdaysHours(Date from, Date to, String flag) {
        if (FnBIndustryWorker) {
            return 0f;
        }

        boolean isSaturdaybegin = false;
        boolean isSaturdayend = false;
        float result = 0f;

        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);

        nightshift_start = (Calendar)beginwork.clone();
        nightshift_end = (Calendar)endwork.clone();
        initializeNightshiftHours();

        float millisinhours = 3600000;

        if (Calendar.SATURDAY == beginwork.get(Calendar.DAY_OF_WEEK)) {
            isSaturdaybegin = true;
        }
        if (Calendar.SATURDAY == endwork.get(Calendar.DAY_OF_WEEK)) {
            isSaturdayend = true;
        }

        if ((isSaturdaybegin) && (isSaturdayend)) {
            result = ((endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
        } else if (isSaturdaybegin) {
            Calendar beginSat = (Calendar)beginwork.clone();
            beginSat = setEndofDay(beginSat);
            result = ((beginSat.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
        } else if (isSaturdayend) {
            Calendar endSat = (Calendar)endwork.clone();
            endSat = setStartofDay(endSat);
            result = ((endwork.getTimeInMillis() - endSat.getTimeInMillis()) / millisinhours);
        } else {
            result = 0f;
        }
        if ((flag == NIGHT) && (endwork.getTimeInMillis() > nightshift_start.getTimeInMillis())) {
            if (isSaturdaybegin) {
                result = ((endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
            } else {
                Calendar endSat = (Calendar)endwork.clone();
                endSat = setStartofDay(endSat);
                result = ((endwork.getTimeInMillis() - endSat.getTimeInMillis()) / millisinhours);
            }
        }

        if (result < 0) {
            result = 0f;
        } else {
            result = round(result, 2);
        }
        return result;
    }

    //Calculate the hours that count as overwork υπερεργασία
    public Float getOverworkHours(Date from, Date to, String flag) {
        //TODO: calculate for NIGHT flag
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);

        Calendar startWeek = Calendar.getInstance();
        startWeek.setTime(from);
        Calendar endWeek = Calendar.getInstance();
        endWeek.setTime(to);
        int dayofweek = beginwork.get(Calendar.DAY_OF_WEEK);
        if (dayofweek > 1) {
            startWeek.add(Calendar.DAY_OF_WEEK, 2-dayofweek );
        } else {
            startWeek.add(Calendar.DAY_OF_WEEK, dayofweek+1 );
        }
        startWeek = setStartofDay(startWeek);
        endWeek = setEndofDay(endWeek);

        if (FnBIndustryWorker) {
            endWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        } else {
            endWeek.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        }
        Float overworkhours = 0f;
        Float overtime = 0f;
        float weeklyhours;
        boolean weekDay = isWeekDay(beginwork);

        weeklyhours = getWorkingHours(startWeek.getTime(), endWeek.getTime());

        overtime = getHoursFromTo(startWeek, endWeek, WORKINGHOURS_COLUMN_OVERTIME);

        if ((weekDay) && (weeklyhours > overwork_start) ){
            overworkhours = weeklyhours - overwork_start;
            overworkhours -= overtime;
            if (overworkhours < 0f) {
                overworkhours = 0f;
            }
        } else {
            overworkhours = 0f;
        }

        return overworkhours;
    }

    public Float getHolidaysHours(Date from, Date to, String flag) {
        Float holidayHours;
        float millisinhours = 3600000;
        boolean isHolidaybegin = false;
        boolean isHolidayend = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date holiday = new Date();
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        Calendar beginHol = Calendar.getInstance();
        Calendar endHol = Calendar.getInstance();
        Calendar hol = Calendar.getInstance();
        sqldb = this.getReadableDatabase();

        nightshift_start = (Calendar)beginwork.clone();
        nightshift_end = (Calendar)endwork.clone();
        initializeNightshiftHours();

        if (Calendar.SUNDAY == beginwork.get(Calendar.DAY_OF_WEEK)) {
            isHolidaybegin = true;
        }
        if (Calendar.SUNDAY == endwork.get(Calendar.DAY_OF_WEEK)) {
            isHolidayend = true;
        }
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM " + HOLIDAYS_TABLE_NAME+" WHERE "+HOLIDAY_COLUMN+"='"+dateFormat.format(from)+"'";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                isHolidaybegin = true;
            }

            sql = "SELECT * FROM " + HOLIDAYS_TABLE_NAME+" WHERE "+HOLIDAY_COLUMN+"='"+dateFormat.format(to)+"'";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                isHolidayend = true;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if ((isHolidaybegin) && (isHolidayend)) {
            holidayHours = ((endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
        } else if (isHolidaybegin) {
            beginHol = (Calendar)beginwork.clone();
            beginHol = setEndofDay(beginHol);
            holidayHours = ((beginHol.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
        } else if (isHolidayend) {
            endHol = (Calendar)endwork.clone();
            endHol = setStartofDay(endHol);
            holidayHours = ((endwork.getTimeInMillis() - endHol.getTimeInMillis()) / millisinhours);
        } else {
            holidayHours = 0f;
        }
        if ((flag == NIGHT) && (endwork.getTimeInMillis() > nightshift_start.getTimeInMillis())) {
            holidayHours = ((endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours);
        }
        if (cursor != null) {
            cursor.close();
        }
        return round(holidayHours, 2);
    }

    public boolean isWeekDay(Calendar day) {
        boolean weekDay = true;
        int dow = day.get(Calendar.DAY_OF_WEEK);
        //Check if Saturday should be calculated as week day or holiday
        if (FnBIndustryWorker) {
            weekDay = ((dow >= Calendar.MONDAY) && (dow <= Calendar.SATURDAY));
        } else {
            weekDay = ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY));
        }
        return weekDay;
    }

    public void storePayments(Date from, Date to, float normal, float holidays, float saturdays, float nights, float overtime, float overwork) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ContentValues contentValues = new ContentValues();
        float entitled_payment = normal + holidays + nights + overtime + overwork + saturdays;
        sqldb = this.getWritableDatabase();

        contentValues.put(PAYMENT_COLUMN_BEGINSHIFT,dateFormat.format(from));
        contentValues.put(PAYMENT_COLUMN_ENDSHIFT,dateFormat.format(to));
        contentValues.put(PAYMENT_COLUMN_ACTUALAMOUNT, normal);
        contentValues.put(PAYMENT_COLUMN_SUNDAY, holidays);
        contentValues.put(PAYMENT_COLUMN_SATURDAY, saturdays);
        contentValues.put(PAYMENT_COLUMN_NIGHT, nights);
        contentValues.put(PAYMENT_COLUMN_OVERTIME, overtime);
        contentValues.put(PAYMENT_COLUMN_OVERWORK, overwork);
        contentValues.put(PAYMENT_COLUMN_ENTITLED_AMOUNT,entitled_payment);

        //Insert or Update Values
        sqldb.insertWithOnConflict(PAYMENT_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public float getEntitledPayment(Date from, Date to) {
        sqldb = this.getReadableDatabase();
        float hours_normal = 0f;
        float hours_holidays = 0f;
        float hours_holidays_overtime = 0f;
        float hours_holidays_night = 0f;
        float hours_holidays_night_overtime = 0f;
        float hours_night = 0f;
        float hours_overwork = 0f;
        float hours_overwork_night = 0f;
        float hours_overtime = 0f;
        float hours_overtime_night = 0f;
        float hours_saturday = 0f;
        float hours_saturday_night = 0f;

        float payment_normal = 0f;
        float payment_holidays = 0f;
        float payment_night = 0f;
        float payment_overwork = 0f;
        float payment_overtime = 0f;
        float payment_overtime_saturday = 0f;
        float payment_overtime_holidays = 0f;
        float payment_overtime_night = 0f;
        float payment_saturdays = 0f;
        float payment_entitled = 0f;
        float legal_hourly_pay = 0f;
        float tmp_hourly_wage = 0f;

        //Calculate Hours
        hours_normal = (to.getTime() - from.getTime()) / 3600000;
        hours_holidays = getHolidaysHours(from, to, "");
        hours_night = getNightShiftHours(from, to);
        //TODO: split hours when more than one conditions is met
        hours_saturday = getSaturdaysHours(from, to, "");
        hours_overtime = getOvertimeHours(from, to, "");
        hours_overwork = getOverworkHours(from, to, "");
        if (hours_overwork > 5f) {
            hours_overtime = (hours_overwork - 5f);
            hours_overwork = 5f;
        }
        hours_overwork -= hours_overtime;
        if (hours_overwork < 0f) {
            hours_overwork = 0f;
        }
        if (hours_saturday > 0) {
            if (hours_overtime > 0) {
                //TODO: calculate overtime with saturday split
            }
        }
        if (hours_night > 0) {
            if (hours_saturday > 0) {
                hours_saturday_night = getSaturdaysHours(from, to, NIGHT);
                hours_saturday -= hours_saturday_night;
            }
            if (hours_overtime > 0) {
                hours_overtime_night = getOvertimeHours(from, to, NIGHT);
                hours_overtime -= hours_overtime_night;
            }
            if (hours_overwork > 0) {
                hours_overwork_night = getOverworkHours(from, to, NIGHT);
                hours_overwork -= hours_overwork_night;
            }
            if (hours_holidays > 0) {
                hours_holidays_night = getHolidaysHours(from, to, NIGHT);
            }
        }

       //Store working hours values to DB
        storeWorkingHours(from, to, hours_holidays, (hours_saturday+hours_saturday_night), hours_night, (hours_overtime+hours_overtime_night), (hours_overwork+hours_overwork_night));

        //Calculate Payments
        legal_hourly_pay = getLegalHourPayment();
        setHourWage();
        payment_normal = getPayment_Actual(hours_normal);
        payment_holidays = getHolidayPayment(hours_holidays, legal_hourly_pay);
        payment_night = getNightShiftPayment(hours_night, legal_hourly_pay);
        /*if (hours_holidays_night > 0) {
            tmp_hourly_wage = hourly_wage + (legal_hourly_pay*multiplier_holidays);
            payment_holidays += getHolidayPayment(hours_holidays_night, tmp_hourly_wage);
        }*/
        if (hours_saturday_night > 0) {
            tmp_hourly_wage = hourly_wage + (legal_hourly_pay*multiplier_nightshifts);
            payment_saturdays = getSaturdaysPayment(hours_saturday_night, tmp_hourly_wage);
        }
        payment_saturdays += getSaturdaysPayment(hours_saturday, hourly_wage);
        if ((hours_overtime > 0) || (hours_overtime_night > 0)){
            payment_overtime = getOvertimePayment(hours_overtime, hourly_wage);
            if (hours_saturday > 0) {
                payment_overtime = 0;
                //Calculate Overtime payment on a Saturday
                tmp_hourly_wage = hourly_wage + (hourly_wage * multiplier_saturdays);
                payment_overtime_saturday = getOvertimePayment(hours_overtime, tmp_hourly_wage);
                //Calculate normal Overtime payment
                //payment_overtime += getOvertimePayment(hours_overtime, hourly_wage);
            }
            if (hours_holidays > 0) {
                payment_overtime = 0;
                //Calculate Overtime payment on a Sunday or Holiday
                tmp_hourly_wage = hourly_wage + (legal_hourly_pay * multiplier_holidays);
                payment_overtime_holidays = getOvertimePayment(hours_overtime, tmp_hourly_wage);
            }
            if (hours_overtime_night > 0) {
                //Calculate overtime payment on a night
                tmp_hourly_wage = hourly_wage + (legal_hourly_pay * multiplier_nightshifts);
                payment_overtime_night = getOvertimePayment(hours_overtime_night, tmp_hourly_wage);
                //Calculate normal Overtime payment
                if (hours_holidays == 0) {
                    payment_overtime_holidays = getOvertimePayment(hours_overtime, hourly_wage);
                } else {
                    payment_overtime_night = 0;
                    tmp_hourly_wage = hourly_wage + (legal_hourly_pay * multiplier_holidays);
                    payment_overtime_holidays = getOvertimePayment(hours_overtime_night, tmp_hourly_wage);
                }
            }
            payment_overtime += payment_overtime_holidays + payment_overtime_night + payment_overtime_saturday;
        }

        if (hours_overwork_night > 0) {
            tmp_hourly_wage = hourly_wage + (legal_hourly_pay*multiplier_nightshifts);
            payment_overwork = getOverworkPayment(hours_overwork_night, tmp_hourly_wage);
        }
        payment_overwork += getOverworkPayment(hours_overwork, hourly_wage);

        //Store payment values to DB
        storePayments(from, to, payment_normal, payment_holidays, payment_saturdays, payment_night, payment_overtime, payment_overwork);
        payment_entitled = round(payment_normal + payment_holidays + payment_night + payment_saturdays + payment_overtime + payment_overwork, 2);
        return payment_entitled;
    }

    public float getHolidayPayment(float hours, float legal_pay) {
        return round((multiplier_holidays * hours) * legal_pay, 2);
    }

    public float getSaturdaysPayment(float hours, float hourly_pay) {
        if (FnBIndustryWorker) {
            return 0;
        } else {
            return round((multiplier_saturdays * hours) * hourly_pay, 2);
        }
    }

    public float getNightShiftPayment(float hours, float legal_pay) {
        return round((multiplier_nightshifts * hours) * legal_pay, 2);
    }

    public float getOvertimePayment(float hours, float hourly_pay) {
        return round((multiplier_overtime * hours) * hourly_pay, 2);
    }

    public float getOverworkPayment(float hours, float hourly_pay) {
        return round((multiplier_overwork * hours) * hourly_pay, 2);
    }

    public void setDailyWage(float value) {
        daily_wage = value;
    }

    public void setMonthlyWage(float value) {
        monthly_wage = value;
    }

    public float getHourWage() {
        if (monthly_wage > 0 ) {
            hourly_wage = monthly_wage * 0.006f;
        } else if (daily_wage > 0) {
            daily_wage = daily_wage * 0.15f;
        }
        return hourly_wage;
    }

    //Return the value of the Legal Wage per hour that the worker is entitled to depending their age and years of experience
    public float getLegalHourPayment() {
        float result = 0f;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        FnBIndustryWorker = prefs.getBoolean("switch_hotels", false);
        Below25 = prefs.getBoolean("switch_below25", false);
        YearsExperience = Integer.parseInt(prefs.getString("years_experience_key","0"));
        //Set Hourly Legal Payment
        LegalPaymentsOver25[0] = 2.96f;
        LegalPaymentsOver25[1] = 3.25f;
        LegalPaymentsOver25[2] = 3.54f;
        LegalPaymentsOver25[3] = 3.84f;
        LegalPaymentsUnder25[0] = 2.58f;
        LegalPaymentsUnder25[1] = 2.83f;

        if (!Below25) {
            if (YearsExperience < 3) {
                result = LegalPaymentsOver25[0];
            } else if (YearsExperience < 6) {
                result = LegalPaymentsOver25[1];
            } else if (YearsExperience < 9) {
                result = LegalPaymentsOver25[2];
            } else {
                result = LegalPaymentsOver25[3];
            }
        } else {
            if (YearsExperience < 3) {
                result = LegalPaymentsUnder25[0];
            } else {
                result = LegalPaymentsUnder25[1];
            }
        }
        return result;
    }

    public float getPayment_Actual(float hours) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float pay = 0f;
        /*
        String paidby = prefs.getString("list_paidby_values","0");
        int salary = Integer.parseInt(paidby);
        switch (salary) {
            case 0:
                pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
                break;
            case 1:
                pay = (Float.parseFloat(prefs.getString("paid_hour_key","0")))/8;
                break;
            case 2:
                pay = (Float.parseFloat(prefs.getString("paid_hour_key","0")))/166;
                break;
        }
        /**/
        pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
        return pay*hours;
    }

    public Date getFirstWorkingDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date result = new Date();
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT MIN("+WORKINGHOURS_COLUMN_BEGINSHIFT+") FROM " + WORKINGHOURS_TABLE_NAME;
            cursor = sqldb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                try {
                    if (cursor.getString(0) != null) {
                        result = dateFormat.parse(cursor.getString(0));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public Date getLastWorkingDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date result = new Date();
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT MAX("+WORKINGHOURS_COLUMN_ENDSHIFT+") FROM " + WORKINGHOURS_TABLE_NAME;
            cursor = sqldb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                try {
                    if (cursor.getString(0) != null) {
                        result = dateFormat.parse(cursor.getString(0));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public float getWorkingHours(Date from, Date to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date begin = new Date();
        Date end = new Date();
        float workinghours = 0;
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "";
            if ((from == null) && (to == null)) {
                sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME;
            } else if (to == null) {
                sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE date("+WORKINGHOURS_COLUMN_BEGINSHIFT+", 'start of day')=date('"+dateFormat.format(from)+"','start of day')";
            } else {
                sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime(" +
                        WORKINGHOURS_COLUMN_BEGINSHIFT + ") > datetime('" + datetimeFormat.format(from) + "') AND datetime(" +
                        WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + datetimeFormat.format(to) + "')";
            }
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = datetimeFormat.parse(cursor.getString(0));
                    end = datetimeFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                workinghours += ((end.getTime() - begin.getTime())/3600000);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }

        return workinghours;
    }

    public void storeWorkingHours(Date from, Date to, float holidays, float saturdays, float nights, float overtime, float overwork) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ContentValues contentValues = new ContentValues();

        sqldb = this.getWritableDatabase();

        contentValues.put(WORKINGHOURS_COLUMN_BEGINSHIFT, dateFormat.format(from));
        contentValues.put(WORKINGHOURS_COLUMN_ENDSHIFT, dateFormat.format(to));
        contentValues.put(WORKINGHOURS_COLUMN_SUNDAY, holidays);
        contentValues.put(WORKINGHOURS_COLUMN_SATURDAY, saturdays);
        contentValues.put(WORKINGHOURS_COLUMN_NIGHT, nights);
        contentValues.put(WORKINGHOURS_COLUMN_OVERTIME, overtime);
        contentValues.put(WORKINGHOURS_COLUMN_OVERWORK, overwork);

        //Insert or Update Values
        try {
            sqldb.insertWithOnConflict(WORKINGHOURS_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Calendar getBeginWorkingHour(Calendar day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar beginWorkHour = Calendar.getInstance();
        Calendar startDay =  (Calendar)day.clone();
        Calendar endDay =  (Calendar)day.clone();
        startDay = setStartofDay(startDay);
        endDay = setEndofDay(endDay);

        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    //TODO: What if there are more than 1 entry in the same day?
                    beginWorkHour.setTime(dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    beginWorkHour = null;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            beginWorkHour = null;
        }
        if (cursor != null) {
            cursor.close();
        }

        return beginWorkHour;
    }

    public Calendar getEndWorkingHour(Calendar day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar endWorkHour = Calendar.getInstance();
        Calendar startDay =  (Calendar)day.clone();
        Calendar endDay =  (Calendar)day.clone();
        startDay = setStartofDay(startDay);
        endDay = setEndofDay(endDay);

        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    //TODO: What if there are more than 1 entry in the same day?
                    endWorkHour.setTime(dateFormat.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    endWorkHour = null;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            endWorkHour = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return endWorkHour;
    }

    public float getTotalHoursinMonth(int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        float result = 0f;
        Date begin = new Date();
        Date end = new Date();
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay = setStartofDay(startDay);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay = setEndofDay(endDay);

        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(cursor.getColumnIndex(WORKINGHOURS_COLUMN_BEGINSHIFT)));
                    end = dateFormat.parse(cursor.getString(cursor.getColumnIndex(WORKINGHOURS_COLUMN_ENDSHIFT)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                result += (float)Math.round((((float)end.getTime() - (float)begin.getTime())/3600000)*4)/4;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if(cursor!=null) {
            cursor.close();
        }
        return result;
    }

    public float getAllHoursFromTo(Calendar From, Calendar To) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date begin = new Date();
        Date end = new Date();
        float result = 0f;
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME+ " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(From.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(To.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(0));
                    end = dateFormat.parse(cursor.getString(1));
                }catch (ParseException e) {
                    e.printStackTrace();
                }
                result += (float)Math.round((((float)end.getTime() - (float)begin.getTime())/3600000)*4)/4;
                /*result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SATURDAY));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SUNDAY));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_NIGHT));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERTIME));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERWORK));*/
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if(result < 0) {
            result = 0f;
        }
        if(cursor != null) {
            cursor.close();
        }
        return result;
    }

    public float getHoursFromTo(Calendar From, Calendar To, String column) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        float result = 0f;
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(From.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(To.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if(result < 0) {
            result = 0f;
        }
        if(cursor != null) {
            cursor.close();
        }
        return result;
    }

    public float getSundayHoursFromTo(Calendar From, Calendar To) { return getHoursFromTo(From, To, WORKINGHOURS_COLUMN_SUNDAY); }
    public float getNightShiftHoursFromTo(Calendar From, Calendar To) { return getHoursFromTo(From, To, WORKINGHOURS_COLUMN_NIGHT); }
    public float getOverworkHoursFromTo(Calendar From, Calendar To) { return getHoursFromTo(From, To, WORKINGHOURS_COLUMN_OVERWORK); }
    public float getOvertimeHoursFromTo(Calendar From, Calendar To) { return getHoursFromTo(From, To, WORKINGHOURS_COLUMN_OVERTIME); }
    public float getSaturdayHoursFromTo(Calendar From, Calendar To) { return getHoursFromTo(From, To, WORKINGHOURS_COLUMN_SATURDAY); }

    public float getWorkingHoursAll() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        float result = 0f;
        Date begin = new Date();
        Date end = new Date();

        sqldb = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME;
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(0));
                    end = dateFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                result += (float)Math.round((((float)end.getTime() - (float)begin.getTime())/3600000)*4)/4;
                /*result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SATURDAY));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SUNDAY));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_NIGHT));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERTIME));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERWORK));*/
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor!=null) {
            cursor.close();
        }
        return result;
    }

    public float getHoursinMonth(String column, int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        float result = 0f;
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay = setStartofDay(startDay);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay = setEndofDay(endDay);

        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql;
            if (month < 0) {
                sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME;
            } else {
                sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime(" +
                        WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                        WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            }
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0) {
            result = 0f;
        }
        if(cursor!=null) {
            cursor.close();
        }
        return result;
    }

/**
    WORKING HOURS IN MONTH
 */
    public float getSundayHoursinMonth(int month) { return getHoursinMonth(WORKINGHOURS_COLUMN_SUNDAY, month); }
    public float getNightShiftHoursinMonth(int month) { return getHoursinMonth(WORKINGHOURS_COLUMN_NIGHT, month); }
    public float getOverworkHoursinMonth(int month) { return getHoursinMonth(WORKINGHOURS_COLUMN_OVERWORK, month); }
    public float getOvertimeHoursinMonth(int month) { return getHoursinMonth(WORKINGHOURS_COLUMN_OVERTIME, month); }
    public float getSaturdayHoursinMonth(int month) { return getHoursinMonth(WORKINGHOURS_COLUMN_SATURDAY, month); }

    public float getWorkingHoursinMonth(int month) {
        //calculate working hours for whole month
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        float workinghours = 0f;
        Date begin = new Date();
        Date end = new Date();
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay = setStartofDay(startDay);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay = setEndofDay(endDay);
        sqldb = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(0));
                    end = dateFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                workinghours += (float)Math.round((((float)end.getTime() - (float)begin.getTime())/3600000)*4)/4;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor!=null) {
            cursor.close();
        }
        return workinghours;
    }

    /**
    WORKING HOURS ALL DURATION
    */
    public float getSundayHoursAll() { return getHoursinMonth(WORKINGHOURS_COLUMN_SUNDAY, -1); }
    public float getNightShiftHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_NIGHT, -1);
    }
    public float getOverworkHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_OVERWORK, -1);
    }
    public float getOvertimeHoursAll() { return getHoursinMonth(WORKINGHOURS_COLUMN_OVERTIME, -1); }
    public float getSaturdayHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_SATURDAY, -1);
    }

    public float getPaymentinMonth(int month) {
        //calculate payment for whole month
        float whours = 0f;
        float hour_pay = 0f;
        float salary = 0f;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        hour_pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
        whours = getWorkingHoursinMonth(month);
        salary = whours * hour_pay;
        return salary;
    }

    public float getPaymentValueFromOnly(Calendar startDay, String column) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        sqldb = this.getReadableDatabase();
        float result = 0f;
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE date("+PAYMENT_COLUMN_BEGINSHIFT+", 'start of day')=date('"+dateFormat.format(startDay.getTime())+"','start of day')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0)
            result = 0f;
        if (cursor!=null) {
            cursor.close();
        }
        return result;
    }

    public float getPaymentValueFromTo(Calendar startDay, Calendar endDay, String column) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        sqldb = this.getReadableDatabase();
        float result = 0f;
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE datetime(" +
                        PAYMENT_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                        PAYMENT_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";

            //String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE date("+PAYMENT_COLUMN_BEGINSHIFT+", 'start of day')=date('"+dateFormat.format(startDay.getTime())+"','start of day')";
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0)
            result = 0f;
        if (cursor!=null) {
            cursor.close();
        }
        return result;
    }

    public float getEntitledPaymentValueFromOnly(Calendar From) {
        return getPaymentValueFromOnly(From, PAYMENT_COLUMN_ENTITLED_AMOUNT);
    }

    public float getEntitledPaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_ENTITLED_AMOUNT);
    }

    public float getSaturdayPaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_SATURDAY);
    }

    public float getSundayPaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_SUNDAY);
    }

    public float getNightShiftPaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_NIGHT);
    }

    public float getOvertimePaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_OVERTIME);
    }

    public float getOverworkPaymentValueFromTo(Calendar From, Calendar To) {
        return getPaymentValueFromTo(From, To, PAYMENT_COLUMN_OVERWORK);
    }

    /**
    Return the total payment amount of a given data column and a given month
     */
    public float getPaymentValueinMonth(String column, int month, int year) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        boolean bgetAll = false;
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        if (month == -1)
        {
            //Get All history
            bgetAll = true;
        } else {
            if (year > 2000) {
                startDay.set(Calendar.YEAR, year);
                endDay.set(Calendar.YEAR, year);
            }
            startDay.set(Calendar.MONTH, month);
            startDay.set(Calendar.DAY_OF_MONTH, 1);
            startDay.set(Calendar.HOUR_OF_DAY, 0);
            startDay.set(Calendar.MINUTE, 0);
            startDay.set(Calendar.SECOND, 0);
            endDay.set(Calendar.MONTH, month);
            endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDay.set(Calendar.HOUR_OF_DAY, 23);
            endDay.set(Calendar.MINUTE, 59);
            endDay.set(Calendar.SECOND, 59);
        }
        sqldb = this.getReadableDatabase();
        float result = 0f;
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME;
            if (!bgetAll) {
                sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE datetime(" +
                      PAYMENT_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                      PAYMENT_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            }
            cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0)
            result = 0f;
        if (cursor!= null) {
            cursor.close();
        }
        return result;
    }


    /**
    PAYMENT FUNCTIONS IN MONTH
     */
    public float getSundaysPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SUNDAY, month, -1);
    }

    public float getNightShiftsPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_NIGHT, month, -1);
    }

    public float getOverworkPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERWORK, month, -1);
    }

    public float getOvertimePaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERTIME, month, -1);
    }

    public float getSaturdaysPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SATURDAY, month, -1);
    }

    public float getEntitledPaymentinMonth(int month, int year) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_ENTITLED_AMOUNT, month, year);
    }

    /**
        PAYMENT FUNCTIONS FOR ALL DATA
    */
    public float getSundaysPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SUNDAY, -1, -1);
    }

    public float getNightShiftsPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_NIGHT, -1, -1);
    }

    public float getOverworkPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERWORK, -1, -1);
    }

    public float getOvertimePaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERTIME, -1, -1);
    }

    public float getSaturdaysPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SATURDAY, -1, -1);
    }

    public float getEntitledPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_ENTITLED_AMOUNT, -1, -1);
    }

}

