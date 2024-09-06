package com.gumdom.boot.infrastructure;

import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;
import java.util.Iterator;

public class MyBatisCursorBlocker<Value> implements Cursor<Value> {
    private Cursor<Value> source;
    private int block;
    private Iterator<Value> iterator;
    private int nextCount = 0;
    private int last = -1;
    private int next = 0;

    public MyBatisCursorBlocker(Cursor<Value> source, int block) {
        this.source = source;
        this.block = block;
        this.iterator = source.iterator();
    }

    @Override
    public boolean isOpen() {
        return source.isOpen();
    }

    @Override
    public boolean isConsumed() {
        return source.isConsumed();
    }

    @Override
    public int getCurrentIndex() {
        return source.getCurrentIndex();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }

    public boolean hasNext(){
        if (last == next) {
            return false;
        }
        return iterator != null && iterator.hasNext();
    }

    @Override
    public Iterator<Value> iterator() {
        if (this.iterator == null) {
            this.iterator = source.iterator();
        }
        return new Iterator<Value>() {
            @Override
            public boolean hasNext() {
                if (block == nextCount) {
                    nextCount = 0;
                    last = -1;
                    next = 0;
                    return false;
                }
                last = next;
                return iterator.hasNext();

            }

            @Override
            public Value next() {
                Value next = iterator.next();
                nextCount++;
                return next;
            }
        };
    }
}
