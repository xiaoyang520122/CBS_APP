package com.cninsure.cp.entity.yjx;

import java.util.Date;

public class YjxCaseDispatchTable {
    /**  */
    public Long id;

    /**
    * 调度UID
    */
    public String uid;

    /**
    * 主体标识
    */
    public String tenantId;

    /**
    * 创建时间
    */
    public Date createDate;

    /**
    * 创建者
    */
    public String createBy;

    /**
    * 更新时间
    */
    public Date updateDate;

    /**
    * 更新人
    */
    public String updateBy;
    
    /**
     * 接报案编号
     */
//    @NotNullValidator(errorMessage="报案编号不能为空")
    public String caseBaoanUid;
    
    /**
     * 报案号
     */
//    @NotNullValidator(errorMessage="报案号不能为空")
    public String caseBaoanNo;

    /**
    * 归属机构ID
    */
//    @NotNullValidator(errorMessage="归属机构ID不能为空")
    public Integer gsOrgId;

    /**
    * 归属机构
    */
//    @NotNullValidator(errorMessage="归属机构不能为空")
    public String gsOrg;

    /**
    * 产品细类ID
    */
//    @NotNullValidator(errorMessage="产品细类ID不能为空")
    public Integer productId;

    /**
    * 产品细类
    */
//    @NotNullValidator(errorMessage="产品细类不能为空")
    public String product;

    /**
    * 业务品种ID
    */
//    @NotNullValidator(errorMessage="业务品种ID不能为空")
    public Integer bussTypeId;

    /**
    * 业务品种
    */
//    @NotNullValidator(errorMessage="业务品种不能为空")
    public String bussType;

    /**
    * 险种大类ID
    */
//    @NotNullValidator(errorMessage="险种大类ID不能为空")
    public String insuranceBigTypeId;
    
    /**
     * 险种大类
     */
//    @NotNullValidator(errorMessage="险种大类不能为空")
    public String insuranceBigType;

    /**
     * 险种细类ID
     */
//    @NotNullValidator(errorMessage="险种细类ID不能为空")
    public String insuranceSmallTypeId;
    /**
    * 险种细类
    */
//    @NotNullValidator(errorMessage="险种细类不能为空")
    public String insuranceSmallType;

    /**
    * 任务地点
    */
    public String taskAddress;

    /**
    * 预约作业时间
    */
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date workTime;

    /**
    * 时效
    */
    public Integer aging;

    /**
    * 状态
    public static final int PREPARED = 0;//调度暂存
	public static final int INITIAL = 1;//调度发起（待处理）
	public static final int RECEIVE = 2;//接收（处理中）
	public static final int AUDITING = 3;//作业提交，审核中
	public static final int AUDITED = 4;//审核完成
	public static final int FIRST_AUDITED = 5;//一审完成
	public static final int ADOPT = 88;//审核驳回
	public static final int REFUSE = 99;//拒绝
	public static final int REDISPATCH = 999;//改派
    */
//    @NotNullValidator(errorMessage="调度状态不能为空")
    public Integer status;
    
    /**
     * 公估师ID
     */
//    @NotNullValidator(errorMessage="公估师ID不能为空")
    public String ggsId;

    /**
    * 公估师名称
    */
//    @NotNullValidator(errorMessage="公估师不能为空")
    public String ggsName;

    /**
    * 作业机构ID
    */
//    @NotNullValidator(errorMessage="作业机构ID不能为空")
    public Integer workOrgId;

    /**
    * 作业机构
    */
//    @NotNullValidator(errorMessage="作业机构为空")
    public String workOrg;

    /**
    * 公估师联系电话
    */
//    @NotNullValidator(errorMessage="公估师联系电话不能为空")
    public String ggsTel;
    
    /**
     * 改派ID
     */
    public Integer reDispatchId;
    
    /**
     * 比例
     */
    public Double ratio;
    
    /**
     * 当前审核人
     */
    public String currentAuditer;
    
    /**调度人**/
    public UserTable user;
    
    /***
     * 公估师列表
     */
//    @Transient
//    public List<YjxCaseDispatchGgsTable> ggsList;

}