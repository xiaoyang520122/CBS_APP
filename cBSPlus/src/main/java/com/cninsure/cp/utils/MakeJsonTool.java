package com.cninsure.cp.utils;

import java.util.List;

public class MakeJsonTool {

	public static String formStringList(List<String> parametes) {
		String resultValue = "{";
		if (parametes == null) {
			return resultValue + "}";
		}
		for (int i = 0; i < parametes.size(); i++) {
			resultValue = resultValue + "\"" + parametes.get(i) + "\"";
			if (i % 2 != 1) {
				resultValue = resultValue + ":";
			} else if (i < (parametes.size() - 1)) {
				resultValue = resultValue + ",";
			}
		}
		resultValue = resultValue + "}";
		return resultValue;
	}

}
