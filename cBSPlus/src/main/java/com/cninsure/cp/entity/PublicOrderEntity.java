package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import android.widget.TextView;

public class PublicOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*********公共**************/
	public String createDate;
	public Long id;
	/** 案件状态3 作业待处理 4 作业处理中 */
	public int status;
	/** “FC” 代表是非车险，“YJX” 代表是医健险，其他是车险订单 **/
	public String caseTypeAPP="";
	
	
	/*********非车**************/
	
	/** 案件编号 */
	public String caseNo;
	/** 案件名称 */
	public String caseName;
	/** 委托人名称 */
	public String deputePer;
	/** 险种大类1 财险。2 水险 */
	public int riskType;
	/** 报案号 */
	public String insurerCaseNo;
	/** 归属机构 */
	public String gsOrg;
	/** 险种分类**/
	public String feicheBaoxianType;
	/**出险单位联系人*/
	public String cxUintLink;
	/**出险单位联系人电话*/
	public String lxPhone;

	
	/*********车险**************/

	/**险种**/
	public int caseTypeId;
	/**险种名称**/
	public String caseTypeName;
	/**报案号**/
	public String baoanNo;
	/**业务品种id**/
    public Integer bussTypeId;
	/**业务品种名称**/
    public String bussTypeName;
    
    public String caseUid;
    /**接报案编号**/
    public String caseBaoanUid;
    /**调度人名称**/
    public String dispatcherName;
    /**调度人Uid**/
    public String dispatcherUid;
    /**委托人id**/
    public long entrusterId;
    /**委托人名称**/
    public String entrusterName;
    /**公估师名称**/
    public String ggsName;
    /**公估师UID**/
    public String ggsUid;
    /**标地方车牌**/
    public String licensePlateBiaoDi;
    /**订单标识UID**/
    public String uid;
    /**跟新时间**/
    public String updateDate;
    /**出险时间**/
    public String riskDate;
	/**出险单位联系人*/
	public String baoanPersonName;
	/**出险单位联系人电话*/
	public String baoanPersonPhone;
	/**出险经过（委托信息）*/
	public String caseLifecycle;
	/**超时时长*/
	public long timeOutHours;
	/**订单调度时间**/
	public String dispatchDate;
/**处理时长 公估师接单开始计算。*/
	public String detailTime;//处理时长 公估师接单开始计算。
	/**显示超时的textView**/
	public TextView outTimView;
	/**出险地点**/
	public String caseLocation="无";
	/**经纬度*/
	public float caseLocationLatitude;//":"22.581202",
	/**经纬度*/
	public float caseLocationLongitude;//":"114.108214",
	public Timer timer;


	public String areaNo; //作业区域编码
	public String area;  //作业区域名称
	public String province;  //作业区域省份
	public String caseProvince;  //出险省份
	public String city;  //城市


	/*********医健险**************/

    /**调度人电话**/
    public String dispatcherTel;
}
