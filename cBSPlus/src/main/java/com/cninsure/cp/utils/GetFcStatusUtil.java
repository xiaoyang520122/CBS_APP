package com.cninsure.cp.utils;

public class GetFcStatusUtil {
	
	public static String  getstatus(int statuValue){
		String statusString="";
		switch (statuValue) {
		case 0:
			statusString="录入保存";
			break;

		case 1:
			statusString="案件提交";
			break;

		case 2:
			statusString="立案处理保存";
			break;

		case 3:
			statusString="立案处理提交";
			break;

		case 4:
			statusString="作业环节";
			break;

		case 5:
			statusString="作业处理提交";
			break;

		case 6:
			statusString="报告审核";
			break;

		case 7:
			statusString="出单";
			break;

		case 8:
			statusString="出单";
			break;

		case 9:
			statusString="出单审核";
			break;

		case 10:
			statusString="开票";
			break;


		case -6:
			statusString="销案";
			break;
		case -100:
			statusString="暂存";
			break;

		default:
			break;
		}
		return statusString;
	}

}
