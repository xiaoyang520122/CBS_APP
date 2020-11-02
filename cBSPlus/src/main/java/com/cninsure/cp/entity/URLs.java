package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.Date;

import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.MD5Test;

public class URLs implements Serializable {

	private static final long serialVersionUID = 1L;

	//测试环境测试账号：test_ct001  密码：123456
	/***
	生产环境：http://sysweb.cnsurvey.cn:8010
	对应的后台：http://sys.cnsurvey.cn:8008

	测试环境：http://http://sysweb.cnsurvey.cn:8085//后改为端口8085
	对应的后台：http://sys.cnsurvey.cn:8007 || 193.112.173.125:8083
	 */
	/**===主接口(登录架构等)===**/
//	private final static String IPHOME="http://sys.cnsurvey.cn:8008/cninsure-pz-sys-ws";//车险外网
	private final static String IPHOME="http://193.112.173.125:8084/cninsure-pz-sys-ws";//车险外网测试 对应：http://sysweb.cnsurvey.cn:8085
//	private final static String IPHOME="http://10.80.13.53:8081/cninsure-pz-sys-ws";//车险外网测试 -冬阳
//	private final static String IPHOME="http://10.80.8.16:8081/cninsure-pz-sys-ws"; //车险外网测试 -邹工

	
//	private final static String FPIPHOME="http://202.170.139.223:8037/cninsure-plus-sys-ws";
	private final static String FPIPHOME="http://202.170.139.223:8036/cninsure-plus-sys-ws";
	
	/**===非车主接口===**/
	private final static String FCIPHOME="http://118.89.40.53:8080/new_cbs/app/interface";//非车测试http://cbs.cnsurvey.cn
//	private final static String FCIPHOME="http://cbs.cnsurvey.cn/app/interface";//非车正式
//	private final static String FCIPHOME="http://10.80.8.1:8080/new_cbs/app/interface";//非车测试 
	
	/**===车险作业界面主接口===**/
	/**订单作业界面连接 方法：goMobileTaskHandle(data) 参数data = {platName:"",userName:"",password:"",orderUid:"",taskType:'' }**/
//	public final static String WORK_SPACE="http://10.80.13.110:8888/CBSPlusApp/#/mobile";//车险作业界面内网
//	public final static String WORK_SPACE="http://10.80.13.110:8888/CBSPlusWeb/#/mobile";//车险作业界面内网
//	public final static String WORK_SPACE="http://sysweb.cnsurvey.cn:8010/#/mobile";//车险作业界面外网
//	public final static String WORK_SPACE="http://sysweb.cnsurvey.cn:8085/#/mobile";//车险作业界面外网测试 
	public final static String WORK_SPACE="http://sysweb.cnsurvey.cn:8085/CBSPlusApp/#/mobile";//车险作业界面外网测试  
//	public final static String WORK_SPACE="http://testweb.cnsurvey.cn:8010/#/mobile";//车险作业界面外网测试
//	public final static String WORK_SPACE="http://10.80.13.32:2018/angulr/angular/#/access/signin";
	
	/**登录地址**/
	private final static String LOGIN_BY_PASS="/authc/login";
	/**上传CID**/
	private final static String UP_CID="/appuser/upload/cid";
	/**上传经纬度*/
	private final static String UP_LOCATION="/users/app/saveGps";
	/**查看自己调度的订单GET**/
	private final static String GET_SELFORDER="/dispatchs/list/selfOrder";
	/**查看自己指定状态的调度订单GET**/
	private final static String GET_STATU_SELFORDER="/dispatchs/list/selfAll";
	/**接收订单调度post**/
	private final static String RECEIVE_ORDER="/cx/order/accept";
	/**取消订单调度post**/
	private final static String CANCEL_ORDER="/cx/order/refuse";
	
