package com.cninsure.cp.navi;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;

public class BaiDuErrorUtil {

	
	public static String getTransitRouteResultErrorString(ERRORNO type){
		if (type==SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
			return "检索词有岐义！";
		}else if (type==SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return "检索地址有岐义！";
		}else if (type==SearchResult.ERRORNO.NOT_SUPPORT_BUS) {
			return "该城市不支持公交搜索！";
		}else if (type==SearchResult.ERRORNO.NOT_SUPPORT_BUS_2CITY) {
			return "不支持跨城市公交！";
		}else if (type==SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return "没有找到检索结果！";
		}else if (type==SearchResult.ERRORNO.ST_EN_TOO_NEAR) {
			return "起终点太近！";
		}
		return "";
		
	}
}
