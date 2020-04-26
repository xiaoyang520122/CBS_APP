package com.cninsure.cp.entity.yjx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;




public class InsuranceTypeUtil{

	private static List<NameValuePair> insuranceType;
	//险种大类
	@SuppressLint("UseSparseArrays")
	public static List<NameValuePair> getinsuranceTypeCollection(){
		insuranceType=new ArrayList<NameValuePair>();
		insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
		insuranceType.add(new BasicNameValuePair("2001", "车险人伤"));
		insuranceType.add(new BasicNameValuePair("2002", "意外险"));
		insuranceType.add(new BasicNameValuePair("2003", "责任险（含车险）"));
		insuranceType.add(new BasicNameValuePair("2004", "寿险"));
		insuranceType.add(new BasicNameValuePair("2005", "重疾险"));
		insuranceType.add(new BasicNameValuePair("2006", "健康险"));
		return  insuranceType;
	}
	
	public static String getTypeIdByName(String typeName){
		for (int i = 0; i < insuranceType.size(); i++) {
			if (typeName.equals(insuranceType.get(i).getValue())) {
				return insuranceType.get(i).getName();
			}
		}
		return "";
	}
	
	public static String getNameByTypeId(String typeid){
		for (int i = 0; i < insuranceType.size(); i++) {
			if (typeid.equals(insuranceType.get(i).getName())) {
				return insuranceType.get(i).getValue();
			}
		}
		return "";
	}

	////险种细类
	public static List<NameValuePair> getinsuranceSmallTypeCollection(String codestr){
		List<NameValuePair> insuranceType=new ArrayList<NameValuePair>();
		int code = Integer.parseInt(codestr);
		switch(code){
		case 2001:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20011", "机动车辆第三者责任险"));
			insuranceType.add(new BasicNameValuePair("20012", "车上人员责任险"));
			break;
		case 2002:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20021", "团体人身意外伤害保险"));
			insuranceType.add(new BasicNameValuePair("20022", "学生团体平安保险"));
			insuranceType.add(new BasicNameValuePair("20023", "个人意外伤害保险"));
			insuranceType.add(new BasicNameValuePair("20024", "旅游意外伤害保险"));
			insuranceType.add(new BasicNameValuePair("20025", "建筑工程团体人身意外伤害保险"));
			break;
		case 2003:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20031", "机动车辆交通事故强制保险"));
			insuranceType.add(new BasicNameValuePair("20032", "机动车辆第三者责任险"));
			insuranceType.add(new BasicNameValuePair("20033", "车上人员责任险"));
			insuranceType.add(new BasicNameValuePair("20034", "雇主责任保险"));
			insuranceType.add(new BasicNameValuePair("20035", "公众责任保险"));
			insuranceType.add(new BasicNameValuePair("20036", "道路承运人责任保险"));
			insuranceType.add(new BasicNameValuePair("20037", "产品责任保险"));
			insuranceType.add(new BasicNameValuePair("20038", "校园方责任保险"));
			insuranceType.add(new BasicNameValuePair("20039", "旅行社责任保险"));
			insuranceType.add(new BasicNameValuePair("200310", "医疗责任保险"));
			insuranceType.add(new BasicNameValuePair("200311", "电梯责任保险"));
			break;
		case 2004:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20041", "终身寿险"));
			insuranceType.add(new BasicNameValuePair("20042", "定期寿险"));
			insuranceType.add(new BasicNameValuePair("20043", "年金保险"));
			break;
		case 2005:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20051", "短期重疾险"));
			insuranceType.add(new BasicNameValuePair("20062", "长期重疾险"));
			break;
		case 2006:
			insuranceType.add(new BasicNameValuePair("0", "--请选择--"));
			insuranceType.add(new BasicNameValuePair("20061", "个人医疗保险（百万住院医疗）"));
			insuranceType.add(new BasicNameValuePair("20062", "团体医疗保险（补充医疗）"));
			insuranceType.add(new BasicNameValuePair("20063", "失能保险"));
			insuranceType.add(new BasicNameValuePair("20064", "护理保险"));
			break;

		default:
			break;
		}
		return insuranceType;
	}
	
	public static String getSmallTypeId(String typeId,String smallTypeName){
		List<NameValuePair> smallTypes = getinsuranceSmallTypeCollection(typeId);
		for (int i = 0; i < smallTypes.size(); i++) {
			if (smallTypeName.equals(smallTypes.get(i).getValue())) {
				return smallTypes.get(i).getName();
			}
		}
		return "";
	}
	
	public static List<String> MapToList(List<NameValuePair> insuranceType){
		List<String> lists=new ArrayList<String>();
		for(NameValuePair integer:insuranceType){
			lists.add(integer.getValue());
		}
		return lists;
	}
	
	/**获取险种类型在集合中显示的位置*/
	public static int getPostionByInsuranceType(String id){
		List<NameValuePair>  insuretypeMap = getinsuranceTypeCollection();
		for (int i = 0; i < insuretypeMap.size(); i++) {
			if (id.equals(insuretypeMap.get(i).getName())) {
				return i;
			}
		}
		return 0;
	}
	
	/**获取险种细类在集合中显示的位置
	 * @param bussTypeId */
	public static int getPostionBySmallInsuretype(String insureId, String smallInsureId){
		List<NameValuePair>  smallInsuretypeMap = getinsuranceSmallTypeCollection(insureId+"");
		for (int i = 0; i < smallInsuretypeMap.size(); i++) {
			String insuranceSmallTypeId = smallInsuretypeMap.get(i).getName();
			if (smallInsureId.equals(insuranceSmallTypeId)) {
				return i;
			}
		}
		return 0;
	}

}
