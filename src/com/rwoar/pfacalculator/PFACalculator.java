package com.rwoar.pfacalculator;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

import com.rwoar.pfacalculator.widget.NumberPicker;



public class PFACalculator extends ListActivity {

	private final int RUN_DIALOG_ID = 0;
	private final int GENDER_DIALOG_ID = 1;
	private final int SITUP_DIALOG_ID = 2;
	private final int PUSHUP_DIALOG_ID = 3;
	private final int AGE_DIALOG_ID = 4;
	private final int WAIST_DIALOG_ID = 5;
	private final int OVERALL_SCORE_DIALOG_ID = 6;
	private final int MINMAX_DIALOG_ID = 7;
	private final int WALK_DIALOG_ID = 8;
	private final int EXACT_AGE_DIALOG_ID = 9;
	private final int HEART_RATE_DIALOG_ID = 10;
	private final int WEIGHT_DIALOG_ID = 11;
	private final int AFICHART_DIALOG_ID = 12;

	private final int DEFAULT_PUSHUP = 45;
	private final int DEFAULT_SITUP = 50;
	private final int DEFAULT_WAIST_INCHES = 35;
	private final int DEFAULT_WAIST_CENTIMETERS = 0;
	private final int DEFAULT_EXACT_AGE = 35;
	private final int DEFAULT_HEART_RATE = 160;
	private final int DEFAULT_WEIGHT = 150;

	private final int FOR_MAIN_LIST = 0;
	private final int FOR_MINMAX = 1;

	private final int RUN = 1;
	private final int WALK = 2;

	private boolean pushupPref;
	private boolean situpPref;
	private boolean aerobicPref;
	private boolean waistPref;
	private String aerobicCompPref;

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

	protected void onStart(){
		super.onStart();
		this.getPrefs();
		updateList();
	}

