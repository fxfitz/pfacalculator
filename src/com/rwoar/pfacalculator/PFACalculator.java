package com.rwoar.pfacalculator;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.admob.android.ads.AdManager;
import com.rwoar.pfacalculator.widget.NumberPicker;



public class PFACalculator extends ListActivity {

	private final int RUN_DIALOG_ID = 0;
	private final int GENDER_DIALOG_ID = 1;
	private final int SITUP_DIALOG_ID = 2;
	private final int PUSHUP_DIALOG_ID = 3;
	private final int AGE_DIALOG_ID = 4;
	private final int WAIST_DIALOG_ID = 5;
	private final int OVERALL_SCORE_DIALOG_ID = 6;
	private final int MINIMUM_DIALOG_ID = 7;
	private final int MAXIMUM_DIALOG_ID = 8;

	private final int DEFAULT_AGE = 20;
	private final int DEFAULT_PUSHUP = 45;
	private final int DEFAULT_SITUP = 50;
	private final int DEFAULT_WAIST_INCHES = 34;
	private final int DEFAULT_WAIST_CENTIMETERS = 0;
	
	private final int FOR_MAIN_LIST = 0;
	private final int FOR_MINIMUM = 1;
	private final int FOR_MAXIMUM = 2;

	private CalculatorVO calculatorVO = null;
	static ArrayList<HashMap<String,String>> optionsList; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfa_list_view);

		if (calculatorVO == null){
			if (savedInstanceState != null && savedInstanceState.containsKey("calculatorVO"))
				calculatorVO = (CalculatorVO) savedInstanceState.getSerializable("calculatorVO");
			else
				calculatorVO = new CalculatorVO();
		}

		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.pfa_footer, lv, false);
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.pfa_header_ads, lv, false);
		lv.addFooterView(footer, null, false);
		lv.addHeaderView(header, null, false);

		updateList();
	}
	
	protected void onSaveInstanceState(Bundle outState){
		outState.putSerializable("calculatorVO", calculatorVO);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case RUN_DIALOG_ID:
			return runDialog("Enter Run Time");
		case GENDER_DIALOG_ID:
			return genderDialog();
		case SITUP_DIALOG_ID:
			return numberDialog("Amount of Sit-ups", SITUP_DIALOG_ID);
		case PUSHUP_DIALOG_ID:
			return numberDialog("Amount of Push-ups", PUSHUP_DIALOG_ID);
		case WAIST_DIALOG_ID:
			return waistDialog("Waist Measurement");
		case AGE_DIALOG_ID:
			return ageGroupDialog(FOR_MAIN_LIST);
		case OVERALL_SCORE_DIALOG_ID:
			return overallScoreDialog();
		case MINIMUM_DIALOG_ID:
			return ageGroupDialog(FOR_MINIMUM);
		case MAXIMUM_DIALOG_ID:
			return ageGroupDialog(FOR_MAXIMUM);

		}
		return null;
	}
	
	protected void onPrepareDialog(int id, Dialog dialog){
		switch (id){
		case OVERALL_SCORE_DIALOG_ID:
			ScoreCalculator sc = new ScoreCalculator(this, calculatorVO);
			
			TextView situps = (TextView) dialog.findViewById(R.id.situp_performance_value);
			boolean bool_situp = sc.passedSitups();
			if (bool_situp == true){
				situps.setText("Passed");
				situps.setTextColor(Color.GREEN);
			}
			else {
				situps.setText("Failed");
				situps.setTextColor(Color.RED);
			}
			
			TextView pushup = (TextView) dialog.findViewById(R.id.pushup_performance_value);
			boolean bool_pushup = sc.passedPushups();
			if (bool_pushup == true){
				pushup.setText("Passed");
				pushup.setTextColor(Color.GREEN);
			}
			else {
				pushup.setText("Failed");
				pushup.setTextColor(Color.RED);
			}
			
			TextView run = (TextView) dialog.findViewById(R.id.run_performance_value);
			boolean bool_run = sc.passedRun();
			if (bool_run == true){
				run.setText("Passed");
				run.setTextColor(Color.GREEN);
			}
			else {
				run.setText("Failed");
				run.setTextColor(Color.RED);
			}
			
			TextView waist = (TextView) dialog.findViewById(R.id.waist_performance_value);
			boolean bool_waist = sc.passedWaist();
			if (bool_waist == true){
				waist.setText("Passed");
				waist.setTextColor(Color.GREEN);
			}
			else {
				waist.setText("Failed");
				waist.setTextColor(Color.RED);
			}
			
			TextView performance = (TextView) dialog.findViewById(R.id.overall_performance_value);
			boolean bool_perform = sc.passedOverallTest();
			if (bool_perform == true){
				performance.setText("Passed");
				performance.setTextColor(Color.GREEN);
			}
			else {
				performance.setText("Failed");
				performance.setTextColor(Color.RED);
			}
		
			
			TextView total_score_value = (TextView) dialog.findViewById(R.id.total_score_value);
			Double total = sc.getTotalScore();
			total_score_value.setText(Double.toString(total));
			break;
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		// This is 'position-1' because of the header
		HashMap<?, ?> choice = (HashMap<?, ?>) this.getListAdapter().getItem(position-1);

		if (choice.get("option").equals("Run Time"))
			showDialog(RUN_DIALOG_ID);
		else if (choice.get("option").equals("Gender"))
			showDialog(GENDER_DIALOG_ID);
		else if (choice.get("option").equals("Sit-Ups"))
			showDialog(SITUP_DIALOG_ID);
		else if (choice.get("option").equals("Age Group"))
			showDialog(AGE_DIALOG_ID);
		else if (choice.get("option").equals("Push-Ups"))
			showDialog(PUSHUP_DIALOG_ID);
		else if (choice.get("option").equals("Waist Measurement"))
			showDialog(WAIST_DIALOG_ID);
	}
	
    public void buttonClickHandler(View v)
    {
    	  	
    	switch(v.getId()){
    	case R.id.calculateButton:
			if (calculatorVO.isComplete() == false){
				Toast.makeText(getApplicationContext(), R.string.incompleteCalculatorVO, Toast.LENGTH_SHORT).show();
				break;
			}
			else 
				showDialog(OVERALL_SCORE_DIALOG_ID);
    		break;
    	case R.id.clearButton:
    		calculatorVO.clear();
    		updateList();
    		break;
    	default:
    		// Do nothing!
    		return;
    	}
    }

    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.pfa_calculator, menu);
    	return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.minimumsMenuButton:
			showDialog(MINIMUM_DIALOG_ID);
			return true;
		case R.id.maximumsMenuButton:
			// Do maximums stuff
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateList() {
		optionsList = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> temp = new HashMap<String,String>();
		temp.put("option","Gender");
		temp.put("selection", calculatorVO.getGenderString());
		temp.put("description", "Male or Female");
		optionsList.add(temp);
		HashMap<String,String> temp1 = new HashMap<String,String>();
		temp1.put("option","Push-Ups");
		temp1.put("selection", calculatorVO.getPushupsString());
		temp1.put("description", "Amount Per Minute");
		optionsList.add(temp1);
		HashMap<String,String> temp2 = new HashMap<String,String>();
		temp2.put("option","Sit-Ups");
		temp2.put("selection", calculatorVO.getSitupsString());
		temp2.put("description", "Amount Per Minute");
		optionsList.add(temp2);
		HashMap<String,String> temp3 = new HashMap<String,String>();
		temp3.put("option","Run Time");
		temp3.put("selection", calculatorVO.getRunString());
		temp3.put("description", "1.5 Mile Run");
		optionsList.add(temp3);
		HashMap<String,String> temp4 = new HashMap<String,String>();
		temp4.put("option","Age Group");
		temp4.put("selection", calculatorVO.getAgeGroupString());
		temp4.put("description", "Age Category");
		optionsList.add(1, temp4);
		HashMap<String,String> temp5 = new HashMap<String,String>();
		temp5.put("option","Waist Measurement");
		temp5.put("selection", calculatorVO.getWaistString());
		temp5.put("description", "Abdominal Circumference");
		optionsList.add(2, temp5);

		SimpleAdapter adapter = new SimpleAdapter(
				this,
				optionsList,
				R.layout.pfa_row_view,
				new String[] {"option","selection", "description"},
				new int[] {R.id.pfa_rowtext1,R.id.pfa_rowtext2,R.id.pfa_rowtext3}
		);

		setListAdapter(adapter);
		
	}

	private AlertDialog genderDialog(){
		final CharSequence[] items = {"Male", "Female"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose your gender");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item+1 == CalculatorVO.MALE)
					calculatorVO.setGender(CalculatorVO.MALE);
				else if (item+1 == CalculatorVO.FEMALE)
					calculatorVO.setGender(CalculatorVO.FEMALE);
				updateList();
			}
		});
		AlertDialog alert = builder.create();
		return alert;
	}

	private AlertDialog ageGroupDialog(int option){
		String[] array = (String[])calculatorVO.ageStringsMap.values().toArray(new String[calculatorVO.ageStringsMap.values().size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Age Group");
		
		if (option == FOR_MAIN_LIST){
			builder.setItems(array, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					calculatorVO.setAgeGroup(item);
					updateList();
				}
			});
		} else {
			builder.setItems(array, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					minimumsMaximumsDialog(item).show();
				}
			});
		}
		AlertDialog alert = builder.create();
		return alert;
	}

	private AlertDialog numberDialog(String title, final int dialogID){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.number_dialog,
				(ViewGroup) findViewById(R.id.number_dialog_layout_root));
		alert.setTitle(title);  

		final NumberPicker np = (NumberPicker) layout.findViewById(R.id.number_dialog_numpicker);

		// Set default values
		switch(dialogID){
		case SITUP_DIALOG_ID:
			np.setCurrent(DEFAULT_SITUP);
			break;
		case PUSHUP_DIALOG_ID:
			np.setCurrent(DEFAULT_PUSHUP);
			break;
		case AGE_DIALOG_ID:
			np.setCurrent(DEFAULT_AGE);
			break;
		}

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				int newNumber = np.getCurrent();

				switch(dialogID){
				case SITUP_DIALOG_ID:
					calculatorVO.setSitups(newNumber);
					break;
				case PUSHUP_DIALOG_ID:
					calculatorVO.setPushups(newNumber);
					break;
				case WAIST_DIALOG_ID:
					calculatorVO.setWaist(newNumber);
					break;
				}

				PFACalculator.this.updateList();
				return;                  
			}  
		});  

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;   
			}
		});

		AlertDialog ad = alert.create();
		return ad;
	}

	private AlertDialog waistDialog(String title){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.waist_dialog,
				(ViewGroup) findViewById(R.id.waist_dialog_layout_root));
		alert.setTitle(title);  

		final NumberPicker inches = (NumberPicker) layout.findViewById(R.id.waist_dialog_inches);
		inches.setCurrent(DEFAULT_WAIST_INCHES);
		final NumberPicker centimeters = (NumberPicker) layout.findViewById(R.id.waist_dialog_centimeters);
		centimeters.setCurrent(DEFAULT_WAIST_CENTIMETERS);
		String[] vals = {"0","5"};
		centimeters.setRange(0, 1, vals);

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				int inchesNum = inches.getCurrent();
				int centimetersNum = centimeters.getCurrent();

				calculatorVO.setWaist(Double.parseDouble(inchesNum+"."+ ((centimetersNum == 0) ? 0 : 5)));

				PFACalculator.this.updateList();
				return;                  
			}  
		});  

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;   
			}
		});

		AlertDialog ad = alert.create();
		return ad;
	}

	private AlertDialog runDialog(String title){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.run_dialog,
				(ViewGroup) findViewById(R.id.run_dialog_layout_root));
		alert.setTitle(title);  

		final NumberPicker minutes = (NumberPicker) layout.findViewById(R.id.run_dialog_minute);
		minutes.setCurrent(DEFAULT_WAIST_INCHES);
		minutes.setRange(0, 59);
		minutes.setCurrent(10);
		final NumberPicker seconds = (NumberPicker) layout.findViewById(R.id.run_dialog_second);
		seconds.setCurrent(DEFAULT_WAIST_CENTIMETERS);
		ArrayList<String> vals = new ArrayList<String>();
		for (int i = 0; i<=59;i++){
			vals.add(String.format("%02d", i));
		}
		seconds.setRange(0, 59, vals.toArray(new String[60]));
		seconds.setCurrent(30);

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				int minutesNum = minutes.getCurrent();
				int secondsNum = seconds.getCurrent();

				calculatorVO.setRun(minutesNum, secondsNum);

				PFACalculator.this.updateList();
				return;                  
			}  
		});  

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;   
			}
		});

		AlertDialog ad = alert.create();
		return ad;
	}

	private AlertDialog overallScoreDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.overall_score_dialog,
				(ViewGroup) findViewById(R.id.overall_score_dialog_layout_root));
		alert.setTitle("PFA Results");  

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				return;                  
			}  
		});  

		AlertDialog ad = alert.create();
		return ad;
	}
	
	private AlertDialog minimumsMaximumsDialog(int option){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.minimum_maximum_dialog,
				(ViewGroup) findViewById(R.id.minimum_maximum_dialog_layout_roots));
		alert.setTitle(option == FOR_MAXIMUM ? "Maximums" : "Minimums");  

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				return;                  
			}  
		});  

		AlertDialog ad = alert.create();
		return ad;
	}
}