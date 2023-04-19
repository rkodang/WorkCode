package com.gumdom.boot.infrastructure.caching;

/**
 * 函数式接口 -> 转换类方法
 */
public interface DelegateFunction2<T1,T2,R> {
    R apply(T1 t1,T2 t2);
}
