package com.cninsure.cp.cx.autoloss.entity;

import java.util.Date;


/**
 * 厂家、品牌、车系表，使用cateLevel字段区分
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年9月11日 下午2:32:50
 */
public class CategoryTable {
	
	  public Integer cateId;
	  public Long cateParentId;//父级ID
	  public String cateCode;//编码
	  public String cateName;//名称（前端申请必填）
	  public String catePinyinCode;//拼音
	  public String cateLpckCode;
	  public String cateSearchKey;//查询key值
	  public Long cateLevel;//级别1：厂家；2：品牌；3：车系（前端申请必填）
	  public Long cateSort;//排序
	  public String cateCountry;//生产类型；0：全部；21：国产；20：进口；22：合资（前端申请必填）
	  public Long cateIsLeaf;//是否叶子节点cateLevel=3的时候这里为1，其他级别都为0
	  public Long cateIsEnable;//是否启用1：启用；0：不启用
	  public Long cateIsDelete;//是否删除
	  public Long cateIsLoss;//是否报损
	  public Date cateCreateDate;//创建日期
	  public Date cateUpdateDate;//更新日期
	  public String cateRemark;//备注
	  public String dataEdition;//
	  public Long version;//版本号


	/**
	 * @return the cateId
	 */
	public Integer getCateId() {
		return cateId;
	}


	/**
	 * @param cateId the cateId to set
	 */
	public void setCateId(Integer cateId) {
		this.cateId = cateId;
	}


	/**
	 * @return the cateParentId
	 */
	public Long getCateParentId() {
		return cateParentId;
	}


	/**
	 * @param cateParentId the cateParentId to set
	 */
	public void setCateParentId(Long cateParentId) {
		this.cateParentId = cateParentId;
	}


	/**
	 * @return the cateCode
	 */
	public String getCateCode() {
		return cateCode;
	}


	/**
	 * @param cateCode the cateCode to set
	 */
	public void setCateCode(String cateCode) {
		this.cateCode = cateCode;
	}


	/**
	 * @return the cateName
	 */
	public String getCateName() {
		return cateName;
	}


	/**
	 * @param cateName the cateName to set
	 */
	public void setCateName(String cateName) {
		this.cateName = cateName;
	}


	/**
	 * @return the catePinyinCode
	 */
	public String getCatePinyinCode() {
		return catePinyinCode;
	}


	/**
	 * @param catePinyinCode the catePinyinCode to set
	 */
	public void setCatePinyinCode(String catePinyinCode) {
		this.catePinyinCode = catePinyinCode;
	}


	/**
	 * @return the cateLpckCode
	 */
	public String getCateLpckCode() {
		return cateLpckCode;
	}


	/**
	 * @param cateLpckCode the cateLpckCode to set
	 */
	public void setCateLpckCode(String cateLpckCode) {
		this.cateLpckCode = cateLpckCode;
	}


	/**
	 * @return the cateSearchKey
	 */
	public String getCateSearchKey() {
		return cateSearchKey;
	}


	/**
	 * @param cateSearchKey the cateSearchKey to set
	 */
	public void setCateSearchKey(String cateSearchKey) {
		this.cateSearchKey = cateSearchKey;
	}


	/**
	 * @return the cateLevel
	 */
	public Long getCateLevel() {
		return cateLevel;
	}


	/**
	 * @param cateLevel the cateLevel to set
	 */
	public void setCateLevel(Long cateLevel) {
		this.cateLevel = cateLevel;
	}


	/**
	 * @return the cateSort
	 */
	public Long getCateSort() {
		return cateSort;
	}


	/**
	 * @param cateSort the cateSort to set
	 */
	public void setCateSort(Long cateSort) {
		this.cateSort = cateSort;
	}


	/**
	 * @return the cateCountry
	 */
	public String getCateCountry() {
		return cateCountry;
	}


	/**
	 * @param cateCountry the cateCountry to set
	 */
	public void setCateCountry(String cateCountry) {
		this.cateCountry = cateCountry;
	}


