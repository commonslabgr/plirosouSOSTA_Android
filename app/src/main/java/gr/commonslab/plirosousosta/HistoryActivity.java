package gr.commonslab.plirosousosta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *  Name: HistoryActivity.java
 *  Description: Implements the "History" screen on the PlirosouSOSTA Android App.
 *  A tabbed activity to display the history of entitled payments according to the recorded working hours/ payments.
 *  The four tabs are: current month, current year, from-to and all.
 *  From-To tab spawns two date pickers for the user to select the starting and ending dates.
 *
 *  Company: commons|lab
 *  Author: Dimitris Koukoulakis
 *  License: General Public Licence v3.0 GPL
*/

public class HistoryActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private static Calendar FromDate;
    private static Calendar ToDate;
    public static boolean bToDate = false;
    private static MonthFragment tab1;
    private static YearFragment tab2;
    private static FromToFragment tab3;
    private static AllFragment tab4;
    private static boolean bFromToVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomViewPager mViewPager;
        TabLayout tabLayout;

        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container_history);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs_history);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        FromDate = Calendar.getInstance();
        ToDate = Calendar.getInstance();

        tab1 = new MonthFragment().newInstance(0);
        tab2 = new YearFragment().newInstance(1);
        tab3 = new FromToFragment().newInstance(2);
        tab4 = new AllFragment().newInstance(3);
    }

    /**
     * A placeholder fragment containing Month view.
     */
    public static class MonthFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public MonthFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MonthFragment newInstance(int sectionNumber) {
            MonthFragment fragment = new MonthFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_month_history, container, false);
            setMonthValues(rootView);

            return rootView;
        }
    }

    public static class YearFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public YearFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static YearFragment newInstance(int sectionNumber) {
            YearFragment fragment = new YearFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_year_history, container, false);
            setAnnualValues(rootView);

            return rootView;
        }
    }

    /**
     * A placeholder fragment containing FromTo view.
     */
    public static class FromToFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FromToFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FromToFragment newInstance(int sectionNumber) {
            FromToFragment fragment = new FromToFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fromto_history, container, false);

            bFromToVisible = true;
            if(isVisible()) {
                showDatePicker(getActivity());
            }
            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            bFromToVisible = false;
        }

        @Override
        public void onStop() {
            super.onStop();
            bFromToVisible = false;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            bFromToVisible = false;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if ((bFromToVisible) && (isVisibleToUser)) {
                showDatePicker(getActivity());
            }
        }
    }

    /**
     * A placeholder fragment containing All view.
     */
    public static class AllFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AllFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AllFragment newInstance(int sectionNumber) {
            AllFragment fragment = new AllFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_all_history, container, false);
            setAllValues(rootView);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position);
            switch (position) {
                case 0:
                    return tab1;
                case 1:
                    return tab2;
                case 2:
                    return tab3;
                case 3:
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar cal = Calendar.getInstance();
            String currentMonth;
            String currentYear;

            currentMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            currentYear =  Integer.toString(cal.get(Calendar.YEAR));
            switch (position) {
                case 0:
                    return currentMonth;
                case 1:
                    return currentYear;
                case 2:
                    return getString(R.string.tab_title_from_to);
                case 3:
                    return getString(R.string.tab_title_all);
            }
            return null;
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
            if (bToDate) {
                dp.setTitle("ΕΩΣ");
            } else {
                dp.setTitle("ΑΠΟ");
            }
            return dp;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (bToDate) {
                ToDate.set(Calendar.HOUR, 0);
                ToDate.set(Calendar.MINUTE, 0);
                ToDate.set(Calendar.SECOND, 0);
                ToDate.set(Calendar.YEAR, year);
                ToDate.set(Calendar.MONTH, month);
                ToDate.set(Calendar.DAY_OF_MONTH, day);
                if (ToDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                    Context context = view.getContext();
                    CharSequence text = "Please select a past or present date.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    DialogFragment TodateFragment;
                    TodateFragment = new DatePickerFragment();
                    TodateFragment.show(getFragmentManager(), "datePicker");
                } else if (ToDate.getTimeInMillis() <= FromDate.getTimeInMillis()) {
                    Context context = view.getContext();
                    CharSequence text = "Please select a date that is after the first selected date.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    //this.show(getFragmentManager(), "datePicker");
                    DialogFragment TodateFragment;
                    TodateFragment = new DatePickerFragment();
                    TodateFragment.show(getFragmentManager(), "datePicker");
                } else {
                    bToDate = false;
                    setFromToValues(getActivity(), FromDate, ToDate);
                }
            } else {
                FromDate.set(Calendar.HOUR, 0);
                FromDate.set(Calendar.MINUTE, 0);
                FromDate.set(Calendar.SECOND, 0);
                FromDate.set(Calendar.YEAR, year);
                FromDate.set(Calendar.MONTH, month);
                FromDate.set(Calendar.DAY_OF_MONTH, day);
                if (FromDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                    Context context = view.getContext();
                    CharSequence text = "Please select a past date.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    DialogFragment FromdateFragment;
                    FromdateFragment = new DatePickerFragment();
                    FromdateFragment.show(getFragmentManager(), "datePicker");
                } else {
                    bToDate = true;
                    DialogFragment TodateFragment;
                    TodateFragment = new DatePickerFragment();
                    TodateFragment.show(getFragmentManager(), "datePicker");
                }
            }
        }
    }

    public static void showDatePicker(FragmentActivity activity){
        DialogFragment dateFragment;
        dateFragment = new DatePickerFragment();
        if (activity != null) {
            dateFragment.show(activity.getFragmentManager(), "datePicker");
        }
    }

    public static int getMinutesinHour(float hours) {
        int minutes = (int)((hours % 1)*60);
        return minutes;
    }

    /**
     * set values to current month tab on History Activity
     * @param rootView
     */
    public static void setMonthValues(View rootView) {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();
        float hours;
        int minutes;
        float amount;

        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());

        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        amount = dbHelper.getEntitledPaymentinMonth(month, -1);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);

        TextView text_workhours = rootView.findViewById(R.id.text_workhours);
        hours = dbHelper.getTotalHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά", hours, minutes);
        text_workhours.setText(s);

        TableLayout tl = rootView.findViewById(R.id.table);
        boolean evenrow = false;
        Calendar cal = Calendar.getInstance();
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar nextDay = Calendar.getInstance();

        for (int i=0; i < maxDays; i++) {
            cal.set(Calendar.DAY_OF_MONTH,i+1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 1);
            startDate.setTime(cal.getTimeInMillis());
            nextDay.set(Calendar.DAY_OF_MONTH,i+1);
            nextDay.set(Calendar.HOUR_OF_DAY, 23);
            nextDay.set(Calendar.MINUTE, 59);
            nextDay.set(Calendar.SECOND, 59);
            endDate.setTime(nextDay.getTimeInMillis());

            if (i%2 == 0) {
                evenrow = true;
            } else {
                evenrow = false;
            }
            TableRow row = new TableRow(rootView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,5,60,0);

            row.setLayoutParams(lp);
            TextView date = new TextView(rootView.getContext());
            TextView time = new TextView(rootView.getContext());
            TextView amountPay = new TextView(rootView.getContext());
            if (evenrow) {
                date.setBackgroundColor(0XFFDADADA);
                time.setBackgroundColor(0XFFDADADA);
                amountPay.setBackgroundColor(0XFFDADADA);
            } else {
                date.setBackgroundColor(0XFFFFFFFF);
                time.setBackgroundColor(0XFFFFFFFF);
                amountPay.setBackgroundColor(0XFFFFFFFF);
            }
            amountPay.setTextColor(0XFFE8646F);

            date.setText(dateFormat.format(startDate));
            date.setPadding(100,5,10,5);
            time.setPadding(100,5,10,5);
            amountPay.setPadding(100,5,50,5);
            hours = dbHelper.getWorkingHours(startDate,endDate);
            minutes = getMinutesinHour(hours);
            s = String.format(Locale.getDefault(),"%.0fω %dλ", hours, minutes);
            time.setText(s);
            amount = dbHelper.getEntitledPaymentValueFromTo(cal,nextDay);
            amountPay.setText(String.format(Locale.getDefault(),"€%.2f",amount));

            row.addView(date);
            row.addView(time);
            row.addView(amountPay);
            tl.addView(row);
        }
    }

    /**
     * set values to "From-To" tab on History Activity
     * @param activity
     * @param From
     * @param to
     */

    public static void setFromToValues(Activity activity, Calendar From, Calendar to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        View rootView = activity.findViewById(R.id.main_content_fromto_history);
        float hours;
        int minutes;
        float amount;

        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();

        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        amount = dbHelper.getEntitledPaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);

        TextView text_workhours = rootView.findViewById(R.id.text_workhours);
        hours = dbHelper.getWorkingHours(From.getTime(), to.getTime());
        minutes = getMinutesinHour(hours);
        s = String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά", hours, minutes);
        text_workhours.setText(s);

        TableLayout tl = rootView.findViewById(R.id.table);

        boolean evenrow = false;
        Calendar cStart = Calendar.getInstance();
        Calendar cNextDay = Calendar.getInstance();

        int maxDays = 0;
        if (From.get(Calendar.YEAR) != to.get(Calendar.YEAR))
        {
            cStart = (Calendar)From.clone();
            cStart.set(Calendar.MONTH, 11);
            cStart.set(Calendar.DAY_OF_MONTH, 31);
            maxDays = cStart.get(Calendar.DAY_OF_YEAR) - From.get(Calendar.DAY_OF_YEAR);
            maxDays += to.get(Calendar.DAY_OF_YEAR);
        } else {
            maxDays = to.get(Calendar.DAY_OF_YEAR) - From.get(Calendar.DAY_OF_YEAR);
        }

        cStart.setTime(From.getTime());
        cNextDay.setTime(From.getTime());
        for (int i=0; i < maxDays; i++) {
            cStart.add(Calendar.DAY_OF_YEAR, 1);
            cStart.set(Calendar.HOUR_OF_DAY, 0);
            cStart.set(Calendar.MINUTE, 0);
            cStart.set(Calendar.SECOND, 1);
            cNextDay.add(Calendar.DAY_OF_YEAR, 1);
            cNextDay.set(Calendar.HOUR_OF_DAY, 23);
            cNextDay.set(Calendar.MINUTE, 59);
            cNextDay.set(Calendar.SECOND, 59);

            if (i%2 == 0) {
                evenrow = true;
            } else {
                evenrow = false;
            }
            TableRow row = new TableRow(rootView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,5,60,0);

            row.setLayoutParams(lp);
            TextView date = new TextView(rootView.getContext());
            TextView time = new TextView(rootView.getContext());
            TextView amountPay = new TextView(rootView.getContext());
            if (evenrow) {
                date.setBackgroundColor(0XFFDADADA);
                time.setBackgroundColor(0XFFDADADA);
                amountPay.setBackgroundColor(0XFFDADADA);
            } else {
                date.setBackgroundColor(0XFFFFFFFF);
                time.setBackgroundColor(0XFFFFFFFF);
                amountPay.setBackgroundColor(0XFFFFFFFF);
            }
            amountPay.setTextColor(0XFFE8646F);

            date.setText(dateFormat.format(cStart.getTime()));
            date.setPadding(100,5,10,5);
            time.setPadding(100,5,10,5);
            amountPay.setPadding(100,5,50,5);
            hours = dbHelper.getWorkingHours(cStart.getTime(),cNextDay.getTime());
            minutes = getMinutesinHour(hours);
            s = String.format(Locale.getDefault(),"%.0fω %dλ", hours, minutes);
            time.setText(s);
            amount = dbHelper.getEntitledPaymentValueFromTo(cStart,cNextDay);
            amountPay.setText(String.format(Locale.getDefault(),"€%.2f",amount));

            row.addView(date);
            row.addView(time);
            row.addView(amountPay);
            tl.addView(row);
        }
    }

    /**
     * set values on current year tab on History Activity
     * @param rootView
     */
    public static void setAnnualValues(View rootView) {
        float hours = 0f;
        int minutes;
        float amount = 0f;

        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());

        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        for (int i = 0; i < 12; i++) {
            amount += dbHelper.getEntitledPaymentinMonth(i, -1);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);

        TextView text_workhours = rootView.findViewById(R.id.text_workhours);
        for (int i = 0; i < 12; i++) {
            hours += dbHelper.getTotalHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά", hours, minutes);
        text_workhours.setText(s);

        TableLayout tl = rootView.findViewById(R.id.table);
        boolean evenrow = false;
        Calendar cal = Calendar.getInstance();

        for (int i=0; i < 12; i++) {
            if (i%2 == 0) {
                evenrow = true;
            } else {
                evenrow = false;
            }
            TableRow row = new TableRow(rootView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,5,60,0);

            row.setLayoutParams(lp);
            TextView date = new TextView(rootView.getContext());
            TextView time = new TextView(rootView.getContext());
            TextView amountPay = new TextView(rootView.getContext());
            if (evenrow) {
                date.setBackgroundColor(0XFFDADADA);
                time.setBackgroundColor(0XFFDADADA);
                amountPay.setBackgroundColor(0XFFDADADA);
            } else {
                date.setBackgroundColor(0XFFFFFFFF);
                time.setBackgroundColor(0XFFFFFFFF);
                amountPay.setBackgroundColor(0XFFFFFFFF);
            }
            amountPay.setTextColor(0XFFE8646F);

            date.setText(String.format(Locale.getDefault(),"%02d-%d",(i+1),cal.get(Calendar.YEAR)));
            date.setPadding(100,5,10,5);
            time.setPadding(100,5,10,5);
            amountPay.setPadding(100,5,50,5);
            hours = dbHelper.getWorkingHoursinMonth(i);
            minutes = getMinutesinHour(hours);
            s = String.format(Locale.getDefault(),"%.0fω %dλ", hours, minutes);
            time.setText(s);
            amount = dbHelper.getEntitledPaymentinMonth(i, -1);
            amountPay.setText(String.format(Locale.getDefault(),"€%.2f",amount));

            row.addView(date);
            row.addView(time);
            row.addView(amountPay);
            tl.addView(row);
        }
    }

    /**
     * set values on "ALL" tab of History Activity
     * @param rootView
     */
    public static void setAllValues(View rootView) {
        float hours = 0f;
        int minutes = 0;
        float amount = 0f;

        String s = "";
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();

        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        amount = dbHelper.getEntitledPaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);

        TextView text_workhours = rootView.findViewById(R.id.text_workhours);
        hours = dbHelper.getWorkingHours(null, null);
        minutes = getMinutesinHour(hours);
        s = String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά", hours, minutes);
        text_workhours.setText(s);

        TableLayout tl = rootView.findViewById(R.id.table);
        boolean evenrow = false;

        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();
        cStart.setTime(dbHelper.getFirstWorkingDateTime());
        cEnd.setTime(dbHelper.getLastWorkingDateTime());
        int years = cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR);
        int months = (years * 12) + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH) + 1;

        for (int i=0; i < months; i++) {
            if (i%2 == 0) {
                evenrow = true;
            } else {
                evenrow = false;
            }

            TableRow row = new TableRow(rootView.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(60,5,60,0);

            row.setLayoutParams(lp);
            TextView date = new TextView(rootView.getContext());
            TextView time = new TextView(rootView.getContext());
            TextView amountPay = new TextView(rootView.getContext());
            if (evenrow) {
                date.setBackgroundColor(0XFFDADADA);
                time.setBackgroundColor(0XFFDADADA);
                amountPay.setBackgroundColor(0XFFDADADA);
            } else {
                date.setBackgroundColor(0XFFFFFFFF);
                time.setBackgroundColor(0XFFFFFFFF);
                amountPay.setBackgroundColor(0XFFFFFFFF);
            }
            amountPay.setTextColor(0XFFE8646F);

            date.setText(String.format(Locale.getDefault(),"%02d-%d",(cStart.get(Calendar.MONTH)+1),cStart.get(Calendar.YEAR)));
            date.setPadding(100,5,10,5);
            time.setPadding(100,5,10,5);
            amountPay.setPadding(100,5,50,5);
            hours = dbHelper.getWorkingHoursinMonth(cStart.get(Calendar.MONTH));
            minutes = getMinutesinHour(hours);
            s = String.format(Locale.getDefault(),"%.0fω %dλ", hours, minutes);
            time.setText(s);
            amount = dbHelper.getEntitledPaymentinMonth(cStart.get(Calendar.MONTH), cStart.get(Calendar.YEAR));
            amountPay.setText(String.format(Locale.getDefault(),"€%.2f",amount));

            row.addView(date);
            row.addView(time);
            row.addView(amountPay);
            tl.addView(row);

            cStart.add(Calendar.MONTH, 1);
        }
    }
}
