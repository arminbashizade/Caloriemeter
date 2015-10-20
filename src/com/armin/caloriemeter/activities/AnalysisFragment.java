package com.armin.caloriemeter.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.armin.caloriemeter.R;

public class AnalysisFragment extends Fragment
{
	
	public AnalysisFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_analysis, container, false);
        return rootView;
	}

	public void update(int year, int month, int day) {
		// TODO Auto-generated method stub
		
	}
}

