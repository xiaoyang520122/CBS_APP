package com.cninsure.cp.entity.fc;

import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;

public class ScoreFCEntity extends FCBasicEntity {

	public static final long serialVersionUID = 1L;

	public FCScoreData data;

	public static class FCScoreData {

		 	public Integer ggsId;
	        public String fgs;
	        public String yyb;
	        public String ggsName;
	        public String ggsCard;
	        /**创收考考核业务了*/
	        public float realYwl=0;
	        /**公估师薪酬*/
	        public float yybGgsxc=0;
	}

}
