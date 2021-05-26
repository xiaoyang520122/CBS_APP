package com.cninsure.cp.entity.cx;

import java.util.Date;

public class CxOrderWorkMediaTypeTable {

    public Long id;
    public Date createTime;
    public String createBy;
    public Date updateTime;
    public String updateBy;
    public String label;//显示值
    public String value;//字典值
    public Integer level;//级别
    public Integer uploadType;//上传类型 0:不能上传；1:非必传；2:满足一定条件为必传；3:满足一定条件为非必传；
    public String uploadCondition;//上传控制条件
    public Integer showType;//显示类型 0:正常显示；1:按车牌名称动态生成；2:按物损名称动态生成；3:按伤者名称动态生成；
    public String dynamicLabel;//动态名称
    public String parentPathIds;//父级路径ID
    public String parentPathNames;//父级路径名称
    public String parentId;//父级值
    public String parentLabel;//父级标签
    public Integer delFlag;
    public String wtShortNameId;//委托人简称UID
    public String wtShortName;//委托人简称
    public String bussTypes;//所属业务品种
    public String tip;//提示

    public CxOrderWorkMediaTypeTable copyNewMediaType(String valueNew,String labelNew){
        CxOrderWorkMediaTypeTable newMediaType = new CxOrderWorkMediaTypeTable();
        newMediaType.createTime = createTime;
        newMediaType.createBy = createBy;
        newMediaType.updateTime = updateTime;
        newMediaType.updateBy = updateBy;
        newMediaType.label = labelNew;//显示值
        newMediaType.value = valueNew;//字典值
        newMediaType.level = level;//级别
        newMediaType.uploadType = uploadType;//上传类型 0:不能上传；1:非必传；2:满足一定条件为必传；3:满足一定条件为非必传；
        newMediaType.uploadCondition = uploadCondition;//上传控制条件
        newMediaType.showType = showType;//显示类型 0:正常显示；1:按车牌名称动态生成；2:按物损名称动态生成；3:按伤者名称动态生成；
        newMediaType.dynamicLabel = dynamicLabel;//动态名称
        newMediaType.parentPathIds = parentPathIds;//父级路径ID
        newMediaType.parentPathNames = parentPathNames;//父级路径名称
        newMediaType.parentId = parentId;//父级值
        newMediaType.parentLabel = parentLabel;//父级标签
        newMediaType.delFlag = delFlag;
        newMediaType.wtShortNameId = wtShortNameId;//委托人简称UID
        newMediaType.wtShortName = wtShortName;//委托人简称
        newMediaType.bussTypes = bussTypes;//所属业务品种
        newMediaType.tip = tip;//提示
        return newMediaType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
    }

    public String getUploadCondition() {
        return uploadCondition;
    }

    public void setUploadCondition(String uploadCondition) {
        this.uploadCondition = uploadCondition;
    }

    public Integer getShowType() {
        return showType;
    }

    public void setShowType(Integer showType) {
        this.showType = showType;
    }

    public String getDynamicLabel() {
        return dynamicLabel;
    }

    public void setDynamicLabel(String dynamicLabel) {
        this.dynamicLabel = dynamicLabel;
    }

    public String getParentPathIds() {
        return parentPathIds;
    }

    public void setParentPathIds(String parentPathIds) {
        this.parentPathIds = parentPathIds;
    }

    public String getParentPathNames() {
        return parentPathNames;
    }

    public void setParentPathNames(String parentPathNames) {
        this.parentPathNames = parentPathNames;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(String parentLabel) {
        this.parentLabel = parentLabel;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getWtShortNameId() {
        return wtShortNameId;
    }

    public void setWtShortNameId(String wtShortNameId) {
        this.wtShortNameId = wtShortNameId;
    }

    public String getWtShortName() {
        return wtShortName;
    }

    public void setWtShortName(String wtShortName) {
        this.wtShortName = wtShortName;
    }

    public String getBussTypes() {
        return bussTypes;
    }

    public void setBussTypes(String bussTypes) {
        this.bussTypes = bussTypes;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
