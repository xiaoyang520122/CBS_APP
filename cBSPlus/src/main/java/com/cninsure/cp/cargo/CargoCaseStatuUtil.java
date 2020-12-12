package com.cninsure.cp.cargo;

public class CargoCaseStatuUtil {

    /**
     * public static final int ENTRUST = 0;//委托
     * 	public static final int SUBMIT = 1;//提交
     * 	public static final int GGS_ACCEPT = 2;//公估师接收
     * 	public static final int GGS_REFUSE = 3;//公估师拒绝
     * 	public static final int DISPATCH = 4;//调度查勘员
     * 	public static final int SURVEY_ACCEPT = 5;//查勘员接收
     * 	public static final int SURVEY_REFUSE = 6;//查勘员拒绝
     * 	public static final int WORK_SUBMIT = 7;//作业提交
     * 	public static final int AUDIT_CLAIMED = 8;//审核认领
     * 	public static final int AUDIT_PASS = 9;//审核通过
     * 	public static final int AUDIT_ADOPT = 10;//审核退回
     * 	public static final int FIX_DUTY = 11;//定责
     * 	public static final int LOSS = 12;//定损
     * 	public static final int ADJUSTMENT = 13;//理算
     * 	public static final int WAIT_FINISH = 14;//待结案（理算完成）
     * 	public static final int FINISH = 15;//结案
     */
    public static String getStatuString(int status){
        switch (status) {
            case 0:return "委托";
            case 1:return "提交";
            case 2:return "公估师接收";
            case 3:return "公估师拒绝";
            case 4:return "待接受";
            case 5:return "作业中";
            case 6:return "拒绝";
            case 7:return "作业提交";
            case 8:return "审核认领";
            case 9: return "审核通过";
            case 10: return "审核退回";
            case 11:return "定责";
            case 12:return "定损";
            case 13:return "理算";
            case 14:return "待结案";
            case 15:return "结案";

            default:
                return "状态未知";
        }

    }
}
