package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class YjxCaseBaoanEntity implements Serializable {
    
	private static final long serialVersionUID = 1L;

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
    * 险种类型ID
    */
    public Integer caseTypeId;

    /**
    * 险种类型名称
    */
    public String caseType;

    /**
    * 产品ID
    */
//    //@NotNullValidator(errorMessage="产品ID不能为空")
    public Integer productId;

    /**
    * 产品名称
    */
//    //@NotNullValidator(errorMessage="产品名称不能为空")
    public String product;

    /**
    * 业务类型ID
    */
    //@NotNullValidator(errorMessage="业务品种ID不能为空")
    public Integer bussTypeId;

    /**
    * 业务名称
    */
    //@NotNullValidator(errorMessage="业务品种不能为空")
    public String bussType;

    /**
     * 险种大类ID
     */
    //@NotNullValidator(errorMessage="险种大类ID不能为空")
    public String insuranceBigTypeId;
    
    /**
    * 险种大类
    */
    //@NotNullValidator(errorMessage="险种大类不能为空")
    public String insuranceBigType;

    /**
     * 险种细类ID
     */
    //@NotNullValidator(errorMessage="险种细类ID不能为空")
    public String insuranceSmallTypeId;
    /**
    * 险种细类
    */
    //@NotNullValidator(errorMessage="险种细类不能为空")
    public String insuranceSmallType;

    /**
    * 委托人ID
    */
    //@NotNullValidator(errorMessage="委托人ID不能为空")
    public Integer wtId;

    /**
    * 委托人名称
    */
    //@NotNullValidator(errorMessage="委托人名称不能为空")
    public String wtName;

    /**
    * 委托人所在地
    */
    public String wtAreaName;

    /**
    * 委托联系人
    */
    //@NotNullValidator(errorMessage="委托人联系人不能为空")
    public String wtCotact;

    /**
    * 委托联系人电话
    */
    //@NotNullValidator(errorMessage="委托人联系人电话不能为空")
    public String wtContactTel;

    /**
    * 保单号
    */
    //@NotNullValidator(errorMessage="保单号不能为空")
    public String policyNo;

    /**
    * 报案号
    */
    //@NotNullValidator(errorMessage="报案号不能为空")
    public String caseBaoanNo;

    /**
    * 保险起期(pattern="yyyy-MM-dd HH:mm:ss")
    */
    //@NotNullValidator(errorMessage="保险起期不能为空")
    public Date insuranceStartDate;

    /**
    * 保险止期(pattern="yyyy-MM-dd HH:mm:ss")
    */
    //@NotNullValidator(errorMessage="保险止期不能为空")
    public Date insuranceEndDate;

    /**
    * 委托日期(pattern="yyyy-MM-dd HH:mm:ss")
    */
    //@NotNullValidator(errorMessage="委托日期不能为空")
    public Date wtDate;

    /**
    * 时效
    */
    //@NotNullValidator(errorMessage="时效不能为空")
    public Integer aging;

    /**
    * 备注
    */
    public String remark;

    /**
    * 状态
    */
    public Integer status;

    /**
    * 是否删除
    */
    public Integer delFlag;

    /**
    * 被保险人
    */
    //@NotNullValidator(errorMessage="被保险人不能为空")
    public String insuredPerson;

    /**
    * 被保险人证件号
    */
    //@NotNullValidator(errorMessage="被保险人证件号不能为空")
    public String insuredPersonCardNo;

    /**
    * 被保险人联系方式
    */
    //@NotNullValidator(errorMessage="被保险人联系方式不能为空")
    public String insuredPersonTel;

    /**
     * 被保险人所在省ID
     */
    //@NotNullValidator(errorMessage="被保险人所在省ID不能为空")
    public String insuredPersonProvinceId;
    
    /**
    * 被保险人所在省
    */
    //@NotNullValidator(errorMessage="被保险人所在省不能为空")
    public String insuredPersonProvince;

    /**
     * 被保险人所在市ID
     */
    //@NotNullValidator(errorMessage="被保险人所在市ID不能为空")
    public String insuredPersonCityId;
    
    /**
    * 被保险人所在市
    */
    //@NotNullValidator(errorMessage="被保险人所在市不能为空")
    public String insuredPersonCity;

    /**
    * 被保险人所在地址
    */
    //@NotNullValidator(errorMessage="被保险人所在地址不能为空")
    public String insuredPersonAddress;

    /**
    * 被保险人联系人
    */
    //@NotNullValidator(errorMessage="被保险人联系人不能为空")
    public String insuredPersonContact;

    /**
    * 被保险人联系人联系方式
    */
    //@NotNullValidator(errorMessage="被保险人联系人联系方式不能为空")
    public String insuredPersonContactTel;

    /**
    * 报案人
    */
    public String baoanPerson;

    /**
    * 报案人联系方式
    */
    public String baoanPersonTel;

    /**
    * 出险时间(pattern="yyyy-MM-dd HH:mm:ss")
    */
    //@NotNullValidator(errorMessage="出险时间不能为空")
    public Date riskDate;

    /**
     * 出险省ID
     */
    //@NotNullValidator(errorMessage="出险省份ID不能为空")
    public String riskProvinceId;
    
    /**
    * 出险省
    */
    //@NotNullValidator(errorMessage="出险省份不能为空")
    public String riskProvince;

    /**
    * 是否本地（1是，0否）
    */
    //@NotNullValidator(errorMessage="是否本地不能为空")
    public Integer local;
    
    /**
     * 出险市ID
     */
    //@NotNullValidator(errorMessage="出险地市ID不能为空")
    public String riskCityId;

    /**
    * 出险市
    */
    //@NotNullValidator(errorMessage="出险地市不能为空")
    public String riskCity;

    /**
    * 出险原因
    */
    public String riskReason;

    /**
    * 出险地点
    */
    //@NotNullValidator(errorMessage="出险地址不能为空")
    public String riskAddress;

    /**
    * 结案时间(pattern="yyyy-MM-dd HH:mm:ss")
    */
    //@NotNullValidator(errorMessage="结案时间不能为空")
    public Date finishDate;

    /**
    * 预估公估费
    */
    //@NotNullValidator(errorMessage="预估公估费不能为空")
    public BigDecimal yuguAmount;

    /**
    * 差旅费
    */
    //@NotNullValidator(errorMessage="差旅费不能为空")
    public BigDecimal clAmount;
    
	 /**
	 * 归属机构ID 
	 */
    //@NotNullValidator(errorMessage="归属机构ID不能为空")
    public Integer gsOrgId;
    
     /**
     * 归属机构
     */
    //@NotNullValidator(errorMessage="归属机构不能为空")
    public String gsOrg;
    
    /***
     *是否生成报告 0：未生成，1：已生成
     */
    public Integer reportGennerated = 0;
    
    /**
     * 作业对象信息
     */
    public List<YjxCaseBaoanInjuredTable> injuredList;
    
    /**
     * 委托资料列表+++++++++++++++++++++这里是一个集合，但是上传的时候只有一个，所有数据就存在0号位上
     */
    public List<YjxCaseBaoanEntrustDataTable> entrustDataList;
    
    /**
     * 案件调度列表
     */
    public List<YjxCaseDispatchTable> dispatchList;

}