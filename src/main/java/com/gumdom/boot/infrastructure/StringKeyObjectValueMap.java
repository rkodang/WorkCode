package com.gumdom.boot.infrastructure;

import java.util.HashMap;
import java.util.Map;

public class StringKeyObjectValueMap extends HashMap<String,Object> {
    private static final long serialVersionUID = 1234567890L;

    public StringKeyObjectValueMap() {
        super();
    }

    public StringKeyObjectValueMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringKeyObjectValueMap(int initialCapacity,float ladFactor) {
        super(initialCapacity,ladFactor);
    }


    public StringKeyObjectValueMap(Map<String,Object> m) {
        super(m);
    }

    public StringKeyObjectValueMap(String key,Object value){
        super(1);
        super.put(key,value);
    }

    public StringKeyObjectValueMap append(String key,Object value){
        if (key == null || "".equals(key)) {
            return this;
        }
        super.put(key, value);
        return this;
    }

    public StringKeyObjectValueMap append(String key,Object value,boolean ignoreNullValue){
        if (key == null || "".equals(key)) {
            return this;
        }
        if (value == null && ignoreNullValue) {
            return this;
        }
        super.put(key, value);
        return this;
    }


}
