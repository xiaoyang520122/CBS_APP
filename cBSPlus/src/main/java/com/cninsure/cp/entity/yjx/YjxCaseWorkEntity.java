package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class YjxCaseWorkEntity implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
    public Integer id;
	
	/**
    * 案件标识(案件编号）
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
    * 创建人
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
    * 调度ID
    */
//    @NotNullValidator(errorMessage="调度ID不能为空")
    public long dispatchId;

   /**
    * 调度UID
    */
//    @NotNullValidator(errorMessage="调度UID不能为空")
    public String dispatchUid;

   /**
    * 调查时间"yyyy-MM-dd HH:mm:ss"
    */
//    @NotNullValidator(errorMessage="调查时间不能为空")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date surveyTime;

   /**
    * 调查地点
    */
//    @NotNullValidator(errorMessage="调度地点不能为空")
    public String surveyAddress;

   /**
    * 调查方式
    */
//    @NotNullValidator(errorMessage="调查方式不能为空")
    public String surveyType;

   /**
    * 调查资料
    */
//    @NotNullValidator(errorMessage="调度资料不能为空")
    public String surveyData;

   /**
    * 调查经过
    */
//    @NotNullValidator(errorMessage="调度经过不能为空")
    public String surveyDescription;

   /**
    * 调查结论
    */
//    @NotNullValidator(errorMessage="调度结论不能为空")
    public String surveyConclusion;
    
   /**
     * 状态
     */
//    @NotNullValidator(errorMessage="状态不能为空")
    public Integer status;
     
    /**委托资料文件*/
    public String fileUrls;
    
   /**
     * 作业对象信息
     */
//    @Transient
    public List<YjxCaseBaoanInjuredTable> injuredList;

}
