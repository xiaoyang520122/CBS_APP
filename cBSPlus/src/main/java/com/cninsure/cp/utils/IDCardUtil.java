package com.cninsure.cp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cninsure.cp.AppApplication;

import android.text.TextUtils;

/** 判断身份证号码是否正确 **/
public class IDCardUtil {

	public static boolean isIDNumber(String IDNumber) {
		if (TextUtils.isEmpty(IDNumber)) {
			return false;
		}
		// 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
		String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|"
				+ "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
		// 假设18位身份证号码:41000119910101123X 410001 19910101 123X
		// ^开头
		// [1-9] 第一位1-9中的一个 4
		// \\d{5} 五位数字 10001（前六位省市县地区）
		// (18|19|20) 19（现阶段可能取值范围18xx-20xx年）
		// \\d{2} 91（年份）
		// ((0[1-9])|(10|11|12)) 01（月份）
		// (([0-2][1-9])|10|20|30|31)01（日期）
		// \\d{3} 三位数字 123（第十七位奇数代表男，偶数代表女）
		// [0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
		// $结尾

		// 假设15位身份证号码:410001910101123 410001 910101 123
		// ^开头
		// [1-9] 第一位1-9中的一个 4
		// \\d{5} 五位数字 10001（前六位省市县地区）
		// \\d{2} 91（年份）
		// ((0[1-9])|(10|11|12)) 01（月份）
		// (([0-2][1-9])|10|20|30|31)01（日期）
		// \\d{3} 三位数字 123（第十五位奇数代表男，偶数代表女），15位身份证不含X
		// $结尾

		boolean matches = IDNumber.matches(regularExpression);

		// 判断第18位校验值
		if (matches) {

			if (IDNumber.length() == 18) {
				try {
					char[] charArray = IDNumber.toCharArray();
					// 前十七位加权因子
					int[] idCardWi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
					// 这是除以11后，可能产生的11位余数对应的验证码
					String[] idCardY = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
					int sum = 0;
					for (int i = 0; i < idCardWi.length; i++) {
						int current = Integer.parseInt(String.valueOf(charArray[i]));
						int count = current * idCardWi[i];
						sum += count;
					}
					char idCardLast = charArray[17];
					int idCardMod = sum % 11;
					if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
						return true;
					} else {
						System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() + "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
						return false;
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("异常:" + IDNumber);
					return false;
				}
			}

		}
		return matches;
	}

	/** 根据身份照号码获取性别 */
	public static String getSex(String IDNumber) {
		try {
			if (IDNumber.length() == 18) {
				return (String) getCarInfo(IDNumber).get("sex");
			}else if (IDNumber.length() == 15) {
				return (String) getCarInfo15W(IDNumber).get("sex");
			}else {
				ToastUtil.showToastLong(AppApplication.mInstance, "身份证号码错误！！");
				return "";
			}
		} catch (Exception e) {
			ToastUtil.showToastLong(AppApplication.mInstance, "身份证号码有误，无法判断对应人员性别！！！");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 根据身份证的号码算出当前身份证持有者的性别和年龄 18位身份证
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getCarInfo(String CardCode) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String year = CardCode.substring(6).substring(0, 4);// 得到年份
		String yue = CardCode.substring(10).substring(0, 2);// 得到月份
		// String day=CardCode.substring(12).substring(0,2);//得到日
		String sex;
		if (Integer.parseInt(CardCode.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别
			sex = "女";
		} else {
			sex = "男";
		}
		Date date = new Date();// 得到当前的系统时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String fyear = format.format(date).substring(0, 4);// 当前年份
		String fyue = format.format(date).substring(5, 7);// 月份
		// String fday=format.format(date).substring(8,10);
		int age = 0;
		if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生
			age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
		} else {// 当前用户还没过生
			age = Integer.parseInt(fyear) - Integer.parseInt(year);
		}
		map.put("sex", sex);
		map.put("age", age);
		return map;
	}

	/**
	 * 15位身份证的验证
	 * 
	 * @param
	 * @throws Exception
	 */
	public static Map<String, Object> getCarInfo15W(String card) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String uyear = "19" + card.substring(6, 8);// 年份
		String uyue = card.substring(8, 10);// 月份
		// String uday=card.substring(10, 12);//日
		String usex = card.substring(14, 15);// 用户的性别
		String sex;
		if (Integer.parseInt(usex) % 2 == 0) {
			sex = "女";
		} else {
			sex = "男";
		}
		Date date = new Date();// 得到当前的系统时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String fyear = format.format(date).substring(0, 4);// 当前年份
		String fyue = format.format(date).substring(5, 7);// 月份
		// String fday=format.format(date).substring(8,10);
		int age = 0;
		if (Integer.parseInt(uyue) <= Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生
			age = Integer.parseInt(fyear) - Integer.parseInt(uyear) + 1;
		} else {// 当前用户还没过生
			age = Integer.parseInt(fyear) - Integer.parseInt(uyear);
		}
		map.put("sex", sex);
		map.put("age", age);
		return map;
	}

}
