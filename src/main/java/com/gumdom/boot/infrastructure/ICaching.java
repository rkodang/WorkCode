package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.DelegateFunction;
import com.gumdom.boot.infrastructure.caching.DelegateFunction2;

/**
 * 缓存接口
 * Log:
 * 1.一般用于当数据字典
 */
public interface ICaching {

    /**
     * 获取字典值
     */
    default Object getValue(String key){
        return this.getValue(key,null);
    }

    Object getValue(String key, DelegateFunction<String,Object> callBack);


    Object getValue(String key, DelegateFunction<String,Object> callBack,int minute);

    <P>Object getValue2(String key, P parameter, DelegateFunction2<String,P,Object> callBack);

    <P>Object getValue2(String key, P parameter, DelegateFunction2<String,P,Object> callBack, int minute);

    default <T> T getTypeValue(String key){
        return this.getTypeValue(key,null);
    }

    <T> T getTypeValue(String key, DelegateFunction<String,Object> callBack);

    <T> T getTypeValue(String key, DelegateFunction<String,Object> callBack, int minute);

    ICaching setValue(String key,Object Value);

    ICaching setValue(String key,Object Value,int minute);

    Boolean trySetValue(String key,Object value);

    Boolean trySetValue(String key,Object value,int minute);

    ICaching delete (String key);

    Boolean tryDelete(String key);

    Boolean tryValidate(String key,int timeOutMillis);






}
