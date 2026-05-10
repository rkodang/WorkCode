package com.gumdom.boot.infrastructure;

@FunctionalInterface
public interface TraceErrHandler {

    void handle(TraceContext<?,?> context,Throwable e);
}
