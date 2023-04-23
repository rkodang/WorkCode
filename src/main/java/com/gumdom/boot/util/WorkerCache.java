package com.gumdom.boot.util;


import com.gumdom.boot.common.entity.DictInfo;
import com.gumdom.boot.infrastructure.MapCached;

import java.util.List;
import java.util.Map;

/**
 * 缓存工具人
 */
public class WorkerCache {

    /**
     * 单例模式
     */
    private static final WorkerCache workCacheManager = new WorkerCache();

    /**
     * 单例模式
     */
    public static WorkerCache getInstance(){
        return workCacheManager;
    }

    /**
     * 获取数据字典
     */
    public Map<String, Map<String, List<DictInfo>>> getDictData(){
        return MapCached.getInstance().getTypeValue("CacheManager.DictData");
    }

    /**
     * 设置数据字典
     */
    public void setDictData(Map<String,Map<String,List<DictInfo>>> dictData){
        MapCached.getInstance().setValueNotExpired("CacheManager.DictData",dictData);
    }

}
