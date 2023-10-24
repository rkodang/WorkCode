package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.AbstMapCache;

import java.util.Map;

/**
 * 数据没有时间缓存,可以当跑批的上下文,这样可以跟着线程结束而清理,也可以当永久缓存,程序不挂,数据不丢;
 */
public final class MapUnDeadCached extends AbstMapCache {
    private static final MapUnDeadCached instance = new MapUnDeadCached();

    public static MapUnDeadCached getInstance() {
        return instance;
    }

    private MapUnDeadCached() {
        this(1000);
    }

    private MapUnDeadCached(int capacity) {
        super(capacity);
    }

    public MapUnDeadCached(Map<String, Object> map) {
        super(map == null ? 0 : map.size());
        initMap(this, map);
    }

    @Override
    protected MapCacheItem newItem(Object value, int minute) {
        MapCacheItem item = super.newItem(value, minute);
        item.neverExpired = true;
        return item;
    }
}
