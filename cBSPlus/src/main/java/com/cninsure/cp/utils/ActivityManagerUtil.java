package com.cninsure.cp.utils;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivityManagerUtil {
	
	public static List<Activity> activityList = null;
	private static ActivityManagerUtil amu = null;

	
	public static ActivityManagerUtil getInstance() {
		if (amu == null) {
			amu = new ActivityManagerUtil();
		}
		if (activityList == null) {
			activityList = new ArrayList<Activity>();
		}
		return amu;
	}

	
	public void addToList(Activity paramActivity) {
		if (amu != null) {
			activityList.add(paramActivity);
		}
	}

	
	public void finishAllActivity() {
		Iterator<Activity> localIterator;
		if (amu != null) {
			localIterator = activityList.iterator();
			if (!localIterator.hasNext()) {
				return;
			}
			Activity localActivity = (Activity) localIterator.next();
			if (localActivity != null) {
				localActivity.finish();
			}
		}
	}
}
