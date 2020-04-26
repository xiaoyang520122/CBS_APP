package com.cninsure.cp.entity.fc;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;

public class WorkBean extends FCBasicEntity{
	private static final long serialVersionUID = 1L;
	public DataBean data;
	public static class DataBean  implements Serializable {
		private static final long serialVersionUID = 1L;
		public Work work = new Work();
		public CaseOperation op = new CaseOperation();
		public CaseManage m = new CaseManage();
		public List<CaseRelate> rels = new ArrayList<CaseRelate>(); //检验师
		public List<CasePolicyLevel> lel =  new ArrayList<CasePolicyLevel>();//保单承保标的险别
		public List<CaseDepute> deps = new ArrayList<CaseDepute>(); //次托方
		public List<CaseChaKan> ck =new ArrayList<CaseChaKan>(); // 
		public List<CaseRelateDevote> devote = new ArrayList<CaseRelateDevote> (); //检验师贡献列表
		
		public String yuguAmountByUser;
		public String yuguAmountByOrg;
		public String lossAmountByUser;
		public String lossAmountByOrg;
		public int caseType;
	}
}
