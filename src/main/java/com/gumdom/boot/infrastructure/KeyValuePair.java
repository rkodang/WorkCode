package com.gumdom.boot.infrastructure;

public class KeyValuePair<Key,Value> {

    //region
    private Key key;

    private Value value;
    //endregion

    //region get&set
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
    //endregion

    public KeyValuePair(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    public static <TKey,TValue> KeyValuePair<TKey,TValue> newInstance(TKey key,TValue value){
        return new KeyValuePair<TKey,TValue>(key,value);
    }

    @Override
    public String toString() {
        return "KeyValuePair{" +
                "key=" + key == null ? "" : key.toString() +
                ", value=" + value == null ? "" : value.toString() +
                '}';
    }


}
