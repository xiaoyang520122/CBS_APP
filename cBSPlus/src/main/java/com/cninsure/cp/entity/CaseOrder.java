package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    public TableDataEntity tableData;

    public static class TableDataEntity {
        public List<PublicOrderEntity> data;

        public int length;
        public int recordsFiltered;
        public int recordsTotal;
        public int start;
    }

}
