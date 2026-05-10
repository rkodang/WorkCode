package com.gumdom.boot.infrastructure;

@FunctionalInterface
public interface TraceProcess {

    void process(TraceContext<?,?> context);
}
