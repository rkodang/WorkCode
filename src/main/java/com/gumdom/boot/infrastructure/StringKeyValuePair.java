package com.gumdom.boot.infrastructure;

import java.util.List;
import java.util.Optional;

public class StringKeyValuePair extends KeyValuePair<String,String> implements IEquatable<StringKeyValuePair> {

    public StringKeyValuePair() {
        super("", "");
    }

    public static StringKeyValuePair newInstance(String key,String value){
        StringKeyValuePair keyValuePair = new StringKeyValuePair();
        keyValuePair.setKey(key);
        keyValuePair.setValue(value);
        return keyValuePair;
    }

    @Override
    public boolean isEquals(StringKeyValuePair other) {
        return other != null && (this.getKey() == null ? "" : this.getKey()).equalsIgnoreCase(other.getKey()) && (this.getValue() == null ? "" : this.getValue()).equalsIgnoreCase(other.getValue());
    }


    public static IEquatabler<StringKeyValuePair,StringKeyValuePair> newKeyEquatabler(){
        return new KeyEquatabler();
    }

    public static IEquatabler<StringKeyValuePair,StringKeyValuePair> newKeyValueEquatabler(){
        return new KeyValueEquatabler();
    }


    /**
     * key对比器
     */
    private final static class KeyEquatabler implements IEquatabler<StringKeyValuePair,StringKeyValuePair>{

        @Override
        public boolean isEquals(StringKeyValuePair one, StringKeyValuePair two) {
            return one != null && (one.getKey() == null ? "" : one.getKey()).equalsIgnoreCase(two.getKey());
        }
    }

    /**
     * key对比器
     */
    private final static class KeyValueEquatabler implements IEquatabler<StringKeyValuePair,StringKeyValuePair>{

        @Override
        public boolean isEquals(StringKeyValuePair one, StringKeyValuePair two) {
            return one.isEquals(two);
        }
    }

    public static StringKeyValuePair firstPair(List<StringKeyValuePair> stringKeyValuePairList, String key){
        Optional<StringKeyValuePair> optional = stringKeyValuePairList.stream().filter(t -> key != null && key.equalsIgnoreCase(t.getKey())).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }


    public static StringKeyValuePair firstPair(List<StringKeyValuePair> stringKeyValuePairList, String key,String value){
        Optional<StringKeyValuePair> optional = stringKeyValuePairList.stream().filter(t -> key != null && key.equalsIgnoreCase(t.getKey()) && value != null && value.equalsIgnoreCase(t.getValue())).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }



}