	/**统计用户作业订单各个类型案件量**/
	private final static String GET_STATUS_CONUT="/dispatchs/selfgroup/status";
	/**车险业务-查看案件的详细信息（所有的信息） GET**/
	private final static String GET_ORDER_INFO="/baoans/detail/full";
	/**获取用户详细信息 GET**/
	private final static String GET_USER_INFO="/users/detail";
	/**作业信息-文件上传-上传图片 POST**/
	private final static String UPLOAD_WORK_PHOTO="/images/wrok/upload?client=android&timestamp="+new Date().getTime()+"&digest="+MD5Test.GetMD5Code("nomessagedigest");
	/**医健险-文件上传 POST**/
	public final static String UPLOAD_FILE_PHOTO=IPHOME+"/uploads/file/upload?client=android&timestamp="+new Date().getTime()+"&digest="+MD5Test.GetMD5Code("nomessagedigest");
	/**获取上传图片类型get**/
	private final static String GET_PHOTO_TYPE="http://dict.cnsurvey.cn:7077/cninsure-disc-ws/dicts/list";
	/**获取上传图片类型get**/
	private final static String GET_WORK_PHOTO=IPHOME+"/images/wrok/list";

	/**车险作业界面头部回显**/
	private final static String GET_WORK_MESSAGE=IPHOME+"/dispatchs/work/message";
	/**获取版本信息GET ?userId=userId**/
	private final static String GET_VERSION_INFO=IPHOME+"/appclient/version/latest";
	/**修改个人用户名密码 POST**/
	private final static String CHANG_PASS=IPHOME+"/users/modify/selfpassword";
	/**修改个人手机号 POST**/
	private final static String CHANG_PHONE=IPHOME+"/users/modify/selfaccuont";
	/**查勘回显 get**/
	private final static String GET_WORK_MESSAGES=IPHOME+"/works";
	/**查勘作业保存 post**/
	private final static String SAVE_WORK_INFO=IPHOME+"/works";
	/**查勘作业提交审核 post**/
	private final static String SUBMIT_WORK=IPHOME+"/dispatchs/app/finishDispath";

	/**获取非车任务列表 post**/
	public final static String FC_GET_WORK_CASE_LIST=FCIPHOME+"/case/listWorkCase";
	/**获取非作业所需字典库数据 post**/
	public final static String FC_GET_DICT_LIST=FCIPHOME+"/dict/getList";
	/**获取非作业保存回显信息 post**/
	public final static String FC_GET_WORK_INFO=FCIPHOME+"/case/viewWorkCase";
	/**上传保存非车作业信息 post**/
	public final static String FC_SAVE_WORK_INFO=FCIPHOME+"/case/saveWorkCase";
	/**扫码登录 post**/
	public final static String SCANNER_LOGIN=IPHOME+"/auxiliarys/app/loginsacn";
	/**获取非车上次影像信息 post**/
	public final static String DOWNLOAD_WORK_FILE=FCIPHOME+"/case/upload";
	/**作业暂存 post**/
	public final static String SAVE_WORK_TEMP=IPHOME+"/temporarys";
	/**非车上传影像信息 post**/
	public final static String UPLOAD_WORK_FILE=FCIPHOME+"/case/uploadFile";
	/**获取营业部信息 GET userId用户信息；organizationId查询营业部信息的父级ID,传3请求所有的影营业部信息**/
	public final static String DOWNLOAD_DEPT_YYB=IPHOME+"/organizations/yyb/list";
	/**非车查询作业完成案件列表接口 POST**/
	public final static String GET_FC_WORKED_LIST=FCIPHOME+"/case/listWorkedCase";
	/**获取非车指定案件状态的列表数据  filed3 传递多个状态 POST**/
	public final static String GET_FC_STATUS_LIST=FCIPHOME+"/case/listCase";
	/**车险 作业操作-留言-提交留言 POST **/
	public final static String SAVE_LEAVING_MESSAGE=IPHOME+"/works/message/do";
	/**车险 作业操作-留言-查看回显 GET **/
	public final static String GET_LEAVING_MESSAGE=IPHOME+"/works/message/show";
	/**车险 作业操作-留言-修改留言 POST **/
	public final static String CHANG_LEAVING_MESSAGE=IPHOME+"/works/message/modify";
	/**非车 公估师薪酬明细汇总接口 POST **/
	public final static String GET_FC_GGS_SCORE=FCIPHOME+"/user/getTotalGgsIncome"; 
	/**车险 公估师薪酬明细汇总接口 **/
	public final static String GET_CX_GGS_SCORE=FPIPHOME+"/allomanages/sum/detail"; 
	/**获取委托人，"data":{"name":"安诚"}为模糊查询，"data":null或者"data":{"name":"%"} 为查询所有  post请求**/
	public final static String GET_WT_REN=FCIPHOME+"/dict/getInsureList"; 
	/**获取公估师列表**/
	public final static String GET_GGS_LIST=IPHOME+"/users/list"; 
	/**获取订单审核信息 userId=……&orderUid=……    GET**/
	public final static String GET_ORDER_STATUS=IPHOME+"/audits/list/audithistory";  
	/**提交非车接报案信息 post**/
	public final static String SUBMIT_FC_NEW_CASE=FCIPHOME+"/case/saveCase"; 
	/**大灾选项列表**/
	public final static String GET_FC_DZ_DICT=FCIPHOME+"/dict/getImmenseOption"; 
	/**车险 上传OCR图片,上传成功后返回图片路径后缀 例如："picture-20180310151556-69210-E2638.jpg" ，访问是需要加上登录时获取的头部分**/
	public final static String UP_OCR_PHOTO=IPHOME+"/uploads/picture/upload?client=android&timestamp="+new Date().getTime()+"&digest="+MD5Test.GetMD5Code("nomessagedigest");

