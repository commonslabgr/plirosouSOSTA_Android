package gr.commonslab.plirosousosta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 *  Name: EntitledActivity.java
 *  Description: Implements the "Entitled" screen on the PlirosouSOSTA Android App.
 *  The screen includes four tabs, current month, current year, from - to, and all.
 *  Each tab displays the total entitled amount, the hours and amount for Sundays, Saturdays, Nights and Overtime
 *
 *  Company: commons|lab
 *  Author: Dimitris Koukoulakis
 *  License: General Public Licence v3.0 GPL
 */

public class EntitledActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private static Calendar FromDate;
    private static Calendar ToDate;
    public static boolean bToDate = false;
    private static Button button_info;
    private static MonthFragment tab1;
    private static YearFragment tab2;
    private static FromToFragment tab3;
    private static AllFragment tab4;
    private static boolean bFromToVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SectionsPagerAdapter mSectionsPagerAdapter;
        CustomViewPager mViewPager;
        TabLayout tabLayout;
        setContentView(R.layout.activity_entitled);

        Toolbar toolbar = findViewById(R.id.main_toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container_entitled);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs_entitled);
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

        public MonthFragment() { }

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
            View rootView = inflater.inflate(R.layout.fragment_month_entitled, container, false);
            setMonthValues(rootView);
            final AlertDialog infodialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom).create();

            infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            button_info = rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    //Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    //v.getContext().startActivity(intent_info);
                    infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_whatcanido)));
                    infodialog.show();
                }
            });

            return rootView;
        }
    }

    public static class YearFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public YearFragment() {}

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
            View rootView = inflater.inflate(R.layout.fragment_year_entitled, container, false);
            setAnnualValues(rootView);
            final AlertDialog infodialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom).create();

            infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            button_info = rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    //Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    //v.getContext().startActivity(intent_info);
                    infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_whatcanido)));
                    infodialog.show();
                }
            });

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
        private static boolean bFirstRunFromTo = false;
        public FromToFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FromToFragment newInstance(int sectionNumber) {
            FromToFragment fragment = new FromToFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            bFirstRunFromTo = true;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fromto_entitled, container, false);
            final AlertDialog infodialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom).create();

            infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            button_info = rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    //Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    //v.getContext().startActivity(intent_info);
                    infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_whatcanido)));
                    infodialog.show();
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            if ((bFirstRunFromTo && getUserVisibleHint()) || (bFromToVisible && getUserVisibleHint())) {
                bFirstRunFromTo = false;
                showDatePicker(getActivity());
            }
            bFromToVisible = true;
        }

        @Override
        public void onStart() {
            super.onStart();
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

        @Override
        public void onHiddenChanged(boolean hidden) {
            super.onHiddenChanged(hidden);
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

        public AllFragment() {}

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
            View rootView = inflater.inflate(R.layout.fragment_all_entitled, container, false);
            setAllValues(rootView);
            final AlertDialog infodialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom).create();

            infodialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            button_info = rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    //Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    //v.getContext().startActivity(intent_info);
                    infodialog.setMessage(Html.fromHtml(getString(R.string.text_info_whatcanido)));
                    infodialog.show();
                }
            });

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
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String currentMonth;
            String currentYear;
            Calendar cal = Calendar.getInstance();
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

    public static void showDatePicker(Activity activity){
        DialogFragment dateFragment;
        dateFragment = new DatePickerFragment();
        if(activity != null) {
            dateFragment.show(activity.getFragmentManager(), "datePicker");
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
            //TODO: REMOVE SET MAX
            // dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            if (bToDate) {
                dp.setTitle(getString(R.string.picker_title_to));
            } else {
                dp.setTitle(getString(R.string.picker_title_from));
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
                    CharSequence text = getString(R.string.warning_date);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    DialogFragment TodateFragment;
                    TodateFragment = new DatePickerFragment();
                    TodateFragment.show(getFragmentManager(), "datePicker");
                } else if (ToDate.getTimeInMillis() <= FromDate.getTimeInMillis()) {
                    Context context = view.getContext();
                    CharSequence text = getString(R.string.warning_after_date);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
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
                    CharSequence text = getString(R.string.warning_date);
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

        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            bToDate = false;
        }
    }

    public static int getMinutesinHour(float hours) {
        return (int)((hours % 1)*60);
    }

    /**
     * Retrieve and set specified period values in tab FROM-TO in Entitled Activity
     */
    public static void setFromToValues(Activity activity, Calendar From, Calendar to) {
        View rootView = activity.findViewById(R.id.main_content_fromto_entitled);
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        float hours;
        int minutes;
        float amount;
        String s;

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        //amount = dbHelper.getEntitledPaymentValueFromTo(From, to);

        TextView text_total_hours = rootView.findViewById(R.id.text_should_get);
        hours = dbHelper.getAllHoursFromTo(From, to);
        amount = dbHelper.getPayment_Actual(hours);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);
        minutes = getMinutesinHour(hours);
        s = text_total_hours.getText().toString();
        //s = s.replaceAll("0 ΩΡΕΣ ΚΑΙ 0 ΛΕΠΤΑ",String.format(Locale.getDefault(),"%.0f ΩΡΕΣ ΚΑΙ %d ΛΕΠΤΑ",hours,minutes));
        s = setHoursandMinutesLongString(hours, minutes, s);
        text_total_hours.setText(s);

        //BOTTOM
        TextView text_bottom = rootView.findViewById(R.id.text_should_getpaid);
        s = text_bottom.getText().toString();
        amount = dbHelper.getEntitledPaymentValueFromTo(From, to);
        s = s.replaceAll("€",String.format(Locale.getDefault(),"<font color=\"#FFFFFF\">€%.2f</font>", amount));
        text_bottom.setText(Html.fromHtml(s));

        //SUNDAYS & HOLIDAYS
        TextView text_sunday_hours = rootView.findViewById(R.id.text_sunday_hours);
        hours = dbHelper.getSundayHoursFromTo(From, to);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_sunday_hours.setText(s);

        TextView text_sunday_amount = rootView.findViewById(R.id.text_sunday_amount);
        amount = dbHelper.getSundayPaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_sunday_amount.setText(s);

        //SATURDAYS
        TextView text_saturday_hours = rootView.findViewById(R.id.text_saturday_hours);
        hours = dbHelper.getSaturdayHoursFromTo(From, to);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_saturday_hours.setText(s);

        TextView text_saturday_amount = rootView.findViewById(R.id.text_saturday_amount);
        amount = dbHelper.getSaturdayPaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_saturday_amount.setText(s);

        //NIGHT SHIFTS
        TextView text_night_hours = rootView.findViewById(R.id.text_night_hours);
        hours = dbHelper.getNightShiftHoursFromTo(From, to);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_night_hours.setText(s);

        TextView text_night_amount = rootView.findViewById(R.id.text_night_amount);
        amount = dbHelper.getNightShiftPaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_night_amount.setText(s);

        //OVERTIME
        TextView text_overtime_hours = rootView.findViewById(R.id.text_overtime_hours);
        hours = dbHelper.getOvertimeHoursFromTo(From, to);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overtime_hours.setText(s);

        TextView text_overtime_amount = rootView.findViewById(R.id.text_overtime_amount);
        amount = dbHelper.getOvertimePaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overtime_amount.setText(s);

        //OVERWORK
        TextView text_overwork_hours = rootView.findViewById(R.id.text_overwork_hours);
        hours = dbHelper.getOverworkHoursFromTo(From, to);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overwork_hours.setText(s);

        TextView text_overwork_amount = rootView.findViewById(R.id.text_overwork_amount);
        amount = dbHelper.getOverworkPaymentValueFromTo(From, to);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overwork_amount.setText(s);
    }

    /**
     * Retrieve and set current ANNUAL values in tab YEAR in Entitled Activity
     */
    public static void setAnnualValues(View rootView) {
        float hours = 0f;
        int minutes;
        float amount = 0f;
        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        /*
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getEntitledPaymentinMonth(i, -1);
        }*/

        TextView text_total_hours = rootView.findViewById(R.id.text_should_get);
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getWorkingHoursinMonth(i);
        }
        amount = dbHelper.getPayment_Actual(hours);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);
        minutes = getMinutesinHour(hours);
        s = text_total_hours.getText().toString();
        //s = s.replaceAll("0 ώρες και 0 λεπτά",String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά",hours,minutes));
        //s = s.replaceAll("0 ΩΡΕΣ ΚΑΙ 0 ΛΕΠΤΑ",String.format(Locale.getDefault(),"%.0f ΩΡΕΣ ΚΑΙ %d ΛΕΠΤΑ",hours,minutes));
        s = setHoursandMinutesLongString(hours, minutes, s);
        text_total_hours.setText(s);

        //BOTTOM
        TextView text_bottom = rootView.findViewById(R.id.text_should_getpaid);
        s = text_bottom.getText().toString();
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getEntitledPaymentinMonth(i, -1);
        }
        s = s.replaceAll("€",String.format(Locale.getDefault(),"<font color=\"#FFFFFF\">€%.2f</font>", amount));
        text_bottom.setText(Html.fromHtml(s));

        //SUNDAYS & HOLIDAYS
        TextView text_sunday_hours = rootView.findViewById(R.id.text_sunday_hours);
        hours = 0;
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getSundayHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_sunday_hours.setText(s);

        TextView text_sunday_amount = rootView.findViewById(R.id.text_sunday_amount);
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getSundaysPaymentinMonth(i);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_sunday_amount.setText(s);

        //SATURDAYS
        TextView text_saturday_hours = rootView.findViewById(R.id.text_saturday_hours);
        hours = 0;
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getSaturdayHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_saturday_hours.setText(s);

        TextView text_saturday_amount = rootView.findViewById(R.id.text_saturday_amount);
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getSaturdaysPaymentinMonth(i);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_saturday_amount.setText(s);

        //NIGHT SHIFTS
        TextView text_night_hours = rootView.findViewById(R.id.text_night_hours);
        hours = 0;
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getNightShiftHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_night_hours.setText(s);

        TextView text_night_amount = rootView.findViewById(R.id.text_night_amount);
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getNightShiftsPaymentinMonth(i);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_night_amount.setText(s);

        //OVERTIME
        TextView text_overtime_hours = rootView.findViewById(R.id.text_overtime_hours);
        hours = 0;
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getOvertimeHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overtime_hours.setText(s);

        TextView text_overtime_amount = rootView.findViewById(R.id.text_overtime_amount);
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getOvertimePaymentinMonth(i);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overtime_amount.setText(s);

        //OVERWORK
        TextView text_overwork_hours = rootView.findViewById(R.id.text_overwork_hours);
        hours = 0;
        for (int i=0; i < 12; i++) {
            hours += dbHelper.getOverworkHoursinMonth(i);
        }
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overwork_hours.setText(s);

        TextView text_overwork_amount = rootView.findViewById(R.id.text_overwork_amount);
        amount = 0;
        for (int i=0; i < 12; i++) {
            amount += dbHelper.getOverworkPaymentinMonth(i);
        }
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overwork_amount.setText(s);
    }

    /**
    Retrieve and set current MONTH values in tab MONTH in Entitled Activity
     */
    public static void setMonthValues(View rootView) {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        float hours;
        int minutes;
        float amount;
        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);

        TextView text_total_hours = rootView.findViewById(R.id.text_should_get);
        hours = dbHelper.getWorkingHoursinMonth(month);
        amount = dbHelper.getPayment_Actual(hours);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);
        minutes = getMinutesinHour(hours);
        s = text_total_hours.getText().toString();
        //s = s.replaceAll("0 ώρες και 0 λεπτά",String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά",hours,minutes));
        s = setHoursandMinutesLongString(hours, minutes, s);
        text_total_hours.setText(s);

        //BOTTOM
        TextView text_bottom = rootView.findViewById(R.id.text_should_getpaid);
        s = text_bottom.getText().toString();
        amount = dbHelper.getEntitledPaymentinMonth(month, -1);
        s = s.replaceAll("€",String.format(Locale.getDefault(),"<font color=\"#FFFFFF\">€%.2f</font>", amount));
        //s = s.replaceAll("€",String.format(Locale.getDefault(),"€%.2f", amount));
        text_bottom.setText(Html.fromHtml(s));

        //SUNDAYS & HOLIDAYS
        TextView text_sunday_hours = rootView.findViewById(R.id.text_sunday_hours);
        hours = dbHelper.getSundayHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_sunday_hours.setText(s);

        TextView text_sunday_amount = rootView.findViewById(R.id.text_sunday_amount);
        amount = dbHelper.getSundaysPaymentinMonth(month);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_sunday_amount.setText(s);

        //SATURDAYS
        TextView text_saturday_hours = rootView.findViewById(R.id.text_saturday_hours);
        hours = dbHelper.getSaturdayHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_saturday_hours.setText(s);

        TextView text_saturday_amount = rootView.findViewById(R.id.text_saturday_amount);
        amount = dbHelper.getSaturdaysPaymentinMonth(month);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_saturday_amount.setText(s);

        //NIGHT SHIFTS
        TextView text_night_hours = rootView.findViewById(R.id.text_night_hours);
        hours = dbHelper.getNightShiftHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_night_hours.setText(s);

        TextView text_night_amount = rootView.findViewById(R.id.text_night_amount);
        amount = dbHelper.getNightShiftsPaymentinMonth(month);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_night_amount.setText(s);

        //OVERTIME
        TextView text_overtime_hours = rootView.findViewById(R.id.text_overtime_hours);
        hours = dbHelper.getOvertimeHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overtime_hours.setText(s);

        TextView text_overtime_amount = rootView.findViewById(R.id.text_overtime_amount);
        amount = dbHelper.getOvertimePaymentinMonth(month);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overtime_amount.setText(s);

        //OVERWORK
        TextView text_overwork_hours = rootView.findViewById(R.id.text_overwork_hours);
        hours = dbHelper.getOverworkHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overwork_hours.setText(s);

        TextView text_overwork_amount = rootView.findViewById(R.id.text_overwork_amount);
        amount = dbHelper.getOverworkPaymentinMonth(month);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overwork_amount.setText(s);
    }

    /**
    Retrieve and set values for tab "ALL" in entitled Activity
     */
    public static void setAllValues(View rootView) {
        float hours;
        int minutes;
        float amount;
        String s;
        DBHelper dbHelper = new DBHelper(rootView.getContext());

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);

        TextView text_total_hours = rootView.findViewById(R.id.text_should_get);
        hours = dbHelper.getWorkingHoursAll();
        amount = dbHelper.getPayment_Actual(hours);
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_total_amount.setText(s);
        minutes = getMinutesinHour(hours);
        s = text_total_hours.getText().toString();
        //s = s.replaceAll("0 ώρες και 0 λεπτά",String.format(Locale.getDefault(),"%.0f ώρες και %d λεπτά",hours,minutes));
        s = setHoursandMinutesLongString(hours, minutes, s);
        text_total_hours.setText(s);

        //BOTTOM
        TextView text_bottom = rootView.findViewById(R.id.text_should_getpaid);
        s = text_bottom.getText().toString();
        amount = dbHelper.getEntitledPaymentAll();
        s = s.replaceAll("€",String.format(Locale.getDefault(),"<font color=\"#FFFFFF\">€%.2f</font>", amount));
        text_bottom.setText(Html.fromHtml(s));

        //SUNDAYS & HOLIDAYS
        TextView text_sunday_hours = rootView.findViewById(R.id.text_sunday_hours);
        hours = dbHelper.getSundayHoursAll();
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_sunday_hours.setText(s);

        TextView text_sunday_amount = rootView.findViewById(R.id.text_sunday_amount);
        amount = dbHelper.getSundaysPaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_sunday_amount.setText(s);

        //SATURDAYS
        TextView text_saturday_hours = rootView.findViewById(R.id.text_saturday_hours);
        hours = dbHelper.getSaturdayHoursAll();
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_saturday_hours.setText(s);

        TextView text_saturday_amount = rootView.findViewById(R.id.text_saturday_amount);
        amount = dbHelper.getSaturdaysPaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_saturday_amount.setText(s);

        //NIGHT SHIFTS
        TextView text_night_hours = rootView.findViewById(R.id.text_night_hours);
        hours = dbHelper.getNightShiftHoursAll();
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_night_hours.setText(s);

        TextView text_night_amount = rootView.findViewById(R.id.text_night_amount);
        amount = dbHelper.getNightShiftsPaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_night_amount.setText(s);

        //OVERTIME
        TextView text_overtime_hours = rootView.findViewById(R.id.text_overtime_hours);
        hours = dbHelper.getOvertimeHoursAll();
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        text_overtime_hours.setText(s);

        TextView text_overtime_amount = rootView.findViewById(R.id.text_overtime_amount);
        amount = dbHelper.getOvertimePaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overtime_amount.setText(s);

        //OVERWORK
        TextView text_overwork_hours = rootView.findViewById(R.id.text_overwork_hours);
        hours = dbHelper.getOverworkHoursAll();
        minutes = getMinutesinHour(hours);
        s = setHoursandMinutesString(hours, minutes);
        //s = String.format(Locale.getDefault(),"%.0fω %dλ",hours,minutes);
        text_overwork_hours.setText(s);

        TextView text_overwork_amount = rootView.findViewById(R.id.text_overwork_amount);
        amount = dbHelper.getOverworkPaymentAll();
        s = String.format(Locale.getDefault(),"€%.2f", amount);
        text_overwork_amount.setText(s);
    }

    public static String setHoursandMinutesString(float hours, int minutes) {
        String text;
        if (Locale.getDefault().getLanguage().equals("el")) {
            text = String.format(Locale.getDefault(), "%.0fω %dλ", hours, minutes);
        } else {
            text = String.format(Locale.getDefault(), "%.0fh %dm", hours, minutes);
        }
        return text;
    }

    public static String setHoursandMinutesLongString(float hours, int minutes, String text) {
        if (Locale.getDefault().getLanguage().equals("el")) {
            text = text.replaceAll("0 ΩΡΕΣ ΚΑΙ 0 ΛΕΠΤΑ", String.format(Locale.getDefault(), "%.0f ΩΡΕΣ ΚΑΙ %d ΛΕΠΤΑ", hours, minutes));
        } else {
            text = text.replaceAll("0 HOURS AND 0 MINUTES", String.format(Locale.getDefault(), "%.0f HOURS AND %d MINUITES", hours, minutes));
        }
        return text;
    }
}