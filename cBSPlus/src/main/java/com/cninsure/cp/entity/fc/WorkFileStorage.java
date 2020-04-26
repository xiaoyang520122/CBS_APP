package com.cninsure.cp.entity.fc;

import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;
import com.cninsure.cp.entity.fc.WorkFile.FCFileEntity;

public class WorkFileStorage extends FCBasicEntity {

	public static final long serialVersionUID = 1L;
	
	public List<List<FCFileEntity>> data;

}
