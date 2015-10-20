package com.armin.caloriemeter.activities;

import java.util.Calendar;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.widget.TextView;

import com.armin.caloriemeter.Constants;
import com.armin.caloriemeter.Date;
import com.armin.caloriemeter.R;
import com.armin.caloriemeter.Time;
import com.armin.caloriemeter.adapters.NavigationAdapter;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog.OnDateSetListener;

public class HistoryActivity extends FragmentActivity implements ActionBar.TabListener, OnDateSetListener  {
//TODO go to the date which you left
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
    ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private TextView[] tabs = new TextView[2];

    private String[] navigationMenuItems;
    
    private PersianCalendar calendar = new PersianCalendar();
    private Calendar cal = Calendar.getInstance();
    
    PersianDatePickerDialog persianDatePickerDialog = PersianDatePickerDialog.newInstance(this, calendar.get(PersianCalendar.YEAR), calendar.get(PersianCalendar.MONTH), calendar.get(PersianCalendar.DAY_OF_MONTH), true, PersianDatePickerDialog.PERSIAN);
	PersianDatePickerDialog datePickerDialog = PersianDatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), true, PersianDatePickerDialog.GREGORIAN);

    private HistoryFragment historyFragment = null;
    private AnalysisFragment analysisFragment = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        actionBar.setStackedBackgroundDrawable(new ColorDrawable(R.color.tab_bar_color));
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        tabs[0] = (TextView) findViewById(R.id.history_tab_label);
        tabs[1] = (TextView) findViewById(R.id.analysis_tab_label);

        tabs[0].setOnClickListener(
        		new View.OnClickListener() {

        			@Override
        			public void onClick(View v) {
        				mViewPager.setCurrentItem(0, true);
        			}
        		});
        tabs[1].setOnClickListener(
        		new View.OnClickListener() {

        			@Override
        			public void onClick(View v) {
        				mViewPager.setCurrentItem(1, true);
        			}
        		});
        
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
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	tabs[position].setTextColor(getResources().getColor(R.color.selected_tab));
            	tabs[1-position].setTextColor(getResources().getColor(R.color.unselected_tab));
            }
        });

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
        	Intent intent = new Intent(HistoryActivity.this, NewConsumptionActivity.class);
        	
        	Date date = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        	Time time = new Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        	intent.putExtra(Constants.DATE_KEY, date);
        	intent.putExtra(Constants.TIME_KEY, time);
        	
        	startActivity(intent);
        }
        else if(item.getItemId() == R.id.action_pick_date)
        {
        	datePickerDialog.setYearRange(1950, cal.get(Calendar.YEAR));
			datePickerDialog.setVibrate(false);
			datePickerDialog.setCloseOnSingleTapDay(false);
			datePickerDialog.show(getSupportFragmentManager(), "date");
        }
        else if(item.getItemId() == R.id.action_previous_day)
        {
        	cal.add(Calendar.DATE, -1);
        	historyFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
        	analysisFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
        	return true;
        }
        else if(item.getItemId() == R.id.action_next_day)
        {
        	cal.add(Calendar.DATE, +1);
        	historyFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
        	analysisFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
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
            	if(historyFragment == null)
            		historyFragment = new HistoryFragment();
//            	else
//            		historyFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
            	return historyFragment;
            case 1:
            	if(analysisFragment == null)
            		analysisFragment = new AnalysisFragment();
//            	else
//            		analysisFragment.update(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
            	return analysisFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
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
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		historyFragment.update(year, month, day);
    	analysisFragment.update(year, month, day);
	}
}
