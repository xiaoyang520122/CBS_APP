package com.cninsure.cp.entity.dispersive;

public class DispersiveDispatchStatus {

    /**
     /** 暂存  0;
     /** 已调度（待接受）1;
     /** 公估师接受（作业中） 2;
     /** 作业提交，审核中  3;
     /** 审核完成  4;
     /** 审核驳回  5;
     /** 公估师拒绝  6;
     /** 拒绝再改派  7;
     /** 公估师取消  8;
     /** 撤单（取消）再改派  9;
     /** 公估师超时  10;
     /** 公估师到达现场  11;
     /** 超时再改派  12;
     */
    public static String getStatuString(int status){
        switch (status) {
            case 0:
                return "调度暂存";

            case 1:
                return "待接单";

            case 2:
                return "待作业";

            case 3:
                return "提交待审核";

            case 4:
                return "审核通过";

            case 5:
                return "审核驳回";

            case 6:
                return "公估师拒绝";

            case 7:
                return "拒绝再改派";

            case 8:
                return "公估师取消";

            case 9:
                return "取消再改派";

            case 10:
                return "公估师超时";

            case 11:
                return "已到达现场";

            case 12:
                return "超时再改派";

            default:
                return "状态未知";
        }

    }
}
