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
import java.util.TimeZone;

/**
 * Created by dimitris on 10/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    //DATABASE
    private SQLiteDatabase sqldb;
    private Context context;
    private static final String DATABASE_NAME = "plirosousosta.db";
    private static final int DATABASE_VERSION = 1;

    private static int overwork_start = 40; //hours per week
    private static int overwork_ends = 45; //hours per week
    private static Float multiplier_overwork = 0.2f; //%
    private static Float multiplier_overtime = 0.8f; //%
    private static Float multiplier_holidays = 0.75f; //%
    private static Float multiplier_nightshifts = 0.25f; //%
    private static Float multiplier_saturdays = 0.25f; //%

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

    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_COLUMN_AMOUNT = "amount";
    public static final String SETTINGS_COLUMN_DATE = "date";
    public static final String SETTINGS_COLUMN_TYPE = "type";

    public static final String WORKINGHOURS_TABLE_NAME = "workinghours";
    public static final String WORKINGHOURS_COLUMN_BEGINSHIFT = "begin";
    public static final String WORKINGHOURS_COLUMN_ENDSHIFT = "end";
    public static final String WORKINGHOURS_COLUMN_SATURDAY = "saturday";
    public static final String WORKINGHOURS_COLUMN_SUNDAY = "sunday";
    public static final String WORKINGHOURS_COLUMN_NIGHT = "night";
    public static final String WORKINGHOURS_COLUMN_OVERTIME = "overtime";
    public static final String WORKINGHOURS_COLUMN_OVERWORK = "overwork";

    public static final String PAYMENT_TABLE_NAME = "payment";
    public static final String PAYMENT_COLUMN_BEGINSHIFT = "beginshift";
    public static final String PAYMENT_COLUMN_ENDSHIFT = "endshift";
    public static final String PAYMENT_COLUMN_ACTUALAMOUNT = "actual_amount";
    public static final String PAYMENT_COLUMN_ENTITLED_AMOUNT = "entitled_amount";
    public static final String PAYMENT_COLUMN_SATURDAY = "saturday";
    public static final String PAYMENT_COLUMN_SUNDAY = "sunday";
    public static final String PAYMENT_COLUMN_NIGHT = "night";
    public static final String PAYMENT_COLUMN_OVERTIME = "overtime";
    public static final String PAYMENT_COLUMN_OVERWORK = "overwork";

    public static final String HOLIDAYS_TABLE_NAME = "holidays";
    public static final String HOLIDAY = "holiday_date";

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
                        " ("+HOLIDAY+" text primary key)"
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
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Add holidays
        sqldb = this.getWritableDatabase();
        //database.beginTransaction();
        ContentValues contentValues = new ContentValues();
        //HOLIDAYS IN GREECE 2018 - 2022
        //ΠΡΩΤΟΧΡΟΝΙΑ
        try {
            contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-01-01")));
            contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-01-01")));
            contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-01-01")));
            contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-01-01")));
            contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-01-01")));

        //ΘΕΟΦΑΝΕΙΑ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-01-06")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-01-06")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-01-06")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-01-06")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-01-06")));
        //ΚΑΘΑΡΑ ΔΕΥΤΕΡΑ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-02-19")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-03-11")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-03-02")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-03-15")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-03-07")));
        //25η ΜΑΡΤΙΟΥ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-03-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-03-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-03-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-03-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-03-25")));
        //ΜΕΓΑΛΗ ΠΑΡΑΣΚΕΥΗ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-04-06")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-04-26")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-04-17")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-04-30")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-04-22")));
        //ΜΕΓΑΛΟ ΣΑΒΒΑΤΟ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-04-07")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-04-27")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-04-18")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-05-01")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-04-23")));
        //ΔΕΥΤΕΡΑ ΤΟΥ ΠΑΣΧΑ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-04-09")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-04-29")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-04-20")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-05-03")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-04-25")));
        //1η ΜΑΙΟΥ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-05-01")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-05-01")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-05-01")));
        //contentValues.put(this.HOLIDAY, dateFormat.format("2021-05-01"))); Same as easter
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-05-01")));
        //15 ΑΥΓΟΥΣΤΟΥ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-08-15")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-08-15")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-08-15")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-08-15")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-08-15")));
        //28η ΟΚΤΩΒΡΙΟΥ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-10-28")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-10-28")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-10-28")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-10-28")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-10-28")));
        //ΧΡΙΣΤΟΥΓΕΝΝA
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-12-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-12-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-12-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-12-25")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-12-25")));
        //ΕΠΟΜΕΝΗ ΧΡΙΣΤΟΥΓΕΝΝΩΝ
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2018-12-26")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2019-12-26")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2020-12-26")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2021-12-26")));
        contentValues.put(HOLIDAY, dateFormat.format(date.parse("2022-12-26")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sqldb.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //database.insert(HOLIDAYS_TABLE_NAME, null, contentValues);
        //database.endTransaction();
    }

    public void setHourWage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        hourly_wage = Float.parseFloat(prefs.getString("paid_hour_key","0"));
    }

    public void initializeNightshiftHours() {
        //String timezone = Calendar.getInstance().getTimeZone().getID();
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone().getDefault();
        int offset = tz.getRawOffset() + tz.getDSTSavings();
        int hourofday = nightshift_start.get(Calendar.HOUR_OF_DAY);
        nightshift_start.set(Calendar.HOUR_OF_DAY, 22);
        /*if ( hourofday < 12) {
            nightshift_start.add(Calendar.HOUR_OF_DAY, -24);
        }*/
        nightshift_start.set(Calendar.MINUTE, 0);
        nightshift_start.set(Calendar.SECOND, 0);

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
        Float d = 0f;
        long millisinhours = 3600000;

        //Initialize calendars for nightshifts
        nightshift_start = (Calendar)beginwork.clone();
        nightshift_end = (Calendar)endwork.clone();
        initializeNightshiftHours();

        //Calculate night shift hours
        if (nightshift_start.getTimeInMillis() > beginwork.getTimeInMillis()) {
            //If begin working hour is before the night shift starting hour
            //Calculate the night shift working hour by subtracting the hour of the start of night shift
            if (nightshift_end.getTimeInMillis() > endwork.getTimeInMillis()) {
                nightshifthours = new Float(endwork.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours;
            } else {
                nightshifthours = new Float(nightshift_end.getTimeInMillis() - nightshift_start.getTimeInMillis()) / millisinhours;
            }
        } else { //Begin work is after night shift start
            if (nightshift_end.getTimeInMillis() > endwork.getTimeInMillis()) {
                nightshifthours = new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours;
            } else {
                nightshifthours = new Float(nightshift_end.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours;
            }
        }
        if (nightshifthours < 0)
            nightshifthours = 0f;
        nightshifthours = round(nightshifthours,2);
        return nightshifthours;
    }

    //Calculate the hours that count as overtime
    public Float getOvertimeHours(Date from, Date to) {
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        Float overtimehours = 0f;
        long millisinhours = 3600000;
        //Overtime in weekdays starts after the 10th hour of work
        long weekDayOvertime = 10 * millisinhours;
        //Overtime in weekend starts after the 8th hour of work
        long weekEndOvertime = 8 * millisinhours;
        boolean weekDay = isWeekDay(beginwork);

        if (weekDay) {
            overtimehours = new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis() - weekDayOvertime) / millisinhours;
        } else {
            overtimehours = new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis() - weekEndOvertime) / millisinhours;
        }
        if (overtimehours < 0)
            overtimehours = 0f;
        return overtimehours;
    }

    //Calculate the hours that are in SATURDAY
    public Float getSaturdaysHours(Date from, Date to) {
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        //TODO: Check END time if it is Saturday or NOT
        long millisinhours = 3600000;
        if (beginwork.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return (new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours);
        } else {
            return 0f;
        }
    }

    //Calculate the hours that count as overwork υπερεργασία
    public Float getOverworkHours(Date from, Date to) {
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        Float overworkhours = 0f;
        long millisinhours = 3600000;

        boolean weekDay = isWeekDay(beginwork);

        if (weekDay) {
            overworkhours = (new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours) - 8f;
            if (overworkhours > 2f) {
                overworkhours = 2f;
            } else if (overworkhours < 0f) {
                overworkhours = 0f;
            }
        } else {
            overworkhours = 0f;
        }

        return overworkhours;
    }

    public Float getHolidaysHours(Date from, Date to) {
        Float holidayHours = 0f;
        long millisinhours = 3600000;
        boolean isHolidaybegin = false;
        boolean isHolidayend = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date holiday = new Date();
        Calendar beginwork = Calendar.getInstance();
        beginwork.setTime(from);
        Calendar endwork = Calendar.getInstance();
        endwork.setTime(to);
        Calendar beginHol = Calendar.getInstance();
        Calendar endHol = Calendar.getInstance();
        Calendar hol = Calendar.getInstance();
        float workinghours = 0;
        sqldb = this.getReadableDatabase();

        if (Calendar.SUNDAY == beginwork.get(Calendar.DAY_OF_WEEK)) {
            isHolidaybegin = true;
        }
        if (Calendar.SUNDAY == endwork.get(Calendar.DAY_OF_WEEK)) {
            isHolidayend = true;
        }

        try {
            String sql = "SELECT * FROM " + HOLIDAYS_TABLE_NAME;
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    holiday = dateFormat.parse(cursor.getString(0));
                    hol.setTime(holiday);
                    if (!isHolidaybegin) {
                        isHolidaybegin = ((beginwork.get(Calendar.YEAR) == hol.get(Calendar.YEAR)) && (beginwork.get(Calendar.MONTH) == hol.get(Calendar.MONTH)) && (beginwork.get(Calendar.DATE) == hol.get(Calendar.DATE)));
                    }
                    if (!isHolidayend) {
                        isHolidayend = ((endwork.get(Calendar.YEAR) == hol.get(Calendar.YEAR)) && (endwork.get(Calendar.MONTH) == hol.get(Calendar.MONTH)) && (endwork.get(Calendar.DATE) == hol.get(Calendar.DATE)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if ((isHolidaybegin) && (isHolidayend)) {
            holidayHours = new Float(endwork.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours;
        } else if (isHolidaybegin) {
            beginHol = (Calendar)beginwork.clone();
            beginHol.set(Calendar.HOUR_OF_DAY, 23);
            beginHol.set(Calendar.MINUTE, 59);
            beginHol.set(Calendar.SECOND, 59);
            holidayHours = new Float(beginHol.getTimeInMillis() - beginwork.getTimeInMillis()) / millisinhours;
        } else if (isHolidayend) {
            endHol = (Calendar)endwork.clone();
            endHol.set(Calendar.HOUR_OF_DAY, 0);
            endHol.set(Calendar.MINUTE, 0);
            endHol.set(Calendar.SECOND, 0);
            holidayHours = new Float(endwork.getTimeInMillis() - endHol.getTimeInMillis()) / millisinhours;
        } else {
            holidayHours = 0f;
        }

        return holidayHours;
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

    public Date getWorkBegin(Date day) {
        return null;
    }

    public void storePayments(Date from, Date to, float normal, float holidays, float saturdays, float nights, float overtime, float overwork) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        float entitled_payment = normal + holidays + nights + overtime + overwork;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = new Date();
        Date end = new Date();
        Calendar cb = Calendar.getInstance();
        Calendar ce = Calendar.getInstance();
        sqldb = this.getReadableDatabase();
        float payment_normal = 0f;
        float hours_normal = 0f;
        float payment_holidays = 0f;
        float hours_holidays = 0f;
        float payment_night = 0f;
        float hours_night = 0f;
        float payment_overwork = 0f;
        float hours_overwork = 0f;
        float payment_overtime = 0f;
        float payment_saturdays = 0f;
        float hours_overtime = 0f;
        float hours_saturday = 0f;
        float payment_entitled = 0f;
        float legal_hourly_pay = 0f;

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") > datetime('" + dateFormat.format(from) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(to) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(0));
                    end = dateFormat.parse(cursor.getString(1));
                    cb.setTime(begin);
                    ce.setTime(end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        //Calculate Hours
        hours_normal = (to.getTime() - from.getTime()) / 3600000;
        hours_holidays = getHolidaysHours(from, to);
        hours_saturday = getSaturdaysHours(from, to);
        hours_night = getNightShiftHours(from, to);
        hours_overtime = getOvertimeHours(from, to);
        hours_overwork = getOverworkHours(from, to);
        //Store working hours values to DB
        storeWorkingHours(from, to, hours_holidays, hours_saturday, hours_night, hours_overtime, hours_overwork);

        //Calculate Payments
        legal_hourly_pay = getLegalHourPayment();
        setHourWage();
        payment_normal = getPayment_Actual(hours_normal);
        payment_holidays = getHolidayPayment(hours_holidays, legal_hourly_pay);
        payment_night = getNightShiftPayment(hours_night, legal_hourly_pay);
        payment_saturdays = getSaturdaysPayment(hours_saturday, hourly_wage);
        payment_overtime = getOvertimePayment(hours_overtime, hourly_wage);
        payment_overwork = getOverworkPayment(hours_overwork, hourly_wage);
        //Store payment values to DB
        storePayments(from, to, payment_normal, payment_holidays, payment_saturdays, payment_night, payment_overtime, payment_overwork);
        payment_entitled = payment_normal + payment_holidays + payment_night + payment_saturdays + payment_overtime + payment_overwork;
        return payment_entitled;
    }

    public float getHolidayPayment(float hours, float legal_pay) {
        return (multiplier_holidays * hours) * legal_pay;
    }

    public float getSaturdaysPayment(float hours, float hourly_pay) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bInHotels = prefs.getBoolean("switch_hotels", false);
        if (bInHotels) {
            return 0;
        } else {
            return (multiplier_saturdays * hours) * hourly_pay;
        }
    }

    public float getNightShiftPayment(float hours, float legal_pay) {
        return (multiplier_nightshifts * hours) * legal_pay;
    }

    public float getOvertimePayment(float hours, float hourly_pay) {
        return (multiplier_overtime * hours) * hourly_pay;
    }

    public float getOverworkPayment(float hours, float hourly_pay) {
        return (multiplier_overwork * hours) * hourly_pay;
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
        Below25 = prefs.getBoolean("switch_below25", false);
        YearsExperience = Integer.parseInt(prefs.getString("years_experience_key","0"));
        //Set Hourly Legal Payment
        LegalPaymentsOver25[0] = 3.52f;
        LegalPaymentsOver25[1] = 3.87f;
        LegalPaymentsOver25[2] = 4.21f;
        LegalPaymentsOver25[3] = 4.57f;
        LegalPaymentsUnder25[0] = 3.07f;
        LegalPaymentsUnder25[1] = 3.37f;

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
        String paidby = prefs.getString("list_paidby_values","0");
        int salary = Integer.parseInt(paidby);
        float pay = 0f;
        switch (salary) {
            case 0:
                pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
                break;
            case 1:
                pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
                //TODO: How calculation differs for per day?
                //pay = pay / 8;
                break;
            case 2:
                pay = Float.parseFloat(prefs.getString("paid_hour_key","0"));
                //TODO: How calculation differs for per month?
                //pay = pay / 160;
                break;
        }

        return pay*hours;
    }

    public float getWorkingHours(Date from, Date to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = new Date();
        Date end = new Date();
        float workinghours = 0;
        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") > datetime('" + dateFormat.format(from) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(to) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(0));
                    end = dateFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                workinghours += end.getTime() - begin.getTime();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return (workinghours/3600000);
    }

    public void storeWorkingHours(Date from, Date to, float holidays, float saturdays, float nights, float overtime, float overwork) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar beginWorkHour = Calendar.getInstance();
        Calendar startDay =  (Calendar)day.clone();
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Calendar endDay =  (Calendar)day.clone();
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);

        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

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

        return beginWorkHour;
    }

    public Calendar getEndWorkingHour(Calendar day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar endWorkHour = Calendar.getInstance();
        Calendar startDay =  (Calendar)day.clone();
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Calendar endDay =  (Calendar)day.clone();
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);

        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

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

        return endWorkHour;
    }

    public float getTotalHoursinMonth(int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        float result = 0f;
        Date begin = new Date();
        Date end = new Date();
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);
        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                try {
                    begin = dateFormat.parse(cursor.getString(cursor.getColumnIndex(WORKINGHOURS_COLUMN_BEGINSHIFT)));
                    end = dateFormat.parse(cursor.getString(cursor.getColumnIndex(WORKINGHOURS_COLUMN_BEGINSHIFT)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                result += (float)Math.round((((float)end.getTime() - (float)begin.getTime())/3600000)*4)/4;
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_NIGHT));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERTIME));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_OVERWORK));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SATURDAY));
                result += cursor.getFloat(cursor.getColumnIndex(WORKINGHOURS_COLUMN_SUNDAY));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public float getHoursinMonth(String column, int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        float result = 0f;
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);
        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return result;
    }
