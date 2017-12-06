package gr.commonslab.plirosousosta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;


public class EntitledActivity extends AppCompatActivity {

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
    private CustomViewPager mViewPager;
    private String currentMonth;
    private String currentYear;
    private TabLayout tabLayout;
    private static Calendar FromDate;
    private static Calendar ToDate;
    public static boolean bToDate = false;
    private static Button button_info;
    private static View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entitled);
        //mFragments.add(new MonthFrangment());
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container_entitled);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs_entitled);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        FromDate = Calendar.getInstance();
        ToDate = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entitled, menu);
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
            return true;
        }
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        return super.onOptionsItemSelected(item);
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

        public MonthFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MonthFragment newInstance(int sectionNumber) {
            MonthFragment fragment = new MonthFragment(sectionNumber);
            /**
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
             /**/
            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_month_entitled, container, false);
            setMonthValues(rootView);
            /**
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                case 1://MONTH
                    rootView = inflater.inflate(R.layout.fragment_month_entitled, container, false);
                    setMonthValues(rootView);
                    break;
                case 2://YEAR
                    rootView = inflater.inflate(R.layout.fragment_year_entitled, container, false);
                    setAnnualValues(rootView);
                    break;
                case 3://FROM-TO
                    rootView = inflater.inflate(R.layout.fragment_fromto_entitled, container, false);
                    currentView = rootView;
                    showDatePicker(this.getActivity());
                    break;
                case 4://ALL
                    rootView = inflater.inflate(R.layout.fragment_all_entitled, container, false);
                    setAllValues(rootView);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_month_entitled, container, false);
                    setMonthValues(rootView);
                    break;
            }
             /**/
            button_info = (Button) rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    v.getContext().startActivity(intent_info);
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

        public YearFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static YearFragment newInstance(int sectionNumber) {
            YearFragment fragment = new YearFragment(sectionNumber);
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
            button_info = (Button) rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    v.getContext().startActivity(intent_info);
                }
            });

            return rootView;
        }
    }

    /**
     * A placeholder fragment containing Month view.
     */
    public static class FromToFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FromToFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FromToFragment newInstance(int sectionNumber) {
            FromToFragment fragment = new FromToFragment(sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fromto_entitled, container, false);
            //setMonthValues(rootView);
            currentView = rootView;
            setUserVisibleHint(false);
            button_info = (Button) rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    v.getContext().startActivity(intent_info);
                }
            });

            return rootView;
        }

        @Override
        public void setMenuVisibility(final boolean visible) {
            super.setMenuVisibility(visible);
            if(visible) {
                showDatePicker(getActivity());
            }
        }
    }

    /**
     * A placeholder fragment containing Month view.
     */
    public static class AllFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AllFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AllFragment newInstance(int sectionNumber) {
            AllFragment fragment = new AllFragment(sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_all_entitled, container, false);

            button_info = (Button) rootView.findViewById(R.id.button_whatcanido);
            button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to INFO activity
                    Intent intent_info = new Intent(v.getContext(), InfoActivity.class);
                    v.getContext().startActivity(intent_info);
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
                    MonthFragment tab1 = new MonthFragment(0);
                    return tab1;
                case 1:
                    YearFragment tab2 = new YearFragment(1);
                    return tab2;
                case 2:
                    FromToFragment tab3 = new FromToFragment(2);
                    return tab3;
                case 3:
                    AllFragment tab4 = new AllFragment(3);
                    return tab4;
                default:
                    return null;
            }
            //return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar cal = Calendar.getInstance();
            currentMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            currentYear =  Integer.toString(cal.get(Calendar.YEAR));
            switch (position) {
                case 0:
                    return currentMonth;
                case 1:
                    return currentYear;
                case 2:
                    return getString(R.string.entitled_tab_from_to);
                case 3:
                    return getString(R.string.entitled_tab_all);
            }
            return null;
        }
    }

    public static void showDatePicker(Activity activity){
        DialogFragment dateFragment;
        dateFragment = new DatePickerFragment();
        dateFragment.show(activity.getFragmentManager(), "datePicker");
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
                ToDate.set(Calendar.DATE, day);
                bToDate = false;
                //TODO: set values
                if(currentView != null) {
                    setFromToValues(currentView,FromDate, ToDate);
                }
            } else {
                FromDate.set(Calendar.HOUR, 0);
                FromDate.set(Calendar.MINUTE, 0);
                FromDate.set(Calendar.SECOND, 0);
                FromDate.set(Calendar.YEAR, year);
                FromDate.set(Calendar.MONTH, month);
                FromDate.set(Calendar.DATE, day);
                bToDate = true;
                DialogFragment TodateFragment;
                TodateFragment = new DatePickerFragment();
                TodateFragment.show(getFragmentManager(), "datePicker");
            }
        }
    }

    public static int getMinutesinHour(float hours) {
        int minutes = (int)((hours % 1)*60);
        return minutes;
    }

    public static void setFromToValues(View rootView, Calendar From, Calendar to) {
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();
        String s = "";
        //TODO: implement setFromToValues
        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_fromto_amount);

        s = "€23.45";
        text_total_amount.setText(s);
    }

    public static void setAnnualValues(View rootView) {
        //TODO: implement setAnnualValues
        int month = Calendar.getInstance().get(Calendar.MONTH);
        float hours = 0f;
        int minutes = 0;
        float amount = 0f;
        String s = "";
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        //amount = dbHelper.getEntitledPaymentinMonth(month);
        s = "TESTING YEAR";
        text_total_amount.setText(s);
    }

    public static void setMonthValues(View rootView) {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        float hours = 0f;
        int minutes = 0;
        float amount = 0f;
        String s = "";
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();

        //TOP
        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        amount = dbHelper.getEntitledPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_total_amount.setText(s);

        //BOTTOM
        TextView text_bottom = rootView.findViewById(R.id.text_should_getpaid);
        s = text_bottom.getText().toString();
        s = s.replaceAll("€",String.format("€%.2f", amount));
        text_bottom.setText(s);

        //SUNDAYS & HOLIDAYS
        TextView text_sunday_hours = rootView.findViewById(R.id.text_sunday_hours);
        hours = dbHelper.getSundayHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0fω %dλ",hours,minutes);
        text_sunday_hours.setText(s);

        TextView text_sunday_amount = rootView.findViewById(R.id.text_sunday_amount);
        amount = dbHelper.getSundaysPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_sunday_amount.setText(s);

        //SATURDAYS
        TextView text_saturday_hours = rootView.findViewById(R.id.text_saturday_hours);
        hours = dbHelper.getSaturdayHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0fω %dλ",hours,minutes);
        text_saturday_hours.setText(s);

        TextView text_saturday_amount = rootView.findViewById(R.id.text_saturday_amount);
        amount = dbHelper.getSaturdaysPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_saturday_amount.setText(s);

        //NIGHT SHIFTS
        TextView text_night_hours = rootView.findViewById(R.id.text_night_hours);
        hours = dbHelper.getNightShiftHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0fω %dλ",hours,minutes);
        text_night_hours.setText(s);

        TextView text_night_amount = rootView.findViewById(R.id.text_night_amount);
        amount = dbHelper.getNightShiftsPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_night_amount.setText(s);

        //OVERTIME
        TextView text_overtime_hours = rootView.findViewById(R.id.text_overtime_hours);
        hours = dbHelper.getOvertimeHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0fω %dλ",hours,minutes);
        text_overtime_hours.setText(s);

        TextView text_overtime_amount = rootView.findViewById(R.id.text_overtime_amount);
        amount = dbHelper.getOvertimePaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_overtime_amount.setText(s);

        //OVERWORK
        TextView text_overwork_hours = rootView.findViewById(R.id.text_overwork_hours);
        hours = dbHelper.getOverworkHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0fω %dλ",hours,minutes);
        text_overwork_hours.setText(s);

        TextView text_overwork_amount = rootView.findViewById(R.id.text_overwork_amount);
        amount = dbHelper.getOverworkPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_overwork_amount.setText(s);
    }

    public static void setAllValues(View rootView) {
        //TODO: implement setAllValues
    }
}