	protected void onSaveInstanceState(Bundle outState){
		outState.putSerializable("calculatorVO", calculatorVO);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case RUN_DIALOG_ID:
			return runDialog("Enter Run Time");
		case WALK_DIALOG_ID:
			return runDialog("Enter Walk Time");
		case GENDER_DIALOG_ID:
			return genderDialog();
		case SITUP_DIALOG_ID:
			return numberDialog("Amount of Sit-ups", SITUP_DIALOG_ID);
		case PUSHUP_DIALOG_ID:
			return numberDialog("Amount of Push-ups", PUSHUP_DIALOG_ID);
		case EXACT_AGE_DIALOG_ID:
			return numberDialog("Enter Your Exact Age", EXACT_AGE_DIALOG_ID);
		case HEART_RATE_DIALOG_ID:
			return numberDialog("Enter Your Heart Rate", HEART_RATE_DIALOG_ID);
		case WAIST_DIALOG_ID:
			return waistDialog("Waist Measurement");
		case AGE_DIALOG_ID:
			return ageGroupDialog(FOR_MAIN_LIST);
		case WEIGHT_DIALOG_ID:
			return numberDialog("Enter Your Weight", WEIGHT_DIALOG_ID);
		case OVERALL_SCORE_DIALOG_ID:
			return overallScoreDialog();
		case MINMAX_DIALOG_ID:
			return minimumsMaximumsDialog(FOR_MINMAX);
		case AFICHART_DIALOG_ID:
			return afiChartsDialog();
		default:
			return null;
		}

	}

	protected void onPrepareDialog(int id, Dialog dialog){
		switch (id){
		case OVERALL_SCORE_DIALOG_ID:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			ScoreCalculator sc = new ScoreCalculator(this, calculatorVO, prefs);
			Double componentScore = new Double(0);

			TextView situps = (TextView) dialog.findViewById(R.id.situp_performance_value);
			TextView situpsScore = (TextView) dialog.findViewById(R.id.situp_score_value);
			if (situpPref == true){
				componentScore = sc.getSitupScore();
				situpsScore.setText("("+componentScore.toString()+")");
			}
			boolean bool_situp = sc.passedSitups();
			if (situpPref == false){
				situps.setText("Exempt");
				situps.setTextColor(Color.YELLOW);
				situpsScore.setText("");
			}
			else if (bool_situp == true){
				situps.setText("Passed");
				situps.setTextColor(Color.GREEN);
			}
			else {
				situps.setText("Failed");
				situps.setTextColor(Color.RED);
			}

			TextView pushup = (TextView) dialog.findViewById(R.id.pushup_performance_value);
			boolean bool_pushup = sc.passedPushups();
			TextView pushupsScore = (TextView) dialog.findViewById(R.id.pushup_score_value);
			if (pushupPref == true){
				componentScore = sc.getPushupScore();
				pushupsScore.setText("("+componentScore.toString()+")");
			}
			if (pushupPref == false){
				pushup.setText("Exempt");
				pushup.setTextColor(Color.YELLOW);
				pushupsScore.setText("");
			}	
			else if (bool_pushup == true){
				pushup.setText("Passed");
				pushup.setTextColor(Color.GREEN);
			}
			else {
				pushup.setText("Failed");
				pushup.setTextColor(Color.RED);
			}

			TextView runwalk = (TextView) dialog.findViewById(R.id.run_performance_value);
			boolean bool_runwalk;
			if (aerobicCompPref.equals(Integer.toString(RUN)))
				bool_runwalk = sc.passedRun();
			else
				bool_runwalk = sc.passedWalk();
			TextView runWalkScore = (TextView) dialog.findViewById(R.id.run_score_value);
			if (aerobicPref == true){
				if (aerobicCompPref.equals(Integer.toString(RUN)))
					componentScore = sc.getRunScore();
				else
					//componentScore = sc.getWalkScore();
					componentScore = 100.0;

				runWalkScore.setText("("+componentScore.toString()+")");
			}
			if (aerobicPref == false){
				runwalk.setText("Exempt");
				runwalk.setTextColor(Color.YELLOW);
				runWalkScore.setText("");
			}
			else if (bool_runwalk == true){
				runwalk.setText("Passed");
				runwalk.setTextColor(Color.GREEN);
			}
			else {
				runwalk.setText("Failed");
				runwalk.setTextColor(Color.RED);
			}

			TextView waist = (TextView) dialog.findViewById(R.id.waist_performance_value);
			boolean bool_waist = sc.passedWaist();
			TextView waistScore = (TextView) dialog.findViewById(R.id.waist_score_value);
			if (waistPref == true) {
				componentScore = sc.getWaistScore();
				waistScore.setText("("+componentScore.toString()+")");
			}
			if (waistPref == false) {
				waist.setText("Exempt");
				waist.setTextColor(Color.YELLOW);
				waistScore.setText("");
			}
			else if (bool_waist == true){
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
		case MINMAX_DIALOG_ID:
			prepareMinMaxDialog(dialog);
			break;
		case AFICHART_DIALOG_ID:
			prepareAfiChartDialog(dialog);
			break;
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		// This is 'position-1' because of the header
		HashMap<?, ?> choice = (HashMap<?, ?>) this.getListAdapter().getItem(position-1);

		if (choice.get("option").equals("Run Time"))
			showDialog(RUN_DIALOG_ID);
		else if (choice.get("option").equals("Walk Time"))
			showDialog(WALK_DIALOG_ID);
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
		else if (choice.get("option").equals("Exact Age"))
			showDialog(EXACT_AGE_DIALOG_ID);
		else if (choice.get("option").equals("Heart Rate"))
			showDialog(HEART_RATE_DIALOG_ID);
		else if (choice.get("option").equals("Weight"))
			showDialog(WEIGHT_DIALOG_ID);

	}

	public void buttonClickHandler(View v)
	{

		switch(v.getId()){
		case R.id.calculateButton:
			if (!aerobicPref && !waistPref && !situpPref && !pushupPref){
				Toast.makeText(getApplicationContext(), "You must test in atleast one component.", Toast.LENGTH_SHORT).show();
				break;
			}
			else if (calculateReady() == false){
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

	private boolean calculateReady(){
		if (calculatorVO.getGender() == CalculatorVO.UNASSIGNED
				|| calculatorVO.getAgeGroup() == CalculatorVO.UNASSIGNED)
			return false;

		if (pushupPref == true && calculatorVO.getPushups() == CalculatorVO.UNASSIGNED)
			return false;
		else if (situpPref == true && calculatorVO.getSitups() == CalculatorVO.UNASSIGNED)
			return false;
		else if (aerobicPref == true && calculatorVO.getRunWalkMinute() == CalculatorVO.UNASSIGNED)
			return false;
		else if (waistPref == true && calculatorVO.getWaist() == CalculatorVO.UNASSIGNED)
			return false;
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pfa_calculator, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.afiChartsButton:
			if (calculatorVO.getAgeGroup() == CalculatorVO.UNASSIGNED || calculatorVO.getGender() == CalculatorVO.UNASSIGNED){
				Toast.makeText(getApplicationContext(), R.string.genderandage, Toast.LENGTH_SHORT).show();
				return false;
			} else {
				showDialog(AFICHART_DIALOG_ID);
				return true;
			}
		case R.id.minmaxMenuButton:
			if (calculatorVO.getAgeGroup() == CalculatorVO.UNASSIGNED || calculatorVO.getGender() == CalculatorVO.UNASSIGNED){
				Toast.makeText(getApplicationContext(), R.string.genderandage, Toast.LENGTH_SHORT).show();
				return false;
			} else {
				showDialog(MINMAX_DIALOG_ID);
				return true;
			}
		case R.id.preferencesMenuButton:
			Intent preferencesActivity = new Intent(getBaseContext(), Preferences.class);
			startActivity(preferencesActivity);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getPrefs(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		pushupPref = prefs.getBoolean("pushupPref", true);
		situpPref = prefs.getBoolean("situpPref", true);
		aerobicPref = prefs.getBoolean("runPref", true);
		waistPref = prefs.getBoolean("waistPref", true);
		aerobicCompPref = prefs.getString("aerobicCompPref", "1");

		// USED FOR DEBUGGING;

		//		Log.d("getPrefs()", aerobicCompPref);
		//		if (aerobicCompPref.equals(Integer.toString(RUN))){
		//			Log.d("getPrefs()", "RUNNING!");
		//		}
		//		else {
		//			Log.d("getPrefs()", "WALKING!");
		//		}
	}

	private void updateList() {
		optionsList = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> temp = new HashMap<String,String>();
		temp.put("option","Gender");
		temp.put("selection", calculatorVO.getGenderString());
		temp.put("description", "Male or Female");
		optionsList.add(temp);

		if (pushupPref == true){
			HashMap<String,String> temp1 = new HashMap<String,String>();
			temp1.put("option","Push-Ups");
			temp1.put("selection", calculatorVO.getPushupsString());
			temp1.put("description", "Amount Per Minute");
			optionsList.add(temp1);
		}

		if (situpPref == true){
			HashMap<String,String> temp2 = new HashMap<String,String>();
			temp2.put("option","Sit-Ups");
			temp2.put("selection", calculatorVO.getSitupsString());
			temp2.put("description", "Amount Per Minute");
			optionsList.add(temp2);
		}

		if (aerobicPref == true){
			HashMap<String,String> temp3 = new HashMap<String,String>();

			if (aerobicCompPref.equals(Integer.toString(RUN))){
				temp3.put("option","Run Time");
				temp3.put("selection", calculatorVO.getRunWalkString());
				temp3.put("description", "1.5 Mile Run");
			} else if (aerobicCompPref.equals(Integer.toString(WALK))) {
				temp3.put("option","Walk Time");
				temp3.put("selection", calculatorVO.getRunWalkString());
				temp3.put("description", "2.0 Kilometer Walk");
			}
			optionsList.add(temp3);
		}



		HashMap<String,String> temp4 = new HashMap<String,String>();
		temp4.put("option","Age Group");
		temp4.put("selection", calculatorVO.getAgeGroupString());
		temp4.put("description", "Age Category");
		optionsList.add(1, temp4);

		if (waistPref == true){
			HashMap<String,String> temp5 = new HashMap<String,String>();
			temp5.put("option","Waist Measurement");
			temp5.put("selection", calculatorVO.getWaistString());
			temp5.put("description", "Abdominal Circumference");
			optionsList.add(2, temp5);
		}

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

		builder.setItems(array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				calculatorVO.setAgeGroup(item);
				updateList();
			}
		});

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
			np.setRange(0, 90);
			np.setCurrent(DEFAULT_SITUP);
			break;
		case PUSHUP_DIALOG_ID:
			np.setRange(0, 100);
			np.setCurrent(DEFAULT_PUSHUP);
			break;
		case EXACT_AGE_DIALOG_ID:
			np.setRange(14,70);
			np.setCurrent(DEFAULT_EXACT_AGE);
			break;
		case HEART_RATE_DIALOG_ID:
			np.setRange(80, 250);
			np.setCurrent(DEFAULT_HEART_RATE);
			break;
		case WEIGHT_DIALOG_ID:
			np.setRange(60, 300);
			np.setCurrent(DEFAULT_WEIGHT);
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
		inches.setRange(20,50);
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
		minutes.setRange(0, 40);
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
		alert.setTitle(calculatorVO.getGenderString()+"; "+calculatorVO.getAgeGroupString());  

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				return;                  
			}  
		});  

		AlertDialog ad = alert.create();
		return ad;
	}

	private void prepareMinMaxDialog(Dialog dialog){
		ScoreCalculator sc = new ScoreCalculator(this, calculatorVO, null);
		dialog.setTitle(calculatorVO.getGenderString()+"; "+calculatorVO.getAgeGroupString());

		TextView pushup_min = (TextView) dialog.findViewById(R.id.pushup_min);
		pushup_min.setText(Integer.toString(sc.getPushupMin()));
		TextView pushup_max = (TextView) dialog.findViewById(R.id.pushup_max);
		pushup_max.setText(Integer.toString(sc.getPushupMax()));

		TextView situp_min = (TextView) dialog.findViewById(R.id.situp_min);
		situp_min.setText(Integer.toString(sc.getSitupMin()));
		TextView situp_max = (TextView) dialog.findViewById(R.id.situp_max);
		situp_max.setText(Integer.toString(sc.getSitupMax()));

		TextView waist_min = (TextView) dialog.findViewById(R.id.waist_min);
		waist_min.setText(Double.toString(sc.getWaistMin()));
		TextView waist_max = (TextView) dialog.findViewById(R.id.waist_max);
		waist_max.setText(Double.toString(sc.getWaistMax()));

		int runmin = sc.getRunMin();
		int runmax = sc.getRunMax();
		String runminstr = Integer.toString(runmin/100)+":"+Integer.toString(runmin%100);
		if (runmin%100 == 0)
			runminstr = Integer.toString(runmin/100) + ":00";
		String runmaxstr = Integer.toString(runmax/100)+":"+Integer.toString(runmax%100);
		if (runmax%100 == 0)
			runmaxstr = Integer.toString(runmax/100) + ":00";
		TextView run_min = (TextView) dialog.findViewById(R.id.run_min);
		run_min.setText(runminstr);
		TextView run_max = (TextView) dialog.findViewById(R.id.run_max);
		run_max.setText(runmaxstr);

	}

	private AlertDialog afiChartsDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.aficharts_dialog,
				(ViewGroup) findViewById(R.id.afi_dialog_layout_root));
		alert.setTitle(calculatorVO.getGenderString()+"; "+calculatorVO.getAgeGroupString());  

		alert.setView(layout);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {
				return;                  
			}  
		});  

		AlertDialog ad = alert.create();
		return ad;
	}

	private void prepareAfiChartDialog(Dialog dialog){
		ScoreCalculator sc = new ScoreCalculator(this, calculatorVO, null);

		PFAUtils.setupAfiPushupTbl(this, dialog, sc);
		PFAUtils.setupAfiSitupTbl(this, dialog, sc);
		PFAUtils.setupAfiWaistTbl(this, dialog, sc);
		PFAUtils.setupAfiRunTbl(this, dialog, sc);
	}
}