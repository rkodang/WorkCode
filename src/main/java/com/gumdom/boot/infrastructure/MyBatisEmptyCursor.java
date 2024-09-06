package com.gumdom.boot.infrastructure;

import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;
import java.util.Iterator;

public class MyBatisEmptyCursor<Value> implements Cursor<Value> {
    public MyBatisEmptyCursor(){
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isConsumed() {
        return false;
    }

    @Override
    public int getCurrentIndex() {
        return -1;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Value next() {
                return null;
            }
        };
    }
}
