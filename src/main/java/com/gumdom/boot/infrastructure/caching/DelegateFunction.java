package com.gumdom.boot.infrastructure.caching;

/**
 * 函数式接口 -> 转换类方法
 */
public interface DelegateFunction<T,R> {
    R apply(T t);
}
