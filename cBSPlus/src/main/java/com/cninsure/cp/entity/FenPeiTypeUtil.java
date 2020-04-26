package com.cninsure.cp.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FenPeiTypeUtil {
	
	private static List<String> caseTypes;
	/*<option value="1">财产险水灾</option>
			<option value="2">财产险</option>
			<option value="3">船舶险</option>
			<option value="4">货运险</option>
			<option value="5">机损及利损险</option>
			<option value="6">工程险</option>
			<option value="7">工时分配</option>*/
	
	public static List<String> getCaseTypes(){
		caseTypes=new ArrayList<String>();
		caseTypes.add( "财产险水灾");
		caseTypes.add("财产险");
		caseTypes.add("船舶险");
		caseTypes.add("货运险");
		caseTypes.add("机损及利损险");
		caseTypes.add("工程险");
		caseTypes.add("工时分配");
		return caseTypes;
	}

}
