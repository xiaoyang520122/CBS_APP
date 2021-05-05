package com.cninsure.cp.entity.cx;

import android.widget.TextView;

import com.cninsure.cp.entity.PublicOrderEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class CxOrderEntity implements Serializable {

    public int endRow;   //2,
    public int firstPage;   //1,
    public boolean hasNextPage;   //是否有下一页,
    public boolean hasPreviousPage;   //是否有 上 一页,
    public boolean isFirstPage;   //是否第一页,
    public boolean isLastPage;   //是否最后一页,
    public int lastPage;   //最后一页页码,
    public List<CxOrderTable> list;   //
    public int navigatePages;   //导航页8,
    public List<Integer> navigatepageNums;   //[1,2,3,4,5,6,7,8],导航页码数组
    public int nextPage;   //下一页页码1,
    public int pageNum;   //页码,
    public int pageSize;   //当前页包含信息条数
    public int pages;   //9,
    public int prePage;   //前一页 0,
    public int size;   //每页包含信息标准条数2,
    public int startRow;   //起始行（一般第一页是1，第二页是每页条数+1）,
    public int total;   //左右页包含信息总数


    public static class CxOrderTable implements Serializable {

        public Long id;
        public String uid;
        public String baoanUid;
        public String tenantId;
        public String createBy;
        public String createTime;
        public String updateBy;
        public String updateTime;
        public Integer status;//状态
        public Integer productId;//产品细类ID，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
        public String productType;//产品细类，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
        public Integer bussTypeId;//任务类型ID（原业务品种）任务类型限制为以下几种：现场查勘、标的定损、三者定损、物损、人伤查勘。一个案件现场查勘、标的定损任务只有一个（其他可多个）
        public String bussType;//任务类型（原业务品种）任务类型限制为以下几种：现场查勘、标的定损、三者定损、物损、人伤查勘。一个案件现场查勘、标的定损任务只有一个（其他可多个）
        public String acceptInsuranceUid;//承保机构UID
        public String acceptInsurance;//承保机构
        public Integer wtId;//委托人ID
        public String wtName;//委托人名称
        public String wtShortNameId;//委托人简称
        public String wtShortName;//委托人简称
        public Integer local;//是否本异地，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
        public String caseBaoanNo;//报案号
        public Integer deptId;//归属营业部ID 	归属营业部根据产品细类、作业范围自动判断
        public String deptName;//归属营业部名称	归属营业部根据产品细类、作业范围自动判断
        public Integer orgId;//作业营业部ID 
        public String orgName;//作业营业部名称
        //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public String wtDate;//委托时间
        public String baoanPerson;//报案人
        public String baoanPersonMobile;//报案人电话
        public String drivePerson;//驾驶人
        public String drivePersonMobile;//驾驶人电话
        //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public String riskDate;//出险时间
        public String biaodiCarNo;//标的车牌
        public String caseAddress;//出险地点
        public String caseProvince;//所在省
        public String caseCity;//所在市
        public String caseArea;//出险区域
        public String areaNo;//出险区域编码
        public Double longtitude;//经度
        public Double latitude;//维度
        public String districtType;//作业范围
        public String districtTypeName;//作业范围名称
        public String caseDetail;//出险经过
        public Integer autoDispatch;//是否自动调度
        public String operatorUid;//操作人ID
        public String operator;//操作人名称
        public Date claimTime;//认领时间
        public String dispatchTime;//派工时间
        public Date acceptTime;//接单时间
        public String ggsUid;//公估师id
        public String ggsName;//公估师名称
        public String ggsMobile;//公估师电话
        public String coordinatorUid;//协调人id
        public String coordinatorName;//协调人名称
        public String blockedUid;//受阻人id
        public String blockedName;//受阻人名称
        public String auditerUid;//审核人id
        public String auditer;//审核人名称
        public String auditerMobile;//审核人电话
        public Double amount;//订单金额
        public Double rewards;//奖惩
        public Date auditTime;//审核时间
        public Date workSubmitTime;//提交审核时间
        public Long overTime;//时间(min) 创建开始计算
        public String detailTime;//处理时长 公估师接单开始计算。
        public String isArrive; //是否到达现场
        public String arriveTime;//到达现场时间。
        public Double balancePrice; // 机构间结算价
        public Double bargainPrice; // 公估师议价


        public Integer investigationType;//调查类型  全案1，单项0
        public String investigations;//调查内容
        /**转派状态(0：转派待接收；1：已转派；2：拒绝转派)*/
        public Integer transferStatus;//转派状态(0：转派待接收；1：已转派；2：拒绝转派)

        public Double baseFee;//基础费
        public Double extraFee;//超额附加费
        public Double settlementBaseFee;//机构间结算基础费
        public Double settlementExtraFee;//机构间结算超额附加费
        public Double serviceFee;//订单服务费=基础费+超额附加费+额外奖励
        public Double orderFee;//订单金额=机构间结算价基础费+机构间结算价超额附加费+额外奖励
        public Double blanceFee;//机构间结算价结余=订单金额—订单服务费
        public Double canPosAmount;//可提现金额
        public Double bondAmount;//保证金
        public Integer posStatus;//提现状态
        public String posFailReason;//提现失败原因


        public PublicOrderEntity getStandardOrderEnt() {
            PublicOrderEntity stdEnt = new PublicOrderEntity();
            /*********公共**************/
            stdEnt.createDate = createTime;
            stdEnt.id = id;
            /** 案件状态3 作业待处理 4 作业处理中 */
            stdEnt.status=status;
            /** “FC” 代表是非车险，“YJX” 代表是医健险，其他是车险订单 **/
            stdEnt.caseTypeAPP = "CX";
            /**险种**/
            stdEnt.caseTypeId = 100;
            /**险种名称**/
            stdEnt.caseTypeName = "车险";
            /**报案号**/
            stdEnt.baoanNo = caseBaoanNo;
            /**业务品种id**/
            stdEnt.bussTypeId=bussTypeId;
            /**业务品种名称**/
            stdEnt.bussTypeName=bussType;

            stdEnt.caseUid=baoanUid;
            /**接报案编号**/
            stdEnt.caseBaoanUid=baoanUid;
            /**调度人名称**/
            stdEnt.dispatcherName=operator;
            /**调度人Uid**/
            stdEnt.dispatcherUid = operatorUid;
            /**委托人id**/
            stdEnt.entrusterId = wtId;
            /**委托人名称**/
            stdEnt.entrusterName = wtName;
            /**公估师名称**/
            stdEnt.ggsName = ggsName;
            /**公估师UID**/
            stdEnt.ggsUid=ggsUid;
            /**标地方车牌**/
            stdEnt.licensePlateBiaoDi=biaodiCarNo;
            /**订单标识UID**/
            stdEnt.uid=uid;
            /**跟新时间**/
            stdEnt.updateDate=updateTime;
            /**出险时间**/
            stdEnt.riskDate=riskDate;
            /**出险单位联系人*/
            stdEnt.baoanPersonName =baoanPerson;
            /**出险单位联系人电话*/
            stdEnt.baoanPersonPhone =baoanPersonMobile;
            /**出险经过（委托信息）*/
            stdEnt.caseLifecycle=caseDetail;
            /**超时时长*/
            stdEnt.detailTime =detailTime;
            /**订单调度时间**/
            stdEnt.dispatchDate =dispatchTime;
            /**出险地点**/
            stdEnt.caseLocation = caseAddress;
            /**经纬度*/
            stdEnt.caseLocationLatitude=0;//":"22.581202",
            /**经纬度*/
            stdEnt.caseLocationLongitude=0;//":"114.108214",
            stdEnt.investigationType = investigationType;//调查类型  全案1，单项0
            /**转派状态(0：转派待接收；1：已转派；2：拒绝转派)*/
            stdEnt.transferStatus = transferStatus;//转派状态(0：转派待接收；1：已转派；2：拒绝转派)

            stdEnt.areaNo=areaNo;
//            stdEnt.area=area;
//            stdEnt.province=province;
            stdEnt.caseProvince=caseProvince;
//            stdEnt.city=city;


            stdEnt.balancePrice = balancePrice; // 机构间结算价
            stdEnt.bargainPrice = bargainPrice; // 公估师议价

            stdEnt.orderTable = CxOrderTable.this; //任务

            return stdEnt;
        }
    }
}
