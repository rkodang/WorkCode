package com.gumdom.boot.infrastructure.caching;

public interface DelegateAction2<T1,T2> {

    void apply(T1 t1,T2 t2);
}
