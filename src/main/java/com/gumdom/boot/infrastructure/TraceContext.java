package com.gumdom.boot.infrastructure;

public class
TraceContext<T,R> {

    private T reqMsg;

    private R rspMsg;


    public T getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(T reqMsg) {
        this.reqMsg = reqMsg;
    }

    public R getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(R rspMsg) {
        this.rspMsg = rspMsg;
    }

    @Override
    public String toString() {
        return "TraceContext{" +
                "reqMsg=" + reqMsg +
                ", rspMsg=" + rspMsg +
                '}';
    }
}
