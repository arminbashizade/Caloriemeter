package com.armin.caloriemeter.dialogs;

import java.util.ArrayList;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.util.MealConsumption;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleteConsumptionDialog extends DialogFragment implements OnClickListener{

	DeleteDialogListener mListener;
	private int position;
	private ArrayList<MealConsumption> mealsArray;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.delete_consumption_dialog_message)
		.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		})
		.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		// Create the AlertDialog object and return it
				return builder.create();
	}

	public interface DeleteDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog, int position, ArrayList<MealConsumption> mealsArray);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog)getDialog();
		if(d != null)
		{
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_NEGATIVE);
			positiveButton.setOnClickListener(this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (DeleteDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DialogListener");
		}
	}

	public void setItemPosition(int position) {
		this.position = position;
	}
	
	public void setArray(ArrayList<MealConsumption> mealsArray)
	{
		this.mealsArray = mealsArray;
	}
	
	@Override
	public void onClick(View v) {
		mListener.onDialogPositiveClick(DeleteConsumptionDialog.this, position, mealsArray);
		dismiss();
	}
}