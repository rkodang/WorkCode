package com.gumdom.boot.infrastructure;

public interface IEquatabler<T1,T2> {
    boolean isEquals(T1 one,T2 two);
}
