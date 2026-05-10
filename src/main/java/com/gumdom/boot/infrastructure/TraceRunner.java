package com.gumdom.boot.infrastructure;

public class TraceRunner {

    private TraceRunner(){

    }


    public static <T,R> R excute(String traceName,TraceContext<T,R> context){
        TraceFlow traceFlow = (TraceFlow) ApplicationContextUtil.getBeanByName(traceName);
        traceFlow.execute(context);
        return context.getRspMsg();
    }

    public static <T,R> R excute(TraceFlow traceFlow,TraceContext<T,R> context){
        traceFlow.execute(context);
        return context.getRspMsg();
    }
}
