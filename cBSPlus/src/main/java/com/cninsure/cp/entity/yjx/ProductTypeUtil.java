package com.cninsure.cp.entity.yjx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;

public class ProductTypeUtil {

	private static List<NameValuePair> productType;
	//险种大类
	@SuppressLint("UseSparseArrays")
	public static List<NameValuePair> caseSmallProductType(){
		productType=new ArrayList<NameValuePair>();
		productType.add(new BasicNameValuePair("0", "--请选择--"));
		productType.add(new BasicNameValuePair("20001", "医健险初勘"));
		productType.add(new BasicNameValuePair("20002", "医健险全案本地"));
		productType.add(new BasicNameValuePair("20003", "医健险全案异地"));
		productType.add(new BasicNameValuePair("20004", "医健险其他"));
		return  productType;
	}
	
	public static String getTypeIdByName(String typeName){
		for (int i = 0; i < productType.size(); i++) {
			if (typeName.equals(productType.get(i).getValue())) {
				return productType.get(i).getName();
			}
		}
		return "";
	}
	
	public static String getNameByTypeId(String typeid){
		for (int i = 0; i < productType.size(); i++) {
			if (typeid.equals(productType.get(i).getName())) {
				return productType.get(i).getValue();
			}
		}
		return "";
	}

	////险种细类
	public static List<NameValuePair> getBussType(String codestr){
		List<NameValuePair> bussType=new ArrayList<NameValuePair>();
		int code = Integer.parseInt(codestr);
		switch(code){
		case 20001:
			bussType.add(new BasicNameValuePair("0", "--请选择--"));
			
			   bussType.add(new BasicNameValuePair("1", "人伤院内首勘"));//, insuranceIds: ",2002,2003,",},
               bussType.add(new BasicNameValuePair("2", "人伤院内复勘"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("3", "人伤院外复勘"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("4", "院外事故相关调查"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("5", "资料收集（人伤案件）"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("6", "住院病历调阅"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("7", "住院病历排查"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("8", "门诊病史调阅"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("9", "门诊病史排查"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("10", "社保调查"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("11", "走访调查"));//, insuranceIds: ",2004,2005,2006,"},
               bussType.add(new BasicNameValuePair("12", "契约调查"));//, insuranceIds: ",2004,"},
               bussType.add(new BasicNameValuePair("13", "陪同体检"));//, insuranceIds: ",2004,"}

			break;
		case 20002:
			bussType.add(new BasicNameValuePair("0", "--请选择--"));
			
			 bussType.add(new BasicNameValuePair("15", "伤亡调查"));//, insuranceIds: ",2002,2003,"},
             bussType.add(new BasicNameValuePair("16", "案件调解"));//, insuranceIds: ",2003,"},
             bussType.add(new BasicNameValuePair("17", "案件代诉"));//, insuranceIds: ",2002,2003,"},
             bussType.add(new BasicNameValuePair("18", "意外真实性调查"));//, insuranceIds: ",2002,"},
             bussType.add(new BasicNameValuePair("19", "重疾身故疑难案件调查"));//, insuranceIds: ",2004,2005,2006,"}

			break;
		case 20003:
			bussType.add(new BasicNameValuePair("0", "--请选择--"));
			  bussType.add(new BasicNameValuePair("15", "伤亡调查"));//, insuranceIds: ",2002,2003,"},
              bussType.add(new BasicNameValuePair("16", "案件调解"));//, insuranceIds: ",2003,"},
              bussType.add(new BasicNameValuePair("17", "案件代诉"));//, insuranceIds: ",2002,2003,"},
              bussType.add(new BasicNameValuePair("18", "意外真实性调查"));//, insuranceIds: ",2002,"},
              bussType.add(new BasicNameValuePair("19", "重疾身故疑难案件调查"));//, insuranceIds: ",2004,2005,2006,"}

			break;
		case 20004:
			bussType.add(new BasicNameValuePair("0", "--请选择--"));
			 bussType.add(new BasicNameValuePair("20", "疑案调查"));//, insuranceIds: ",*,"},
             bussType.add(new BasicNameValuePair("21", "调解代诉"));//, insuranceIds: ",*,"},
             bussType.add(new BasicNameValuePair("22", "陪同鉴定"));//, insuranceIds: ",*,"},
             bussType.add(new BasicNameValuePair("23", "未决管理"));//, insuranceIds: ",*,"},

			break;

		default:
			break;
		}
		return bussType;
	}
	
	public static String getBussTypeId(String typeId,String SmallProductName){
		List<NameValuePair> smallTypes = getBussType(typeId);
		for (int i = 0; i < smallTypes.size(); i++) {
			if (SmallProductName.equals(smallTypes.get(i).getValue())) {
				return smallTypes.get(i).getName();
			}
		}
		return "";
	}
	
	public static List<String> MapToList(List<NameValuePair> bussType){
		List<String> lists=new ArrayList<String>();
		for(NameValuePair integer:bussType){
			lists.add(integer.getValue());
		}
		return lists;
	}

	/**获取产品细类在spinner中显示的位置*/
	public static int getPostionBycaseSmallProductType(Integer id){
		List<NameValuePair>  producttypeMap = caseSmallProductType();
		for (int i = 0; i < producttypeMap.size(); i++) {
			if ((id+"").equals(producttypeMap.get(i).getName())) {
				return i;
			}
		}
		return 0;
	}
	
	/**获取业务品种在spinner中显示的位置
	 * @param bussTypeId */
	public static int getPostionBygetBussType(Integer productId, Integer bussTypeId){
		List<NameValuePair>  producttypeMap = getBussType(productId+"");
		for (int i = 0; i < producttypeMap.size(); i++) {
			if ((bussTypeId+"").equals(producttypeMap.get(i).getName())) {
				return i;
			}
		}
		return 0;
	}

}
