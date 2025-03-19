package com.gumdom.boot.infrastructure;

/**
 * 数据过滤器
 * @param <T>
 */
public interface ExcelFilter<T> {
    boolean accept(T t, int rowAt);
}
