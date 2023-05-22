package com.gumdom.boot.infrastructure.database;

import java.util.List;

public class Page<T> {

    //region

    /**
     * 查询数量
     */
    private Integer fetchNum;

    /**
     * 开始查询的下班
     */
    private Integer beginNum;

    /**
     * 总记录数
     */
    private Integer totalNum;

    /**
     * 记录list
     */
    private List<T> list;

    //endregion


    public Page(){

    }

    public Page(Integer fetchNum, Integer beginNum, Integer totalNum, List<T> list) {
        this.fetchNum = fetchNum;
        this.beginNum = beginNum;
        this.totalNum = totalNum;
        this.list = list;
    }

    //region get&set

    public Integer getFetchNum() {
        return fetchNum;
    }

    public void setFetchNum(Integer fetchNum) {
        this.fetchNum = fetchNum;
    }

    public Integer getBeginNum() {
        return beginNum;
    }

    public void setBeginNum(Integer beginNum) {
        this.beginNum = beginNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }


    //endregion


}
