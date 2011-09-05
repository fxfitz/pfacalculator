package com.rwoar.pfacalculator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

public class ScoreCalculator {

	private final double MIN_TOTAL_SCORE = 75.0;
	private final CalculatorVO calculatorVO;
	private final Activity parentActivity;
	private final Properties waistProperties;
	private final Properties situpProperties;
	private final Properties pushupProperties;
	private final Properties runProperties;
	private final Properties walkProperties;
	
	private double runScore = 0;
	private double situpScore = 0;
	private double pushupScore = 0;
	private double waistScore = 0;
	private double walkScore = 0;
	
	private int min_situp, max_situp, min_situp_point;
	private int min_pushup, max_pushup, min_pushup_point;
	private double min_waist, max_waist;
	private int min_run, max_run;
	private int min_walk, max_walk;
	
	private boolean pushupPref;
	private boolean situpPref;
	private boolean aerobicPref;
	private boolean waistPref;
	private String aerobicCompPref;
	
	private final int RUN = 1;
	private final int WALK = 2;
		
	// Used for rounding scores to one decimal place
	private final DecimalFormat oneDForm = new DecimalFormat("#.#");

	public ScoreCalculator(Activity ac, CalculatorVO calc, SharedPreferences prefs){
//		if (calc.isComplete() == false){
//			throw new RuntimeException("All options must be selected (pushups, situps, gender, etc)");
//		}
		
		if (prefs != null)
			setPrefs(prefs);
		
		calculatorVO = calc;
		parentActivity = ac;
		pushupProperties = this.getProperties("pushups.properties");
		situpProperties = this.getProperties("situps.properties");
		waistProperties = this.getProperties("waist.properties");
		runProperties = this.getProperties("run.properties");
		walkProperties = this.getProperties("walk.properties");
		
		waistScore = setWaistScore();
		if (situpPref)
			situpScore = setSitupScore();
		if (pushupPref)
			pushupScore = setPushupScore();
		if (aerobicPref){
			if (aerobicCompPref.equals(Integer.toString(RUN)))
				runScore = setRunScore();
			else
				walkScore = setWalkScore();
		}
		
		setWaistMinMax();	
		setPushupMinMax();
		setSitupMinMax();
		setRunMinMax();
		setWalkMinMax();
		
	}
	
	private void setPrefs(SharedPreferences prefs){
		pushupPref = prefs.getBoolean("pushupPref", true);
		situpPref = prefs.getBoolean("situpPref", true);
		aerobicPref = prefs.getBoolean("runPref", true);
		waistPref = prefs.getBoolean("waistPref", true);
		aerobicCompPref = prefs.getString("aerobicCompPref", "1");
	}
	
	public double getTotalScore(){
		//double total = situpScore + pushupScore + runScore + waistScore;
		double totalpoints = 0;
		double maxpoints = 0;
		double total;
		
		if (pushupPref == true){
			maxpoints += Double.parseDouble(pushupProperties.getProperty("max_point"));
			totalpoints += pushupScore;
		}
		if (situpPref == true){
			maxpoints += Double.parseDouble(situpProperties.getProperty("max_point"));
			totalpoints += situpScore;
		}
		if (aerobicPref == true){
			maxpoints += Double.parseDouble(runProperties.getProperty("max_point"));
			if (aerobicCompPref.equals(Integer.toString(RUN)))
				totalpoints += runScore;
			else
				totalpoints += walkScore;
		}
		if (waistPref == true){
			maxpoints += Double.parseDouble(waistProperties.getProperty("max_point"));
			totalpoints += waistScore;
		}
		
		total = totalpoints / maxpoints * 100;
		return Double.valueOf(oneDForm.format(total));		
	}
	
	public double getPushupScore(){
		return pushupScore;
	}
	
	public int getPushupMin(){
		return this.min_pushup;
	}
	
	public int getPushupMinPoint(){
		return this.min_pushup_point;
	}
	
	public int getPushupMax(){
		return this.max_pushup;
	}
	
	public double getSitupScore(){
		return situpScore;
	}
	
	public int getSitupMin(){
		return this.min_situp;
	}
	
	public int getSitupMinPoint(){
		return this.min_situp_point;
	}
	
	public int getSitupMax(){
		return this.max_situp;
	}
	
	public double getRunScore(){
		return runScore;
	}
	
	public int getRunMin(){
		return this.min_run;
	}
	
	public int getRunMax(){
		return this.max_run;
	}
	
	public double getWalkScore(){
		return walkScore;
	}
	