	/**
	 * @return the cateIsLeaf
	 */
	public Long getCateIsLeaf() {
		return cateIsLeaf;
	}


	/**
	 * @param cateIsLeaf the cateIsLeaf to set
	 */
	public void setCateIsLeaf(Long cateIsLeaf) {
		this.cateIsLeaf = cateIsLeaf;
	}


	/**
	 * @return the cateIsEnable
	 */
	public Long getCateIsEnable() {
		return cateIsEnable;
	}


	/**
	 * @param cateIsEnable the cateIsEnable to set
	 */
	public void setCateIsEnable(Long cateIsEnable) {
		this.cateIsEnable = cateIsEnable;
	}


	/**
	 * @return the cateIsDelete
	 */
	public Long getCateIsDelete() {
		return cateIsDelete;
	}


	/**
	 * @param cateIsDelete the cateIsDelete to set
	 */
	public void setCateIsDelete(Long cateIsDelete) {
		this.cateIsDelete = cateIsDelete;
	}


	/**
	 * @return the cateIsLoss
	 */
	public Long getCateIsLoss() {
		return cateIsLoss;
	}


	/**
	 * @param cateIsLoss the cateIsLoss to set
	 */
	public void setCateIsLoss(Long cateIsLoss) {
		this.cateIsLoss = cateIsLoss;
	}


	/**
	 * @return the cateCreateDate
	 */
	public Date getCateCreateDate() {
		return cateCreateDate;
	}


	/**
	 * @param cateCreateDate the cateCreateDate to set
	 */
	public void setCateCreateDate(Date cateCreateDate) {
		this.cateCreateDate = cateCreateDate;
	}


	/**
	 * @return the cateUpdateDate
	 */
	public Date getCateUpdateDate() {
		return cateUpdateDate;
	}


	/**
	 * @param cateUpdateDate the cateUpdateDate to set
	 */
	public void setCateUpdateDate(Date cateUpdateDate) {
		this.cateUpdateDate = cateUpdateDate;
	}


	/**
	 * @return the cateRemark
	 */
	public String getCateRemark() {
		return cateRemark;
	}


	/**
	 * @param cateRemark the cateRemark to set
	 */
	public void setCateRemark(String cateRemark) {
		this.cateRemark = cateRemark;
	}


	/**
	 * @return the dataEdition
	 */
	public String getDataEdition() {
		return dataEdition;
	}


	/**
	 * @param dataEdition the dataEdition to set
	 */
	public void setDataEdition(String dataEdition) {
		this.dataEdition = dataEdition;
	}


	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	
	public CategoryTable() {
		super();
	}

	public CategoryTable(Integer cateId,Long cateParentId, String cateCode, String cateName, String catePinyinCode, String cateLpckCode, 
			String cateSearchKey, Long cateLevel, Long cateSort, String cateCountry, Long cateIsLeaf, Long cateIsEnable, Long cateIsDelete, Long cateIsLoss, Date cateCreateDate, Date cateUpdateDate, String cateRemark, String dataEdition, Long version)
	  {
		super();
		this.cateId = cateId;
	    this.cateParentId = cateParentId;
	    this.cateCode = cateCode;
	    this.cateName = cateName;
	    this.catePinyinCode = catePinyinCode;
	    this.cateLpckCode = cateLpckCode;
	    this.cateSearchKey = cateSearchKey;
	    this.cateLevel = cateLevel;
	    this.cateSort = cateSort;
	    this.cateCountry = cateCountry;
	    this.cateIsLeaf = cateIsLeaf;
	    this.cateIsEnable = cateIsEnable;
	    this.cateIsDelete = cateIsDelete;
	    this.cateIsLoss = cateIsLoss;
	    this.cateCreateDate = cateCreateDate;
	    this.cateUpdateDate = cateUpdateDate;
	    this.cateRemark = cateRemark;
	    this.dataEdition = dataEdition;
	    this.version = version;
	  }
}
