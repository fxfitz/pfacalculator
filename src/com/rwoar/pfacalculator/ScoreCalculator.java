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
	
	private double runScore;
	private double situpScore;
	private double pushupScore;
	private double waistScore;
		
	// Used for rounding scores to one decimal place
	private final DecimalFormat oneDForm = new DecimalFormat("#.#");

	public ScoreCalculator(Activity ac, CalculatorVO calc){
		if (calc.isComplete() == false){
			throw new RuntimeException("All options must be selected (pushups, situps, gender, etc)");
		}
		calculatorVO = calc;
		parentActivity = ac;
		pushupProperties = this.getProperties("pushups.properties");
		situpProperties = this.getProperties("situps.properties");
		waistProperties = this.getProperties("waist.properties");
		runProperties = this.getProperties("run.properties");
		situpScore = setSitupScore();
		pushupScore = setPushupScore();
		waistScore = setWaistScore();
		runScore = setRunScore();
	}
	
	public double getTotalScore(){
		double total = situpScore + pushupScore + runScore + waistScore;
				
		return Double.valueOf(oneDForm.format(total));		
	}
	
	public double getPushupScore(){
		return pushupScore;
	}
	
	public double getSitupScore(){
		return situpScore;
	}
	
	public double getRunScore(){
		return runScore;
	}
	
	public double getWaistScore(){
		return waistScore;
	}
	
	public boolean passedPushups(){
		if (calculatorVO.getPushups() < Integer.parseInt(pushupProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedSitups(){
		if (calculatorVO.getSitups() < Integer.parseInt(situpProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedWaist(){
		if (calculatorVO.getWaist() > Double.parseDouble(waistProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedRun(){
		int runtime = Integer.parseInt(Integer.toString(calculatorVO.getRunMinute()*100+calculatorVO.getRunSecond()));
		if (runtime > Integer.parseInt(runProperties.getProperty("min_pass_amount"))){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean passedOverallTest(){
		if (passedRun() && passedWaist() && passedSitups() && 
				passedPushups() && getTotalScore() >= MIN_TOTAL_SCORE)
			return true;
		else
			return false;
	}
	
	private double setPushupScore(){
		double score = 0;
		int pushups = calculatorVO.getPushups();

		if (pushups >= Integer.parseInt(pushupProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(pushupProperties.getProperty("max_point"));
		}
		else if (pushups <= Integer.parseInt(pushupProperties.getProperty("min_point_amount"))){
			score = Double.parseDouble(pushupProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(pushupProperties.getProperty(""+pushups));
		}

		
		return Double.valueOf(oneDForm.format(score));
	}

	private double setSitupScore(){
		double score = 0;
		int situps = calculatorVO.getSitups();

		if (situps >= Integer.parseInt(situpProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(situpProperties.getProperty("max_point"));
		}
		else if (situps <= Integer.parseInt(situpProperties.getProperty("min_point_amount"))){
			score = Double.parseDouble(situpProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(situpProperties.getProperty(""+situps));
		}

		return Double.valueOf(oneDForm.format(score));
	}

	/**
	 * Currently this method is NOT efficient. But you know what? Who cares? All of
	 * the intervals are very small so there's really no need to be efficient. One day
	 * we'll make it efficient. :-D
	 * @return
	 */
	private double setRunScore(){
		double score = 0;
		// minute*100 + second
		// this formula effectively removes the : from the time
		int runtime = Integer.parseInt(Integer.toString(calculatorVO.getRunMinute()*100+calculatorVO.getRunSecond()));

		// First check to see runtime is above max_point_amount or under min_point_amount
		if (runtime <= Integer.parseInt(runProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(runProperties.getProperty("max_point"));
		}
		else if (runtime >= Integer.parseInt(runProperties.getProperty("min_point_amount"))){
			score = Double.parseDouble(runProperties.getProperty("min_point"));
		}
		else{
			// Get intervals
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
			Collections.sort(startIntervals);

			ListIterator<Integer> intervalItr = startIntervals.listIterator();
			while (intervalItr.hasNext()){
				Integer time = intervalItr.next();

				if (runtime >= time && runtime < intervalItr.next()){
					score = Double.parseDouble(runProperties.getProperty(Integer.toString(time)));
					break;
				}
				else{
					intervalItr.previous();
				}
			}
		}

		
		return Double.valueOf(oneDForm.format(score));
	}

	private double setWaistScore(){
		double score = 0;
		double waist = calculatorVO.getWaist();

		if (waist <= Double.parseDouble(waistProperties.getProperty("max_point_amount"))){
			score = Double.parseDouble(waistProperties.getProperty("max_point"));
		}
		else if (waist >= Double.parseDouble(waistProperties.getProperty("min_point_amount"))){
			score = Double.parseDouble(waistProperties.getProperty("min_point"));
		}
		else {
			score = Double.parseDouble(waistProperties.getProperty(""+waist));
		}

		
		return Double.valueOf(oneDForm.format(score));
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

}
