package com.armin.caloriemeter.dialogs;

import java.util.ArrayList;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class IngredientDetailDialogFragment extends DialogFragment implements OnClickListener{
	AlertDialog dialog;
	private String[] units;
	private float[] amounts;
	private float[] energies;


	private LayoutInflater inflater;
	private View view;
	private EditText amountEditText;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		inflater = getActivity().getLayoutInflater();

		dialog = builder.create();
		view = inflater.inflate(R.layout.ingridient_detail, null);
		amountEditText = (EditText) view.findViewById(R.id.ingridient_amount_edit_text);
		ArrayList<String> spinnerArray =  new ArrayList<String>();
		for(String s: units)
			spinnerArray.add(s);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_rtl_item, spinnerArray);

		((Spinner)view.findViewById(R.id.ingridient_unit_spinner)).setAdapter(adapter);
		((Spinner)view.findViewById(R.id.ingridient_unit_spinner)).setSelection(0);

		builder.setView(view)
		.setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		})
		.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogNegativeClick(IngredientDetailDialogFragment.this);				
			}
		});
		return builder.show();
	}
	
	public interface IngredientDetailDialogListener {
		public void onDialogPositiveClick(int amount, int energy, String unit, int listItemPosition);
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

	public void setArguments(String[] units, float[] energy, float[] amount)
	{
		this.units = units;
		this.energies = energy;
		this.amounts = amount;
	}

	// Use this instance of the interface to deliver action events
	IngredientDetailDialogListener mListener;
	private int position;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (IngredientDetailDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	public void setListItemPosition(int position)
	{
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		if(TextUtils.isEmpty(amountEditText.getText()))
		{
			Toast.makeText(getActivity(), getResources().getString(R.string.error_enter_amount), Toast.LENGTH_SHORT).show();
		}
		else
		{
			int unitIndex = ((Spinner)view.findViewById(R.id.ingridient_unit_spinner))
					.getSelectedItemPosition();
			int enteredAmount = Integer.parseInt(Utils.toEnglishNumbers(amountEditText.getText().toString()));
			int energy = (int) (enteredAmount * energies[unitIndex] / amounts[unitIndex]);
			
			mListener.onDialogPositiveClick(
					enteredAmount,
					energy,
					units[unitIndex],
					position);
			dismiss();
		}
	}
}