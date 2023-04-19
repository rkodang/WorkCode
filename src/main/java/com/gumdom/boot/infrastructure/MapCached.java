package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.AbstMapCache;

/**
 * 缓存打工人
 */
public final class MapCached extends AbstMapCache {

    private static final MapCached instance = new MapCached();

    public  static MapCached getInstance(){
        return instance;
    }

    private MapCached(){
        this(1000);
    }

    public MapCached(int capacity){
        super(capacity);
    }
}
