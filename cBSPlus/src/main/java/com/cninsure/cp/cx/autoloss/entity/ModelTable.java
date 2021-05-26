package com.cninsure.cp.cx.autoloss.entity;

import java.util.Date;

/**
 * 车型
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年9月11日 下午2:32:37
 */
public class ModelTable {
	
	  public Integer modelId;
	  public Long modelCateId;//车系ID（前端申请必填）
	  public String modelCode;//车型编码
	  public String modelStandardName;//标准名称（前端申请必填）
	  public String modelTrivialName;//普通名称
	  public String modelPinyinCode;//拼音
	  public String modelLpckCode;
	  public String modelType;//类型
	  public String modelClass;//车型类别
	  public String modelCountry;//车型生产类型；0：全部；21：国产；20：进口；22：合资（前端申请必填）
	  public String modelMadeIn;//厂家
	  public Long modelIsInsurer;//是否在保
	  public Long modelIsLoss;//是否报损
	  public Long modelIsPic;
	  public String modelBigPath;
	  public String modelSmallPath;
	  public String modelRemark;//备注
	  public Long modelPartCount;//配件数量
	  public String modelVinNo;//
	  public String modelStructType;
	  public String modelGearBox;
	  public Long modelIsEnable;//是否启用
	  public Long modelIsDelete;//是否删除
	  public Date modelCreateDate;//创建时间
	  public Date modelUpdateDate;//更新时间
	  public String modelUpdateType;//
	  public String modelOperSource;
	  public String dataEdition;
	  public Long version;//版本
	  public String modelFactory;//厂家
	  public String modelDesc;//描述
	  public String tranEdition;
	  public Date partUpdateTime;//配件更新时间
	  public String cateName;

	  
	  public Integer getModelId() {
		return modelId;
	}
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
	public Long getModelCateId() {
		return modelCateId;
	}
	public void setModelCateId(Long modelCateId) {
		this.modelCateId = modelCateId;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getModelStandardName() {
		return modelStandardName;
	}
	public void setModelStandardName(String modelStandardName) {
		this.modelStandardName = modelStandardName;
	}
	public String getModelTrivialName() {
		return modelTrivialName;
	}
	public void setModelTrivialName(String modelTrivialName) {
		this.modelTrivialName = modelTrivialName;
	}
	public String getModelPinyinCode() {
		return modelPinyinCode;
	}
	public void setModelPinyinCode(String modelPinyinCode) {
		this.modelPinyinCode = modelPinyinCode;
	}
	public String getModelLpckCode() {
		return modelLpckCode;
	}
	public void setModelLpckCode(String modelLpckCode) {
		this.modelLpckCode = modelLpckCode;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public String getModelClass() {
		return modelClass;
	}
	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}
	public String getModelCountry() {
		return modelCountry;
	}
	public void setModelCountry(String modelCountry) {
		this.modelCountry = modelCountry;
	}
	public String getModelMadeIn() {
		return modelMadeIn;
	}
	public void setModelMadeIn(String modelMadeIn) {
		this.modelMadeIn = modelMadeIn;
	}
	public Long getModelIsInsurer() {
		return modelIsInsurer;
	}
	public void setModelIsInsurer(Long modelIsInsurer) {
		this.modelIsInsurer = modelIsInsurer;
	}
	public Long getModelIsLoss() {
		return modelIsLoss;
	}
	public void setModelIsLoss(Long modelIsLoss) {
		this.modelIsLoss = modelIsLoss;
	}
	public Long getModelIsPic() {
		return modelIsPic;
	}
	public void setModelIsPic(Long modelIsPic) {
		this.modelIsPic = modelIsPic;
	}
	public String getModelBigPath() {
		return modelBigPath;
	}
	public void setModelBigPath(String modelBigPath) {
		this.modelBigPath = modelBigPath;
	}
	public String getModelSmallPath() {
		return modelSmallPath;
	}
	public void setModelSmallPath(String modelSmallPath) {
		this.modelSmallPath = modelSmallPath;
	}
	public String getModelRemark() {
		return modelRemark;
	}
	public void setModelRemark(String modelRemark) {
		this.modelRemark = modelRemark;
	}
	public Long getModelPartCount() {
		return modelPartCount;
	}
	public void setModelPartCount(Long modelPartCount) {
		this.modelPartCount = modelPartCount;
	}
	public String getModelVinNo() {
		return modelVinNo;
	}
	public void setModelVinNo(String modelVinNo) {
		this.modelVinNo = modelVinNo;
	}
	public String getModelStructType() {
		return modelStructType;
	}
	public void setModelStructType(String modelStructType) {
		this.modelStructType = modelStructType;
	}
	public String getModelGearBox() {
		return modelGearBox;
	}
	public void setModelGearBox(String modelGearBox) {
		this.modelGearBox = modelGearBox;
	}
	public Long getModelIsEnable() {
		return modelIsEnable;
	}
	public void setModelIsEnable(Long modelIsEnable) {
		this.modelIsEnable = modelIsEnable;
	}
	public Long getModelIsDelete() {
		return modelIsDelete;
	}
	public void setModelIsDelete(Long modelIsDelete) {
		this.modelIsDelete = modelIsDelete;
	}
	public Date getModelCreateDate() {
		return modelCreateDate;
	}
	public void setModelCreateDate(Date modelCreateDate) {
		this.modelCreateDate = modelCreateDate;
	}
	public Date getModelUpdateDate() {
		return modelUpdateDate;
	}
	public void setModelUpdateDate(Date modelUpdateDate) {
		this.modelUpdateDate = modelUpdateDate;
	}
	public String getModelUpdateType() {
		return modelUpdateType;
	}
	public void setModelUpdateType(String modelUpdateType) {
		this.modelUpdateType = modelUpdateType;
	}
	public String getModelOperSource() {
		return modelOperSource;
	}
	public void setModelOperSource(String modelOperSource) {
		this.modelOperSource = modelOperSource;
	}
	public String getDataEdition() {
		return dataEdition;
	}
	public void setDataEdition(String dataEdition) {
		this.dataEdition = dataEdition;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getModelFactory() {
		return modelFactory;
	}
	public void setModelFactory(String modelFactory) {
		this.modelFactory = modelFactory;
	}
	public String getModelDesc() {
		return modelDesc;
	}
	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}
	public String getTranEdition() {
		return tranEdition;
	}
	public void setTranEdition(String tranEdition) {
		this.tranEdition = tranEdition;
	}
	public Date getPartUpdateTime() {
		return partUpdateTime;
	}
	public void setPartUpdateTime(Date partUpdateTime) {
		this.partUpdateTime = partUpdateTime;
	}
	public String getCateName() {
		return cateName;
	}
	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
	

	
	public ModelTable(){
		  super(); 
	  }
	
	  public ModelTable(Integer modelId,Long modelCateId, String modelCode, String modelStandardName, String modelTrivialName, String modelPinyinCode, String modelLpckCode, String modelType, 
			  String modelClass, String modelCountry, String modelMadeIn, Long modelIsInsurer, Long modelIsLoss, Long modelIsPic, String modelBigPath, String modelSmallPath, 
			  String modelRemark, Long modelPartCount, String modelVinNo, String modelStructType, String modelGearBox, Long modelIsEnable, Long modelIsDelete, Date modelCreateDate, 
			  Date modelUpdateDate, String modelUpdateType, String modelOperSource, String dataEdition, Long version, String modelFactory, String modelDesc, String tranEdition, Date partUpdateTime)
	  {
		this.modelId = modelId;
	    this.modelCateId = modelCateId;
	    this.modelCode = modelCode;
	    this.modelStandardName = modelStandardName;
	    this.modelTrivialName = modelTrivialName;
	    this.modelPinyinCode = modelPinyinCode;
	    this.modelLpckCode = modelLpckCode;
	    this.modelType = modelType;
	    this.modelClass = modelClass;
	    this.modelCountry = modelCountry;
	    this.modelMadeIn = modelMadeIn;
	    this.modelIsInsurer = modelIsInsurer;
	    this.modelIsLoss = modelIsLoss;
	    this.modelIsPic = modelIsPic;
	    this.modelBigPath = modelBigPath;
	    this.modelSmallPath = modelSmallPath;
	    this.modelRemark = modelRemark;
	    this.modelPartCount = modelPartCount;
	    this.modelVinNo = modelVinNo;
	    this.modelStructType = modelStructType;
	    this.modelGearBox = modelGearBox;
	    this.modelIsEnable = modelIsEnable;
	    this.modelIsDelete = modelIsDelete;
	    this.modelCreateDate = modelCreateDate;
	    this.modelUpdateDate = modelUpdateDate;
	    this.modelUpdateType = modelUpdateType;
	    this.modelOperSource = modelOperSource;
	    this.dataEdition = dataEdition;
	    this.version = version;
	    this.modelFactory = modelFactory;
	    this.modelDesc = modelDesc;
	    this.tranEdition = tranEdition;
	    this.partUpdateTime = partUpdateTime;
	  }	  
}
