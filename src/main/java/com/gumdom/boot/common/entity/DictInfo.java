package com.gumdom.boot.common.entity;

/**
 * 数据字典
 */
public class DictInfo {

    //region 属性

    //字典类型
    private String dictType;
    //字典代码
    private String dictCode;
    //字典值
    private String dictValue;
    //默认值
    private String defaultValue;
    //排序序号
    private Integer orderSeq;

    //endregion


    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }
}