	/**案件自调度 POST  请求参数：userId用户id，bussTypeId作业类型id，bussTypeName作业类型名称，fromOrderUid订单uid**/
	public final static String ADD_NEW_ORDER_SELF=IPHOME+"/dispatchs/selfdo";
	/**APP下载路径
	 * http://sys.cnsurvey.cn:8008/file_app/pz_plus/android/product/zd/ZDCBS.apk //正达
	   http://sys.cnsurvey.cn:8008/file_app/pz_plus/android/product/cninsure/CBSPlus.apk //公估正式环境
	   http://sys.cnsurvey.cn:8008/file_app/pz_plus/android/test/cninsure/CBSPlus.apk //公估测试环境**/
	public final static String APP_DOWNLOAD_URL="http://sys.cnsurvey.cn:8008/file_app/pz_plus/android/product/cninsure/CBSPlus.apk";
	/**请求定损维修数据**/
	public final static String DOWN_PARTS_INFO=IPHOME+"/qxwdmess/time/price";
	/**获取TOKEN值   返回数据格式"20180425101357751-42255-A8682"**/
	public final static String GET_TOKEN=IPHOME+"/apis/token/get";
	/**保存银行卡信息**/
	public final static String SAVE_BANK_CARD=IPHOME+"/users/modify/payee";
	/**非车计算公估师贡献比例**/
	public final static String CALCULATION=FCIPHOME+"/case/calculation"; 
	/**销案案件查询：http://127.0.0.1:8080/new_cbs/app/interface/case/xaApply
	**请求参数：{"userToken":"2432","requestData":{"id":67391,"filed3":"销案申请"}}
	**返回参数：{"code":"0","message":"成功","exception":"","data":"销案申请成功"} IP改成53 **/
	public final static String 	CANCEL_CASE=FCIPHOME+"/case/xaApply"; 
	/**获取非车接报案回显信息 post**/
	public final static String FC_GET_CASE_INFO=FCIPHOME+"/case/view";
	
