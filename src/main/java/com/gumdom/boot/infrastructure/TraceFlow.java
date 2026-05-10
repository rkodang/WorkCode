package com.gumdom.boot.infrastructure;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.List;

public class TraceFlow {


    private List<TraceProcess> traceProcessList;

    private TraceErrHandler traceErrHandler;


    public TraceFlow(List<TraceProcess> traceProcessList) {
        this.traceProcessList = traceProcessList;
    }


    public void execute(TraceContext<?, ?> context) {
        try {
            for (TraceProcess traceProcess : traceProcessList) {
                String methodName = getMehodName(traceProcess);
                traceProcess.process(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (traceErrHandler != null) {
                traceErrHandler.handle(context,e);
            }
        }


    }

    public String getMehodName(TraceProcess traceProcess) {
        Method writeReplace = null;
        try {
            writeReplace = traceProcess.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(traceProcess);
            return serializedLambda.getImplMethodName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";


    }

}
