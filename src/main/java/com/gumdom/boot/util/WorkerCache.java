package com.gumdom.boot.util;


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



}