	/**获取医健险公估师任务列表**/
	public final static String YJX_GGS_ORDER_LIST = IPHOME +"/yjx/dispatch/list";
	/**修改调度状态 ?id=1&status=1*/
	public final static String YJX_CHANGE_STATUS = IPHOME +"/yjx/dispatch/updateStatus";
	/**作业详情?dispatchUid=YJX-DISPATCH-20190305163333-EB193 */
	public final static String GET_WORK_INFO = IPHOME +"/yjx/work/view";
	/**接报案详情?uid=YJX-B-20190214173328-3BFDE */
	public final static String GET_BAOAN_INFO = IPHOME +"/yjx/baoan/view";
	/**单个调度详情-GET ?id=7  */
	public final static String GET_DISPATCH_INFO = IPHOME +"/yjx/dispatch/viewDetail";
	/**医健险作业保存POST*/
	public final static String POST_YJX_WORK_SAVE = IPHOME +"/yjx/work/save";
	/**接报案保存-作业处专用POST*/
	public final static String POST_YJX_BAOAN_UPDATE = IPHOME +"/yjx/baoan/update";
	/**E公估委托人获取*/
	public final static String GET_E_WT_REN = IPHOME+"/entrusters/list";
	/**E公估省市信息获取，parentId传对0获取省份信息*/
	private final static String GET__PROBINCE_INFO = IPHOME+"/regions/levelSecond";
	/**E公估市县信息获取，parentId传对应省id获取下面市县区列表*/
	private final static String GET_CITY_BY_PROBINCE_ID = IPHOME+"/regions/levelThird";
	/**E公估区信息获取，parentId传对应市id获取下面区列表*/
	private final static String GET_QREA_BY_CITY_ID = IPHOME+"/regions/levelFouth"; 
	/**E公估医健险接报案新增、修改*/
	public final static String POST_YJX_BAOAN_SAVE = IPHOME+"/yjx/baoan/save"; 
	/**E公估医健险接报案列表*/
	public final static String GET_YJX_BAOAN_LIST = IPHOME+"/yjx/baoan/list";
	/**E公估医健险接报案删除：POST*/
	public final static String DELETE_YJX_BAOAN_CASE = IPHOME+"/yjx/baoan/delete";
	/**E公估医健险接报案退回：POST*/
	public final static String BACK_YJX_BAOAN_CASE = IPHOME+"/yjx/baoan/updateStatus";
	/**E公估医健险接报案对应调度列表：GET*/
	public final static String YJX_BAOAN_CASE_DISPATCH_LIST = IPHOME+"/yjx/dispatch/view";
	/**E公估医健险接报案调度保存：POST*/
	public final static String YJX_BAOAN_CASE_DISPATCH_SAVE = IPHOME+"/yjx/dispatch/save";
	/**E公估医健险接报案调度删除：POST*/
	public final static String YJX_BAOAN_CASE_DISPATCH_DELETE = IPHOME+"/yjx/dispatch/delete";
	/**获取医健险待审核任务列表：GET*/
	public final static String YJX_SHENHE_ORDER_LIST = IPHOME+"/yjx/dispatch/listAudit";
	/**医健险审核退回：POST*/
	public final static String YJX_SHENHE_REJECT = IPHOME+"/audits/action/yjx/rejected";
	/**医健险审核通过：POST*/
	public final static String YJX_SHENHE_PASS = IPHOME+"/audits/action/yjx/pass";

