package com.cninsure.cp.entity.yjx;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

public class SurveyType {

	private static List<String> surveyTypeValues;

	public static List<String> getType() {
		surveyTypeValues = new ArrayList<String>();
		surveyTypeValues.add("");
		surveyTypeValues.add("面访");
		surveyTypeValues.add("走访");
		surveyTypeValues.add("调阅");
		surveyTypeValues.add("验真");
		surveyTypeValues.add("排查");
		return surveyTypeValues;
	}

	/**通过传递的调查类型字符串返回其在集合中的位置，也就是适配器中item的位置*/
	public static int getPostion(String type) {
		if (TextUtils.isEmpty(type)) { //传过来的调查类型为空，返回0
			return 0;
		}
		getType();
		for (int i = 0; i < surveyTypeValues.size(); i++) {
			if (type.equals(surveyTypeValues.get(i))) {
				return i;
			}
		}
		return 0; //未匹配到合适的调查类型，返回0
	}

}
