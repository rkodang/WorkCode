package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.AbstMapCache;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

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

    public MapCached(Map<String,Object> map){
        super(map == null ? 0 : map.size());
        this.initMap(this,map);
    }

    public MapCached setValueNotExpired(String key,Object value){
        if (StringUtils.isEmpty(key)) {
            return this;
        }

        if (value == null) {
            return this;
        }

        //[tips]永不过时的缓存内容;慎用
        MapCacheItem item = this.newItem(value, 0);
        item.neverExpired = true;
        this.putItem(key,item);
        return this;
    }

}