	public int getWalkMin(){
		return this.min_walk;
	}
	
	public int getWalkMax(){
		return this.max_walk;
	}
	
	public double getWaistScore(){
		return waistScore;
	}
	
	public double getWaistMin(){
		return min_waist;
	}
	
	public double getWaistMax(){
		return max_waist;
	}
	
	public boolean passedPushups(){
		if (pushupPref && calculatorVO.getPushups() < Integer.parseInt(pushupProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedSitups(){
		if (situpPref && calculatorVO.getSitups() < Integer.parseInt(situpProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedWaist(){
		if (waistPref && calculatorVO.getWaist() > Double.parseDouble(waistProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedRun(){
		int runtime = Integer.parseInt(Integer.toString(calculatorVO.getRunMinute()*100+calculatorVO.getRunSecond()));
		if (aerobicPref && aerobicCompPref.equals(Integer.toString(RUN)) 
				&& runtime > Integer.parseInt(runProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedWalk(){
		if (aerobicPref && aerobicCompPref.equals(Integer.toString(WALK)) 
				&& getVO2Max() < Integer.parseInt(walkProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedOverallTest(){
		if (passedRun() && passedWalk() && passedWaist() && passedSitups() && 
				passedPushups() && getTotalScore() >= MIN_TOTAL_SCORE)
			return true;
		else
			return false;
	}
	
	private double setPushupScore(){
		int pushups = calculatorVO.getPushups();
		double score = getIndividualPushupScore(pushups);
		return score;
	}

	private void setPushupMinMax(){
		this.min_pushup = Integer.parseInt(pushupProperties.getProperty("min_pass_amount"));
		this.max_pushup = Integer.parseInt(pushupProperties.getProperty("max_point_amount"));
		this.min_pushup_point = Integer.parseInt(pushupProperties.getProperty("min_point_amount"));
	}
	
	private double setSitupScore(){
		int situps = calculatorVO.getSitups();
		double score = getIndividualSitupScore(situps);
		return score;
	}
	
	private void setSitupMinMax(){
		this.min_situp = Integer.parseInt(situpProperties.getProperty("min_pass_amount"));
		this.max_situp = Integer.parseInt(situpProperties.getProperty("max_point_amount"));
		this.min_situp_point = Integer.parseInt(situpProperties.getProperty("min_point_amount"));
	}


	private double setRunScore(){
		return getIndividualRunScore(calculatorVO.getRunMinute(), calculatorVO.getRunSecond());
	}
	

	private void setRunMinMax(){
		this.min_run = Integer.parseInt(runProperties.getProperty("min_pass_amount"));
		this.max_run = Integer.parseInt(runProperties.getProperty("max_point_amount"));
	}
	
	private int getVO2Max(){
		int weight = calculatorVO.getWeight();
		int age = calculatorVO.getExactAge();
		double run = calculatorVO.getRunMinute() + (calculatorVO.getRunSecond()/60.0);
		int hr = calculatorVO.getHeartRate();
				
		//Log.d("getVO2Max()", "weight="+weight+";age="+age+";run="+run+";hr="+hr);
		
		int int_vo2max;
		double VO2max = 132.853 - (.0769 * weight) - (.3877 * age) - (3.2649 * run) - (.1565*hr);
		if (calculatorVO.getGender() == CalculatorVO.MALE)
			VO2max += 6.315;
		
		int_vo2max = (int) Math.round(VO2max);
		
		return int_vo2max;
	}
	
	private double setWalkScore(){
		double score = 0;
		int VO2max = getVO2Max();
		
		if (VO2max >= Double.parseDouble(walkProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(walkProperties.getProperty("max_point"));
		}
		else if (VO2max < Double.parseDouble(walkProperties.getProperty("min_pass_amount"))){
			score = Double.parseDouble(walkProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(walkProperties.getProperty(""+VO2max));
		}

		
		return Double.valueOf(oneDForm.format(score));
	}
	
	private void setWalkMinMax(){
		this.min_walk = Integer.parseInt(walkProperties.getProperty("min_pass_amount"));
		this.max_walk = Integer.parseInt(walkProperties.getProperty("max_point_amount"));
	}

	private double setWaistScore(){
		double waist = calculatorVO.getWaist();
		double score = getIndividualWaistScore(waist);
		return score;

	}

	private void setWaistMinMax(){
		this.min_waist = Double.parseDouble(waistProperties.getProperty("min_pass_amount"));
		this.max_waist = Double.parseDouble(waistProperties.getProperty("max_point_amount"));
	}
	
	private Properties getProperties(String filename){
		AssetManager assetManager = parentActivity.getAssets();
		Properties prop = new Properties();
		String propFilePath = "";
		String ageGroup = new String();

		// ageGroup corresponds to the folder name in assets/pfacalculator/
		switch(calculatorVO.getAgeGroup()){
		case CalculatorVO.RUN_UNDER30:
			ageGroup = "under30";
			break;
		case CalculatorVO.RUN_30TO39:
			ageGroup = "30-39";
			break;
		case CalculatorVO.RUN_40TO49:
			ageGroup = "40-49";
			break;
		case CalculatorVO.RUN_50TO59:
			ageGroup = "50-59";
			break;
		case CalculatorVO.RUN_60OVER:
			ageGroup = "60+";
			break;
		default:
			throw new RuntimeException();
		}

		propFilePath += ageGroup;
		propFilePath += "/" + calculatorVO.getGenderString().toLowerCase();
		propFilePath += "/" + filename;
		

		try {
			prop.load(assetManager.open(propFilePath));
		} catch (IOException e) {
			Log.e("ScoreCalculator.getProperties()", "FAILED TO OPEN PROPERTIES FILE!");
		}

		return prop;
	}
	
	public double getIndividualPushupScore(int pushups){
		double score = 0;
		if (pushups >= Integer.parseInt(pushupProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(pushupProperties.getProperty("max_point"));
		}
		// Changing from min_point_amount to min_pass_amount because of regulation 
		// change effective 1 Jan 2011 
		else if (pushups < Integer.parseInt(pushupProperties.getProperty("min_pass_amount"))){
			score = Double.parseDouble(pushupProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(pushupProperties.getProperty(""+pushups));
		}
		
		return score;
	}
	
	public double getIndividualSitupScore(int situps){
		double score = 0;

		if (situps >= Integer.parseInt(situpProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(situpProperties.getProperty("max_point"));
		}
		else if (situps < Integer.parseInt(situpProperties.getProperty("min_pass_amount"))){
			score = Double.parseDouble(situpProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(situpProperties.getProperty(""+situps));
		}

		return Double.valueOf(oneDForm.format(score));
	}
	
	public double getIndividualWaistScore(double waist){
		double score = 0;
		
		if (waist <= Double.parseDouble(waistProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(waistProperties.getProperty("max_point"));
		}
		else if (waist > Double.parseDouble(waistProperties.getProperty("min_pass_amount"))){
			score = Double.parseDouble(waistProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(waistProperties.getProperty(""+waist));
		}

		
		return Double.valueOf(oneDForm.format(score));
	}
	
	/**
	 * Currently this method is NOT efficient. But you know what? Who cares? All of
	 * the intervals are very small so there's really no need to be efficient. One day
	 * we'll make it efficient. :-D
	 * @return
	 */
	public double getIndividualRunScore(int minute, int second){
		double score = 0;
		// minute*100 + second
		// this formula effectively removes the : from the time
		int runtime = PFAUtils.formatToIntTime(minute, second);

		// First check to see runtime is above max_point_amount or under min_point_amount
		if (runtime <= Integer.parseInt(runProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(runProperties.getProperty("max_point"));
		}
		else if (runtime > Integer.parseInt(runProperties.getProperty("min_pass_amount"))){
			score = Double.parseDouble(runProperties.getProperty("min_point"));
		}
		else{
			// Get intervals
			LinkedList<Integer> startIntervals = getRunIntervals();

			ListIterator<Integer> intervalItr = startIntervals.listIterator();
			while (intervalItr.hasNext()){
				Integer time = intervalItr.next();
		
				if (runtime < time){
					// The double call is needed apparently :(
					time = intervalItr.previous();
					time = intervalItr.previous();
					score = Double.parseDouble(runProperties.getProperty(Integer.toString(time)));
					break;
				}
			}
		}

		
		return Double.valueOf(oneDForm.format(score));
	}
	
	public LinkedList<Integer> getRunIntervals(){
		LinkedList<Integer> startIntervals = new LinkedList<Integer>();
		Set<?> keys=runProperties.keySet();
		Iterator<String> keysItr = (Iterator<String>) keys.iterator();
		while(keysItr.hasNext()){
			String key = (String) keysItr.next();
			try{
				startIntervals.add(Integer.parseInt(key));
			} catch (Exception e){
				// Ignore it!
			}
		}
		
		// Sort the intervals
		Collections.sort(startIntervals);
		
		return startIntervals;
	}
}