	/**获取分散型公估师任务列表**/
	public final static String FSX_GGS_ORDER_LIST = IPHOME +"/other/dispatch/list";
	/**公估师接受或拒绝任务 --POST**/
	public final static String FSX_GGS_ACCEPT_ORDER = IPHOME +"/other/dispatch/accept";
	/**公估师取消任务 --POST**/
	public final static String FSX_GGS_CANCEL_ORDER = IPHOME +"/other/dispatch/cancel";
	/**公估师到达现场 --POST**/
	public final static String FSX_GGS_SAVE_ORDER = IPHOME +"/other/work/save";
	/**作业图像信息下载 --POST**/
	public final static String FSX_WORK_IMG_DOWLOAD = IPHOME +"/other/work/image/list";
	/**作业详细信息查看 --GET**/
	public final static String FSX_WORK_INFO = IPHOME +"/other/work/view";
	/**作业信息保存 --POST**/
	public final static String FSX_WORK_INFO_SAVE = IPHOME +"/other/work/save";
	/**作业图像信息上传 --POST**/
	public final static String FSX_WORK_IMG_SAVE = IPHOME +"/other/work/image/upload";
	/**作业提交审核 --POST**/
	public final static String FSX_WORK_SUBMIT_RIVIEW = IPHOME +"/other/audit/submit";
	
	/**********************************************车险新全流程*************************************************/
	/**车险公估师任务订单查询 --GET**/
	public final static String CX_NEW_GET_GGS_ORDER = IPHOME +"/cx/order/listPage";
	/**拍照类型字典表 type=cxOrderWorkImageType --GET**/
	public final static String CX_NEW_GET_IMG_TYPE_DICT = IPHOME +"/dict/listByType";
	/**POST 作业保存 userId:User-20180103101603-687B671A  ,orderUid:ORDER--20200706105235-30E90
	 content{“key”:"value"}作业内容，保存为JSON对象 ,status 0：暂存；1：提交（送审）**/
	public final static String CX_NEW_WORK_SAVE = IPHOME +"/cx/order/work/save";
	/**GET 查看订单作业详情 userId:User-20180103101603-687B671A    orderUid:ORDER--20200706105235-30E90**/
	public final static String CX_NEW_GET_ORDER_VIEW_BY_UID = IPHOME +"/cx/order/work/viewByOrderUid";
	/**GET 查看接报案详情 uid报案UID，userid用户Uid**/
	public final static String CX_JIE_BAOAN_INFO = IPHOME +"/cx/baoan/view";
	/**GET查看委托人简称详情 uid简称uid**/
	public final static String CX_GET_WT_SHORT_INFO = IPHOME +"/entruster/shortName/cx/findByUid";
	/**GET 根据报案编号查询订单列表信息 userId,baoanUid**/
	public final static String CX_GET_ORDER_LIST_BY_BAOAN_UID = IPHOME +"/cx/order/listByBaoanUid";
	/**GET 根据订单编号查询某个订单 userId,uid**/
	public final static String CX_GET_ORDER_VIEW= IPHOME +"/cx/order/view";
	/**GET 作业图片查询 baoanUid：报案UID,isDelete:0否，1是**/
	public final static String CX_GET_WORK_IMG= IPHOME +"/cx/order/work/images/list";
	/**POST 作业图片保存 userId；orderUid任务id；baoanUid接报案UId；source图片来源
	 type图片类型；fileUrl；fileName；fileSuffix。**/
	public final static String CX_UP_WORK_IMG= IPHOME +"/cx/order/work/images/save";
	/**GET 查询订单留言信息 userId;orderUid*/
	public final static String CX_GET_EXAMINE_INFO= IPHOME +"/cx/order/work/message/listByOrderUid";
	/**POST 留言保存 userId;orderUid;content*/
	public final static String CX_SAVE_EXAMINE_INFO= IPHOME +"/cx/order/work/message/save";
	/**GET 获取用户账户信息 userId*/
	public final static String CX_EXT_USER= IPHOME +"/extUser/viewByUserId";
	
	
	
	
	/**
	 * code=1获取省份接口，code=2获取市县接口，code=3获取区接口
	 * @param code
	 * @return
	 */
	public static String getCityUrlInterface(int code){
		switch (code) {
		case HttpRequestTool.GET_PROVINCE_LIST:
			return GET__PROBINCE_INFO;
			
		case HttpRequestTool.GET_CITYE_LIST:
			return GET_CITY_BY_PROBINCE_ID;
			
		case HttpRequestTool.GET_AREA_LIST:
			return GET_QREA_BY_CITY_ID;
			
		case HttpRequestTool.GET_CITYE_LIST2:
			return GET_QREA_BY_CITY_ID;

		default:
			return GET__PROBINCE_INFO;
		}
	}
		
