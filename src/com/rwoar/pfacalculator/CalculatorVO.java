package com.rwoar.pfacalculator;

import java.io.Serializable;
import java.util.LinkedHashMap;


public class CalculatorVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int UNASSIGNED = -1;
	public static final int MALE = 1;
	public static final int FEMALE = 2;
	public static final int RUN_UNDER30 = 0;
	public static final int RUN_30TO39 = 1;
	public static final int RUN_40TO49 = 2;
	public static final int RUN_50TO59 = 3;
	public static final int RUN_60OVER = 4;
	public final LinkedHashMap<Integer, String> ageStringsMap = new LinkedHashMap<Integer,String>();
	private static final int MINUTE = 0;
	private static final int SECOND = 1;
	private final String unassignedString = 
		new String("Unassigned");
	
	private int gender;
	private int agegroup;
	private int situps = 0;
	private int pushups = 0;
	private double waist;
	private int[] runwalk = new int[2];
	private int exact_age;
	private int heart_rate;
	private int weight;
	
	
	public CalculatorVO(){
		this.ageStringsMap.put(CalculatorVO.RUN_UNDER30, "Under 30");
		this.ageStringsMap.put(CalculatorVO.RUN_30TO39, "30 to 39");
		this.ageStringsMap.put(CalculatorVO.RUN_40TO49, "40 to 49");
		this.ageStringsMap.put(CalculatorVO.RUN_50TO59, "50 to 59");
		this.ageStringsMap.put(CalculatorVO.RUN_60OVER, "60+");
		
		clear();
	}
	
	public void clear(){
		this.gender = UNASSIGNED;
		this.agegroup = UNASSIGNED;
		this.situps = UNASSIGNED;
		this.pushups = UNASSIGNED;
		this.waist = UNASSIGNED;
		this.runwalk[MINUTE] = UNASSIGNED;
		this.runwalk[SECOND] = UNASSIGNED;
		this.exact_age = UNASSIGNED;
		this.heart_rate = UNASSIGNED;
		this.weight = UNASSIGNED;
	}
	
	/**
	 * Returns true if all 
	 * @return
	 */
	@Deprecated
	public boolean isComplete(){
		if (this.getAgeGroup() == UNASSIGNED || this.getGender() == UNASSIGNED
				|| this.getSitups() == UNASSIGNED || this.getPushups() == UNASSIGNED
				|| this.getRunMinute() == UNASSIGNED || this.getRunSecond() == UNASSIGNED
				|| this.getWaist() == UNASSIGNED)
			return false;
		else
			return true;
	}
	
	public void setGender(int choice){
		if (choice != MALE && choice != FEMALE)
			this.gender = UNASSIGNED;
		else
			this.gender = choice;
	}
	
	public int getGender(){
		return this.gender;
	}
	
	public String getGenderString(){
		String gender = new String();
		switch(this.gender){
		case MALE:
			gender = "Male";
			break;
		case FEMALE:
			gender = "Female";
			break;
		default:
			gender = unassignedString;
			break;
		}
		
		return gender;
	}
	
	public void setAgeGroup(int age){
		if (age != CalculatorVO.RUN_UNDER30 && age != CalculatorVO.RUN_30TO39 && age != CalculatorVO.RUN_40TO49
				&& age != CalculatorVO.RUN_50TO59 && age != CalculatorVO.RUN_60OVER)
			this.agegroup = UNASSIGNED;
		else
			this.agegroup = age;
	}
	
	public int getAgeGroup(){
		return this.agegroup;
	}
	
	public String getAgeGroupString(){
		if (getAgeGroup() == UNASSIGNED)
			return this.unassignedString;
		else
			return this.ageStringsMap.get(this.getAgeGroup());	
	}
	
	public void setSitups(int situps){
		this.situps = situps;
	}
	
	public int getSitups(){
		return this.situps;
	}
	
	public String getSitupsString(){
		String situpString = new String();
		if (getSitups() == UNASSIGNED)
			situpString = unassignedString;
		else
			situpString = Integer.toString(getSitups());
		
		return situpString;
	}
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	public int getWeight(){
		return this.weight;
	}
	
	public String getWeightString(){
		String weightString = new String();
		if (getWeight() == UNASSIGNED)
			weightString = unassignedString;
		else
			weightString = Integer.toString(getWeight()) + " lbs";
		
		return weightString;
	}
	
	public void setPushups(int pushups){
		this.pushups = pushups;
	}
	
	public int getPushups(){
		return this.pushups;
	}
	
	public String getPushupsString(){
		String pushupString = new String();
		if (getPushups() == UNASSIGNED)
			pushupString = unassignedString;
		else
			pushupString = Integer.toString(getPushups());
		
		return pushupString;
	}
	
	public int getRunMinute(){
		return this.runwalk[MINUTE];
	}
	
	public int getRunSecond(){
		return this.runwalk[SECOND];
	}
	
	public String getRunWalkString(){
		String runTime = new String();
		if (getRunMinute() == UNASSIGNED || 
				getRunSecond() == UNASSIGNED){
			runTime = unassignedString;
		}
		else
			runTime = getRunMinute() + ":" + String.format("%02d", getRunSecond());
	
		
		return runTime;
	}
	
	public void setRun(int minute, int second){
		this.runwalk[MINUTE] = minute;
		this.runwalk[SECOND] = second;
	}
	
	public void setWaist(double waist){
		this.waist = waist;
	}
	
	public double getWaist(){
		return this.waist;
	}
	
	public String getWaistString(){
		String waistString = new String();
		if (getWaist() == UNASSIGNED)
			waistString = unassignedString;
		else
			waistString = Double.toString(getWaist()) + " inches";
		
		return waistString;
	}
	
	public int getExactAge(){
		return exact_age;
	}
	
	public void setExactAge(int ea){
		exact_age = ea;
	}
	
	public String getExactAgeString(){
		String exactAgeString = new String();
		if (getExactAge() == UNASSIGNED)
			exactAgeString = unassignedString;
		else
			exactAgeString = Integer.toString(getExactAge()) + " years old";
		
		return exactAgeString;
	}
	
	public int getHeartRate(){
		return heart_rate;
	}
	
	public void setHeartRate(int hr){
		heart_rate = hr;
	}
	
	public String getHeartRateString(){
		String heartRateString = new String();
		if (getHeartRate() == UNASSIGNED)
			heartRateString = unassignedString;
		else
			heartRateString = Integer.toString(getHeartRate()) + " bpm";
		
		return heartRateString;
	}

}
