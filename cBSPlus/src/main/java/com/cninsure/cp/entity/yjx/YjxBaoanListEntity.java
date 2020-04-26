package com.cninsure.cp.entity.yjx;

import java.util.List;

public class YjxBaoanListEntity {
	
	//当前页
    public int pageNum;
    //每页的数量
    public int pageSize;
    //当前页的数量
    public int size;
    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    //当前页面第一个元素在数据库中的行号
    public int startRow;
    //当前页面最后一个元素在数据库中的行号
    public int endRow;
    //总记录数
    public long total;
    //总页数
    public int pages;
    //结果集
    public List<YjxCaseBaoanEntity> list;

    //第一页
    public int firstPage;
    //前一页
    public int prePage;
    //下一页
    public int nextPage;
    //最后一页
    public int lastPage;

    //是否为第一页
    public boolean isFirstPage = false;
    //是否为最后一页
    public boolean isLastPage = false;
    //是否有前一页
    public boolean hasPreviousPage = false;
    //是否有下一页
    public boolean hasNextPage = false;
    //导航页码数
    public int navigatePages;
    //所有导航页号
    public int[] navigatepageNums;
    /**当前请求页页码*/
//    public int myPageNum=0;

}
