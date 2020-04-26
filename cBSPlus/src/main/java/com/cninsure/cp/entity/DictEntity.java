package com.cninsure.cp.entity;

import java.util.ArrayList;
import java.util.List;

import com.cninsure.cp.entity.DictEntity.DictDatas.publicData;

public class DictEntity extends FCBasicEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * case_progress 案件进度
		case_filing 案件归档
		loss_currency 币别
		feiche_case_account 估损范围
		depute_sf 委托人身份
		business_type 业务类型
		rev_case_type 接案方式
		case_industry 所属行业
		danger_res 出险原因
	 */
	
	public DictDatas data;
	public static class DictDatas{
		/**案件进度**/
		public List<publicData> case_progress;
		/**案件归档**/
		public List<publicData> case_filing;
		/**币别**/
		public List<publicData> loss_currency;
		/**估损范围**/
		public List<publicData> feiche_case_account;
		/**委托人身份**/
		public List<publicData> depute_sf;
		/**业务类型**/
		public List<publicData> business_type;
		/**接案方式**/
		public List<publicData> rev_case_type;
		/**所属行业**/
		public List<publicData> case_industry;
		/**出险原因**/
		public List<publicData> danger_res;
		
		/**险种大类**/
		public List<publicData> risk_type;
		/**财险险种**/
		public List<publicData> feiche_baoxian_type;
		/**水险险种**/
		public List<publicData> feicheBaoxianType;
		/**创新及分散型险种**/
		public List<publicData> feiche_baoxian_type_other;
		/**紧急程度**/
		public List<publicData> urgency_level;
		/**是否本异地**/
		public List<publicData> is_yd;
		/**财险委托事项*/
		public List<publicData> depute_item;
		/**水险委托事项*/
		public List<publicData> deputeItem; 
		/**船舶类型*/
		public List<publicData> ship_type; 
		/**船舶吨位*/
		public List<publicData> ship_dw; 
		/**主要受损类型*/
		public List<publicData> mainLoss_type; 
		/**上传文件类型*/
		public List<publicData> file_type_id;

		
		
		public static class publicData{
			 private String label;
			 public int value;
			 
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				label=label.replace("&lt;", "<");
				label=label.replace("&gt;", ">");
				this.label = label;
			}
		}
	}
	
	public List<publicData> addhint(List<publicData> tmpList){
		publicData emptyData=new publicData();
		emptyData.label="";
		emptyData.value=-1;
		List<publicData> tempDatas=new ArrayList<publicData>();
		tempDatas.add(emptyData);
		tempDatas.addAll(tmpList);
		return tmpList;
	}

	
	public List<String> getDictArr(List<publicData> dictList){
		List<String> tempdata=new ArrayList<String>();
		for (publicData data:dictList) {
			tempdata.add(data.label);
		}
		return tempdata;
	}
}
