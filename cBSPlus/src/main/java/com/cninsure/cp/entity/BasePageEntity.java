package com.cninsure.cp.entity;

import com.cninsure.cp.entity.cx.CxOrderEntity;

import java.io.Serializable;
import java.util.List;

public class BasePageEntity implements Serializable {

    public int endRow;   //2,
    public int firstPage;   //1,
    public boolean hasNextPage;   //是否有下一页,
    public boolean hasPreviousPage;   //是否有 上 一页,
    public boolean isFirstPage;   //是否第一页,
    public boolean isLastPage;   //是否最后一页,
    public int lastPage;   //最后一页页码,
    public int navigatePages;   //导航页8,
    public int[] navigatepageNums;   //[1,2,3,4,5,6,7,8],导航页码数组
    public int nextPage;   //下一页页码1,
    public int pageNum;   //页码,
    public int pageSize;   //当前页包含信息条数
    public int pages;   //9,
    public int prePage;   //前一页 0,
    public int size;   //每页包含信息标准条数2,
    public int startRow;   //起始行（一般第一页是1，第二页是每页条数+1）,
    public int total;   //左右页包含信息总数

}
