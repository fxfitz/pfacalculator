package com.rwoar.pfacalculator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class PFAUtils {

	public static String formatIntToStrTime(int time){
		int minute = getRunMinute(time);
		int second = getRunSecond(time);
		return new String(minute+":"+String.format("%02d", second));
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

	private static int getRunMinute(int time){
		return time/100;
	}

	private static int getRunSecond(int time){
		return time%100;
	}

	/**
	 * Clears all of the TableRows from a TableLayout, with the EXCEPTION
	 * of the first row! The first row should be the component category
	 * (Situps, Pushups, Run, Waist) and should therefore never be deleted.
	 * @param table
	 */
	public static void clearAfiTable(TableLayout table){
		int count = table.getChildCount();
		for (int i = 1; i < count; i++) {
			View child = table.getChildAt(i);
			if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
		}
	}

	private static TableRow generateAfiTblRow(Context context, String first, String second){
		TableRow tr = new TableRow(context);
		tr.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView amount = new TextView(context);
		amount.setText(first);
		tr.setGravity(Gravity.CENTER);
		tr.addView(amount);

		TextView score = new TextView(context);
		score.setText(second);
		tr.setGravity(Gravity.CENTER);
		tr.addView(score);

		return tr;
	}

	public static void setupAfiPushupTbl(Context context, Dialog dialog, ScoreCalculator sc){
		TableLayout tl = (TableLayout)dialog.findViewById(R.id.afi_pushup_table);
		PFAUtils.clearAfiTable(tl);

		tl.addView(generateAfiTblRow(context, Integer.toString(sc.getPushupMax())+"+", Double.toString(sc.getIndividualPushupScore(sc.getPushupMax()))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (int i = sc.getPushupMax()-1; i >= sc.getPushupMin(); i--){
			tl.addView(generateAfiTblRow(context, Integer.toString(i), Double.toString(sc.getIndividualPushupScore(i))),new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}

		tl.addView(generateAfiTblRow(context, "<="+Integer.toString(sc.getPushupMin()-1), Double.toString(sc.getIndividualPushupScore(sc.getPushupMin()-1))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	public static void setupAfiSitupTbl(Context context, Dialog dialog, ScoreCalculator sc){
		TableLayout tl = (TableLayout)dialog.findViewById(R.id.afi_situp_table);
		PFAUtils.clearAfiTable(tl);

		tl.addView(generateAfiTblRow(context, Integer.toString(sc.getSitupMax())+"+", Double.toString(sc.getIndividualSitupScore(sc.getSitupMax()))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (int i = sc.getSitupMax()-1; i >= sc.getSitupMin(); i--){
			tl.addView(generateAfiTblRow(context, Integer.toString(i), Double.toString(sc.getIndividualSitupScore(i))),new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}

		tl.addView(generateAfiTblRow(context, "<="+Integer.toString(sc.getSitupMin()-1), Double.toString(sc.getIndividualSitupScore(sc.getSitupMin()-1))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	public static void setupAfiWaistTbl(Context context, Dialog dialog, ScoreCalculator sc){
		TableLayout tl = (TableLayout)dialog.findViewById(R.id.afi_waist_table);
		PFAUtils.clearAfiTable(tl);

		tl.addView(generateAfiTblRow(context, "<="+sc.getWaistMax(), Double.toString(sc.getIndividualWaistScore(sc.getWaistMax()))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (double i = sc.getWaistMax()+.5; i <= sc.getWaistMin(); i = i+.5){
			tl.addView(generateAfiTblRow(context, Double.toString(i), Double.toString(sc.getIndividualWaistScore(i))),new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}

		tl.addView(generateAfiTblRow(context, sc.getWaistMin()+.5+"+", Double.toString(sc.getIndividualWaistScore(sc.getWaistMin()+.5))),new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	public static void setupAfiRunTbl(Context context, Dialog dialog, ScoreCalculator sc){
		Integer current, next;

		TableLayout tl = (TableLayout)dialog.findViewById(R.id.afi_run_table);
		PFAUtils.clearAfiTable(tl);

		LinkedList<Integer> intervals = sc.getRunIntervals();
		ListIterator<Integer> itr = (ListIterator<Integer>) intervals.iterator();

		// Check to make sure there are no intervals.
		// This should NEVER trigger
		if (!itr.hasNext()){
			try {
				throw new IOException("");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			current = itr.next();

			tl.addView(generateAfiTblRow(context, "<="+PFAUtils.formatIntToStrTime(current.intValue()-1), Double.toString(sc.getIndividualRunScore(PFAUtils.getRunMinute(current), PFAUtils.getRunSecond(current-1)))),new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			itr.previous();
		}

		while(itr.hasNext()){
			current = itr.next();
			
			if (!sc.passedRun(current)){
				tl.addView(generateAfiTblRow(context, ">="+PFAUtils.formatIntToStrTime(current.intValue()), Double.toString(sc.getIndividualRunScore(PFAUtils.getRunMinute(current), PFAUtils.getRunSecond(current)))),new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				break;
			} 
			
			if (itr.hasNext()){
				next = itr.next();

				tl.addView(generateAfiTblRow(context, PFAUtils.formatIntToStrTime(current.intValue())+"-"+PFAUtils.formatIntToStrTime(next.intValue()-1), Double.toString(sc.getIndividualRunScore(PFAUtils.getRunMinute(current), PFAUtils.getRunSecond(current)))),new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				
				itr.previous();
			} else {
				tl.addView(generateAfiTblRow(context, PFAUtils.formatIntToStrTime(current.intValue()), Double.toString(sc.getIndividualRunScore(PFAUtils.getRunMinute(current), PFAUtils.getRunSecond(current)))),new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));	
			}
		}		
	}
}
