package com.gumdom.boot.infrastructure.caching;

import com.gumdom.boot.infrastructure.ICaching;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象Map方法
 */
public abstract class AbstMapCache implements ICaching {

    protected class MapCacheItem {
        //存在时间
        public Long expireTime;
        //被缓存的对象
        public Object cacheObject;
        //是否永不过时
        public boolean neverExpired;
    }

    /**
     * 缓存对象
     */
    private Map<String, MapCacheItem> cache;

    private AbstMapCache() {
        this(1000);
    }

    protected AbstMapCache(int capacity) {
        this.cache = new ConcurrentHashMap<>(capacity);
    }

    protected AbstMapCache(Map<String, MapCacheItem> map) {
        this.cache = map;
    }

    //初始化
    protected static void initMap(AbstMapCache cached, Map<String, Object> map) {
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            cached.putItem(stringObjectEntry.getKey(), cached.newItem(stringObjectEntry.getValue(), 0));
        }
    }

    //存一下缓存内容
    protected AbstMapCache putItem(String key, MapCacheItem item) {
        this.cache.put(key, item);
        return this;
    }


    protected MapCacheItem newItem(Object value, int minute) {
        MapCacheItem item = new MapCacheItem();
        //缓存对象;
        item.cacheObject = value;
        if (minute <= 0) {
            item.expireTime = 0L;
            return item;
        }
        item.expireTime = System.currentTimeMillis() + (minute * 60 * 1000L);
        return item;
    }

    @Override
    public Object getValue(String key) {
        return this.getValue(key, null);
    }

    @Override
    public Object getValue(String key, DelegateFunction<String, Object> callBack) {
        return this.getValue(key, callBack, 3);
    }

    @Override
    public Object getValue(String key, DelegateFunction<String, Object> callBack, int minute) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        //[tips]缓存里面 先判断是否有key,再做一层判断,避免前一个拿了以后空针;
        if (this.cache.containsKey(key)) {
            MapCacheItem item = this.cache.get(key);
            if (item == null) {
                return null;
            }
            //[tips] 如果是永不过时的就可以返回缓存对象,如果是会过时的就要判断是否已经过时了;
            if (item.neverExpired || item.expireTime >= System.currentTimeMillis()) {
                return item.cacheObject;
            }
        }
        //****************************************************************************************

        //[tips]考虑到这里的执行速度会很慢,不建议加锁,假如不删除这个key,那么后面同样的key过来,得到也是过期的对象,那么多条线程同时执行callBack的时候,第二条线程会将第一条线程set的key值删除并替换成自己得到的内容
        Object value = callBack == null ? null : callBack.apply(key);
        if (value == null) {
            this.cache.remove(key);
            return null;
        }
        MapCacheItem item = this.newItem(value, minute);
        this.putItem(key, item);
        return value;
    }

    @Override
    public <Parameter> Object getValue2(String key, Parameter parameter, DelegateFunction2<String, Parameter, Object> callBack) {
        return this.getValue2(key, parameter, callBack, 3);
    }

    @Override
    public <Parameter> Object getValue2(String key, Parameter parameter, DelegateFunction2<String, Parameter, Object> callBack, int minute) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        //[tips]缓存里面 先判断是否有key,再做一层判断,避免前一个拿了以后空针;
        if (this.cache.containsKey(key)) {
            MapCacheItem item = this.cache.get(key);
            if (item == null) {
                return null;
            }
            //[tips] 如果是永不过时的就可以返回缓存对象,如果是会过时的就要判断是否已经过时了;
            if (item.neverExpired || item.expireTime >= System.currentTimeMillis()) {
                return item.cacheObject;
            }
        }
        //****************************************************************************************

        //[tips]考虑到这里的执行速度会很慢,不建议加锁,假如不删除这个key,那么后面同样的key过来,得到也是过期的对象,那么多条线程同时执行callBack的时候,第二条线程会将第一条线程set的key值删除并替换成自己得到的内容
        Object value = callBack == null ? null : callBack.apply(key, parameter);
        if (value == null) {
            this.cache.remove(key);
            return null;
        }
        MapCacheItem item = this.newItem(value, minute);
        this.putItem(key, item);
        return value;
    }

    @Override
    public <T> T getTypeValue(String key) {
        Object value = this.getValue(key);
        return (T) value;
    }


    @Override
    public <T> T getTypeValue(String key, DelegateFunction<String, Object> callBack) {
        Object value = this.getValue(key, callBack);
        return (T) value;
    }

    @Override
    public <T> T getTypeValue(String key, DelegateFunction<String, Object> callBack, int minute) {
        Object value = this.getValue(key, callBack, minute);
        return (T) value;
    }

    @Override
    public AbstMapCache setValue(String key, Object Value) {
        return (AbstMapCache) this.setValue(key, Value, 3);
    }

    @Override
    public AbstMapCache setValue(String key, Object Value, int minute) {
        if (this.trySetValue(key, Value, minute)) {
            return this;
        }
        return this;
    }

    @Override
    public Boolean trySetValue(String key, Object value) {
        return this.trySetValue(key, value, 3);
    }

    @Override
    public Boolean trySetValue(String key, Object value, int minute) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        if (value == null) {
            return false;
        }
        MapCacheItem item = this.newItem(value, minute <= 0 ? 3 : minute);
        this.putItem(key, item);
        return true;
    }

    @Override
    public AbstMapCache delete(String key) {
        if (StringUtils.isEmpty(key)) {
            return this;
        }
        this.cache.remove(key);
        return this;
    }

    @Override
    public Boolean tryDelete(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return this.cache.remove(key) != null;
    }

    @Override
    public Boolean tryValidate(String key, int timeOutMillis) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }

        if (this.cache.containsKey(key)) {
            MapCacheItem item = this.cache.get(key);
            if (item == null) {
                return false;
            }
            if (item.neverExpired) {
                return true;
            }
            return item.expireTime >= System.currentTimeMillis() + timeOutMillis;
        }

        return false;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>(this.cache.size());
        for (Map.Entry<String, MapCacheItem> keyValue : this.cache.entrySet()) {
            map.put(keyValue.getKey(),keyValue.getValue().cacheObject);
        }
        return map;
    }
}