/*
    WORKING HOURS IN MONTH
 */
    public float getSundayHoursinMonth(int month) {
        return getHoursinMonth(WORKINGHOURS_COLUMN_SUNDAY, month);
    }

    public float getNightShiftHoursinMonth(int month) {
        return getHoursinMonth(WORKINGHOURS_COLUMN_NIGHT, month);
    }

    public float getOverworkHoursinMonth(int month) {
        return getHoursinMonth(WORKINGHOURS_COLUMN_OVERWORK, month);
    }

    public float getOvertimeHoursinMonth(int month) {
        return getHoursinMonth(WORKINGHOURS_COLUMN_OVERTIME, month);
    }

    public float getSaturdayHoursinMonth(int month) {
        return getHoursinMonth(WORKINGHOURS_COLUMN_SATURDAY, month);
    }

    public float getWorkingHoursinMonth(int month) {
        //calculate working hours for whole month
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        float workinghours = 0f;
        Date begin = new Date();
        Date end = new Date();
        Calendar startDay =  Calendar.getInstance();
        startDay.set(Calendar.MONTH, month);
        startDay.set(Calendar.DAY_OF_MONTH, 1);
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Calendar endDay =  Calendar.getInstance();
        endDay.set(Calendar.MONTH, month);
        endDay.set(Calendar.DAY_OF_MONTH, startDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);
        sqldb = this.getReadableDatabase();

        try {
            String sql = "SELECT * FROM " + WORKINGHOURS_TABLE_NAME + " WHERE datetime("+
                    WORKINGHOURS_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                    WORKINGHOURS_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

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
        return workinghours;
    }
    /*
    WORKING HOURS ALL DURATION
 */
    public float getSundayHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_SUNDAY, -1);
    }

    public float getNightShiftHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_NIGHT, -1);
    }

    public float getOverworkHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_OVERWORK, -1);
    }

    public float getOvertimeHoursAll() {
        return getHoursinMonth(WORKINGHOURS_COLUMN_OVERTIME, -1);
    }

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

    public float getPaymentValueonDay(Calendar startDay, Calendar endDay) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sqldb = this.getReadableDatabase();
        float result = 0f;

        try {
            String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE datetime(" +
                        PAYMENT_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                        PAYMENT_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(PAYMENT_COLUMN_ENTITLED_AMOUNT));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0)
            result = 0f;
        return result;
    }

    /*
    Return the total payment amount of a given data column and a given month
     */
    public float getPaymentValueinMonth(String column, int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean bgetAll = false;
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        if (month == -1)
        {
            //Get All history
            bgetAll = true;
        } else {
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

        try {
            String sql = "SELECT * FROM " + PAYMENT_TABLE_NAME;
            if (!bgetAll) {
                sql = "SELECT * FROM " + PAYMENT_TABLE_NAME + " WHERE datetime(" +
                      PAYMENT_COLUMN_BEGINSHIFT + ") >= datetime('" + dateFormat.format(startDay.getTime()) + "') AND datetime(" +
                      PAYMENT_COLUMN_ENDSHIFT + ") <= datetime('" + dateFormat.format(endDay.getTime()) + "')";
            }
            Cursor cursor = sqldb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                result += cursor.getFloat(cursor.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (result < 0)
            result = 0f;
        return result;
    }


    /*
    PAYMENT FUNCTIONS IN MONTH
     */
    public float getSundaysPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SUNDAY, month);
    }

    public float getNightShiftsPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_NIGHT, month);
    }

    public float getOverworkPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERWORK, month);
    }

    public float getOvertimePaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERTIME, month);
    }

    public float getSaturdaysPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SATURDAY, month);
    }

    public float getEntitledPaymentinMonth(int month) {
        return getPaymentValueinMonth(PAYMENT_COLUMN_ENTITLED_AMOUNT, month);
    }

    /*
        PAYMENT FUNCTIONS FOR ALL DATA
    */
    public float getSundaysPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SUNDAY, -1);
    }

    public float getNightShiftsPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_NIGHT, -1);
    }

    public float getOverworkPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERWORK, -1);
    }

    public float getOvertimePaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_OVERTIME, -1);
    }

    public float getSaturdaysPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_SATURDAY, -1);
    }

    public float getEntitledPaymentAll() {
        return getPaymentValueinMonth(PAYMENT_COLUMN_ENTITLED_AMOUNT, -1);
    }

}

