package com.armin.caloriemeter.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.adapters.NavigationAdapter;
import com.armin.caloriemeter.dialogs.DeleteConsumptionDialog.DeleteDialogListener;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.MealConsumption;
import com.armin.caloriemeter.util.Time;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog.OnDateSetListener;

public class HistoryActivity extends FragmentActivity implements ActionBar.TabListener, OnDateSetListener, DeleteDialogListener  {
	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    
    //TODO navigate between days? :-?
    ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
//    private TextView[] tabs = new TextView[2];
    
    private String[] navigationMenuItems;
    
    private PersianCalendar calendar = new PersianCalendar();
//    private Calendar cal= Calendar.getInstance();
    
    private PersianDatePickerDialog persianDatePickerDialog = PersianDatePickerDialog.newInstance(this, calendar.get(PersianCalendar.YEAR), calendar.get(PersianCalendar.MONTH), calendar.get(PersianCalendar.DAY_OF_MONTH), true, PersianDatePickerDialog.PERSIAN);
//	private PersianDatePickerDialog datePickerDialog = PersianDatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), true, PersianDatePickerDialog.GREGORIAN);

    private HistoryFragment historyFragment = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	Date date = (Date) extras.getSerializable(Constants.DATE_KEY);
        	calendar.set(Calendar.YEAR, date.year);
        	calendar.set(Calendar.MONTH, date.month-1);
        	calendar.set(Calendar.DAY_OF_MONTH, date.day);
        	Time time = (Time) extras.getSerializable(Constants.TIME_KEY);
        	calendar.set(Calendar.HOUR_OF_DAY, time.hour);
        	calendar.set(Calendar.MINUTE, time.minute);
        }

        persianDatePickerDialog = PersianDatePickerDialog.newInstance(this, calendar.get(PersianCalendar.YEAR), calendar.get(PersianCalendar.MONTH), calendar.get(PersianCalendar.DAY_OF_MONTH), true, PersianDatePickerDialog.PERSIAN);
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(R.color.tab_bar_color));
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        tabs[0] = (TextView) findViewById(R.id.history_tab_label);
//        tabs[1] = (TextView) findViewById(R.id.analysis_tab_label);

//        tabs[0].setOnClickListener(
//        		new View.OnClickListener() {
//
//        			@Override
//        			public void onClick(View v) {
//        				mViewPager.setCurrentItem(0, true);
//        			}
//        		});
//        tabs[1].setOnClickListener(
//        		new View.OnClickListener() {
//
//        			@Override
//        			public void onClick(View v) {
//        				mViewPager.setCurrentItem(1, true);
//        			}
//        		});
        
        navigationMenuItems = getResources().getStringArray(R.array.navigation_menu_items);
        
        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavigationAdapter(this, this, mDrawerLayout, mDrawerList, navigationMenuItems));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, 0, 0) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//            	tabs[position].setTextColor(getResources().getColor(R.color.selected_tab));
//            	tabs[1-position].setTextColor(getResources().getColor(R.color.unselected_tab));
//            }
//        });

    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        if(item.getItemId() == R.id.action_new_food)
        {
        	Intent intent = new Intent(HistoryActivity.this, SearchActivity.class);
        	
        	Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        	Time time = new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        	intent.putExtra(Constants.SEARCH_MODE_KEY, Constants.FOOD);
        	intent.putExtra(Constants.DATE_KEY, date);
        	intent.putExtra(Constants.TIME_KEY, time);
        	
        	startActivity(intent);
        }
        else if(item.getItemId() == R.id.action_pick_date)
        {
        	persianDatePickerDialog.setYearRange(Constants.FIRST_YEAR_FOOD, calendar.get(Calendar.YEAR));
			persianDatePickerDialog.setVibrate(false);
			persianDatePickerDialog.setCloseOnSingleTapDay(false);
			persianDatePickerDialog.show(getSupportFragmentManager(), "date_picker");
        }
        else if(item.getItemId() == R.id.action_previous_day)
        {
        	calendar.add(Calendar.DATE, -1);
        	historyFragment.update(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        	return true;
        }
        else if(item.getItemId() == R.id.action_next_day)
        {
        	Calendar tmpCal = Calendar.getInstance();
        	if(calendar.getTimeInMillis() + 24*60*60*1000 <= tmpCal.getTimeInMillis())
        	{
        		calendar.add(Calendar.DATE, +1);
        		historyFragment.update(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        	}
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
            switch(position)
            {
            case 0:
            	if(historyFragment== null)
            		historyFragment = new HistoryFragment();
            	historyFragment.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            	return historyFragment;
            case 1:
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.history_tab_title).toUpperCase(l);
                case 1:
                    return getString(R.string.analysis_tab_title).toUpperCase(l);
            }
            return null;
        }
    }

	@Override
	public void onDateSet(PersianDatePickerDialog datePickerDialog, int year,
			int month, int day)
	{
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		historyFragment.update(year, month+1, day);
	}
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int position, ArrayList<MealConsumption> mealsArray) {
		historyFragment.new DeleteConsumptionQuery().execute(mealsArray.remove(position));
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}
}