	/**获取医健险接报案录入角色ID*/
	public static int getRoleId(){
		if (IPHOME.equals("http://sys.cnsurvey.cn:8008/cninsure-pz-sys-ws")) { //E公估正式环境APP接报案录入角色id
			return 151;
		}else { //E公估测试环境APP接报案录入角色id
			return 149;
		}
	}
	
	/**获取医健险审核角色ID*/
	public static int getSHId(){
		if (IPHOME.equals("http://sys.cnsurvey.cn:8008/cninsure-pz-sys-ws")) { //E公估正式环境APP接报案录入角色id
			return 147;
		}else { //E公估测试环境APP接报案录入角色id
			return 147;
		}
	}
	/**获取医健险调度角色ID*/
	public static int getDispatchId(){
		if (IPHOME.equals("http://sys.cnsurvey.cn:8008/cninsure-pz-sys-ws")) { //E公估正式环境APP接报案录入角色id
			return 148;
		}else { //E公估测试环境APP接报案录入角色id
			return 148;
		}
	}
	
	/**查勘作业提交审核 post**/
	public static String SubmitWork() {
		return SUBMIT_WORK;
	}

	/**查勘作业保存 post**/
	public static String SaveWorkInfo() {
		return SAVE_WORK_INFO;
	}

	/**查勘回显 get**/
	public static String GetWorkMessages() {
		return GET_WORK_MESSAGES;
	}

	/**修改个人手机号 POST**/
	public static String ChangPhone() {
		return CHANG_PHONE;
	}
	
	/**修改个人用户名密码 POST**/
	public static String ChangPass() {
		return CHANG_PASS;
	}

	/**获取版本信息GET ?userId=userId**/
	public static String GetVersionInfo() {
		return GET_VERSION_INFO;
	}

	/**车险作业界面头部回显**/
	public static String GetWorkMessage() {
		return GET_WORK_MESSAGE;
	}

	/**获取上传图片类型get**/
	public static String GetWorkPhoto() {
		return GET_WORK_PHOTO;
	}

	/**获取上传图片类型get**/
	public static String GetPhotoType() {
		return GET_PHOTO_TYPE;
	}

	/**作业信息-文件上传-上传图片 POST**/
	public static String UploadWorkPhoto() {
		return IPHOME+UPLOAD_WORK_PHOTO;
	}

	/**获取用户详细信息 GET**/
	public static String GetUserInfo() {
		return IPHOME+GET_USER_INFO;
	}

	/**查看自己指定状态的调度订单GET**/
	public static String GetStatuSelforder() {
		return IPHOME+GET_STATU_SELFORDER;
	}

	/**统计用户作业订单各个类型案件量**/
	public static String GetStatusConut() {
		return IPHOME+GET_STATUS_CONUT;
	}

	/**车险业务-查看案件的详细信息（所有的信息） GET**/
	public static String GetOrderInfo() {
		return IPHOME+GET_ORDER_INFO;
	}

	/**接收订单调度post**/
	public static String ReceiveOrder() {
		return IPHOME+RECEIVE_ORDER;
	}
	
	/**取消订单调度post**/
	public static String CancelOrder() {
		return IPHOME+CANCEL_ORDER;
	}

	/**登录地址**/
	public static String LoginByPass() {
		return IPHOME+LOGIN_BY_PASS;
	}

	/**上传CID**/
	public static String UpCid() {
		return IPHOME+UP_CID;
	}

	/**上传经纬度*/
	public static String UpLocation() {
		return IPHOME+UP_LOCATION;
	}

	/**查看自己调度的订单GET**/
	public static String GetSelforder() {
		return IPHOME+GET_SELFORDER;
	}
	
	
	

}
