package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.DelegateFunction;
import org.apache.ibatis.cursor.Cursor;

import java.io.IOException;
import java.util.Iterator;

public class MyBatisCursorChanger<Source,Value> implements Cursor<Value> {

    private Cursor<Source> source;
    private DelegateFunction<Source,Value> changeFunction;
    private Iterator<Source> iterator;

    public MyBatisCursorChanger(Cursor<Source> source, DelegateFunction<Source, Value> changeFunction) {
        this.source = source;
        this.changeFunction = changeFunction;
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

    @Override
    public Iterator<Value> iterator() {
        if (this.iterator == null) {
            this.iterator = this.source.iterator();
        }
        return new Iterator<Value>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Value next() {
                Source next = iterator.next();
                return changeFunction.apply(next);
            }
        };
    }
}
