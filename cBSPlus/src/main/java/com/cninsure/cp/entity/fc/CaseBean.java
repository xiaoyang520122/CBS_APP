package com.cninsure.cp.entity.fc;

import java.util.ArrayList;
import java.util.List;

public class CaseBean {

	public CaseManage casem;
	/**次托方**/
	public List<CaseDepute> deps = new ArrayList<CaseDepute>(); //次托方
	/**检验师*/
	public List<CaseRelate> rels = new ArrayList<CaseRelate>(); //检验师
	/**检验师作业情况*/
	public List<CaseRelateDevote> devote = new ArrayList<CaseRelateDevote>(); //检验师作业情况
	/**关联方*/
	public List<CaseLaboratorian> labs = new ArrayList<CaseLaboratorian>(); //关联方
	/**关联方*/
	public List<CaseShip> shp = new ArrayList<CaseShip>(); //关联方
}
