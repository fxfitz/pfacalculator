package com.rwoar.pfacalculator;

public class PFAUtils {
	
	public static String formatIntToStrTime(int time){
		int minute = time/100;
		int second = time%100;
		return new String(minute+":"+second);
	}
	
	public static int formatToIntTime(int minute, int second){
		return minute*100+second;
	}
	
	public static int formatStrToIntTime(String time){
		int colonLocation = time.indexOf(":");
		int minute = Integer.parseInt(time.substring(0, colonLocation));
		int second = Integer.parseInt(time.substring(colonLocation+1));
		return formatToIntTime(minute,second);
	}
}
