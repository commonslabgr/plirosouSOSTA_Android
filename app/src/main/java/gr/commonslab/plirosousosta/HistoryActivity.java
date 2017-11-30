package gr.commonslab.plirosousosta;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.ViewDebug;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Locale;



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
    private ViewPager mViewPager;
    private String currentMonth;
    private String currentYear;
    private TabLayout tabLayout;
    private static Calendar FromDate;
    private static Calendar ToDate;
    public static boolean bToDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_history);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs_history);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        FromDate = Calendar.getInstance();
        ToDate = Calendar.getInstance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_month_history, container, false);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                case 1://MONTH
                    rootView = inflater.inflate(R.layout.fragment_month_history, container, false);
                    setMonthValues(rootView);
                    return rootView;
                case 2://YEAR
                    rootView = inflater.inflate(R.layout.fragment_year_history, container, false);
                    setAnnualValues(rootView);
                    return rootView;
                case 3://FROM-TO
                    showDatePicker(this.getActivity());
                    rootView = inflater.inflate(R.layout.fragment_month_history, container, false);
                    setFromToValues(rootView, FromDate, ToDate);
                    return rootView;
                case 4://ALL
                    rootView = inflater.inflate(R.layout.fragment_month_history, container, false);
                    setAllValues(rootView);
                    return rootView;
            }
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
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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
                setFromToValues(view, FromDate, ToDate);
            } else {
                FromDate.set(Calendar.HOUR, 0);
                FromDate.set(Calendar.MINUTE, 0);
                FromDate.set(Calendar.SECOND, 0);
                FromDate.set(Calendar.YEAR, year);
                FromDate.set(Calendar.MONTH, month);
                FromDate.set(Calendar.DATE, day);
                bToDate = true;
                DialogFragment TodateFragment;
                TodateFragment = new EntitledActivity.DatePickerFragment();
                TodateFragment.show(getFragmentManager(), "datePicker");
            }
        }
    }

    public static void showDatePicker(FragmentActivity activity){
        DialogFragment dateFragment;
        dateFragment = new EntitledActivity.DatePickerFragment();
        dateFragment.show(activity.getFragmentManager(), "datePicker");
    }

    public static int getMinutesinHour(float hours) {
        int minutes = (int)((hours % 1)*60);
        return minutes;
    }

    public static void setMonthValues(View rootView) {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        float hours = 0f;
        int minutes = 0;
        float amount = 0f;
        String s = "";
        DBHelper dbHelper = new DBHelper(rootView.getContext());
        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();

        TextView text_total_amount = rootView.findViewById(R.id.text_total_amount);
        amount = dbHelper.getEntitledPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_total_amount.setText(s);

        TextView text_workminutes = rootView.findViewById(R.id.text_workminutes);
        TextView text_workhours = rootView.findViewById(R.id.text_workhours);
        hours = dbHelper.getTotalHoursinMonth(month);
        minutes = getMinutesinHour(hours);
        s = String.format("%.0f ώρες", hours);
        text_workhours.setText(s);
        s = String.format("και %d λεπτά",minutes);
        text_workminutes.setText(s);

        TextView text_amount = rootView.findViewById(R.id.text_amount);
        amount = dbHelper.getPaymentinMonth(month);
        s = String.format("€%.2f", amount);
        text_amount.setText(s);
    }

    public static void setFromToValues(View rootView, Calendar From, Calendar to) {
        //TODO: implement setFromToValues
    }

    public static void setAnnualValues(View rootView) {
        //TODO: implement setAnnualValues
    }

    public static void setAllValues(View rootView) {
        //TODO: implement setAllValues
    }
}
