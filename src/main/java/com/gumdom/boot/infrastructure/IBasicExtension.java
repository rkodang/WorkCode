package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.DelegateAction2;
import com.gumdom.boot.infrastructure.caching.DelegateFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IBasicExtension {

    /**
     * 并行执行(没有任何返回值)
     */
    default void runTaskAsync(Runnable runnable) {
        this.runFutureTaskOnAsync(Arrays.asList(1), 1, f -> {
            runnable.run();
            return f;
        });
    }

    /**
     * 并行执行(是否需要等待执行)
     */
    default void runTaskAsync(boolean waitToExecuted,Runnable runnable){
        if (waitToExecuted == false) {
            this.runTaskAsync(runnable);
            return;
        }
        this.runTaskAsync(Arrays.asList(1),1,f->{
            runnable.run();
            return f;
        });
    }

    /**
     *并行执行(带返回值)
     * Log:
     * 1.传入V类型,返回V类型
     */
    default <V> V runTaskAsync(DelegateFunction<List<Integer>,List<V>> delegateCallBack){
        List<V> values = this.runTaskAsync(Arrays.asList(0),1,delegateCallBack);
        return values == null ? null : values.get(0);
    }

    /**
     * 并行执行(带返回值)[中转方法]
     * Log:
     * 1.传入T类型参数和 V类型返回值(返回实体),返回V类型;
     */
    default <T,V> List<V> runTaskAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack){
        return this.runTaskAsync(targetList,threadNum,delegateCallBack,null);
    }

    /**
     * 并行执行(带返回值)[最终执行方法]
     */
    default <T, V> List<V> runTaskAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack, DelegateAction2<List<T>, List<V>> thenAcceptAction) {
        List<CompletableFuture<List<V>>> futures = this.runFutureTaskOnAsync(targetList, threadNum, delegateCallBack, thenAcceptAction);
        return this.completableFutureFinish(futures);
    }

    default <V> CompletableFuture<V> runFutureTaskOnAsync(DelegateFunction<List<Integer>,V> delegateCallBack){
       List<CompletableFuture<List<V>>> futures = new ArrayList<>(1);
        Supplier<V> supplier = new Supplier<V>() {
            @Override
            public V get() {
                return delegateCallBack.apply(Arrays.asList(0));
            }
        };
        return CompletableFuture.supplyAsync(supplier);
    }

    /**
     * 并行执行
     */
    default <T, V> List<CompletableFuture<List<V>>> runFutureTaskOnAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack) {
        return this.runFutureTaskOnAsync(targetList, threadNum, delegateCallBack, null);
    }

    /**
     * 并行执行
     */
    default <T, V> List<CompletableFuture<List<V>>> runFutureTaskOnAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack, DelegateAction2<List<T>, List<V>> thenAcceptAction) {
        List<CompletableFuture<List<V>>> futures = new ArrayList<>(threadNum);
        List<List<T>> splits = this.arrayFixedSplit(targetList, threadNum);
        for (int i = 0; i < threadNum; i++) {
            List<T> split = splits.get(i);
            Supplier<List<V>> supplier = new Supplier<List<V>>(){
                @Override
                public List<V> get() {
                    return delegateCallBack.apply(split);
                }
            };

            CompletableFuture<List<V>> completableFuture = CompletableFuture.supplyAsync(supplier);
            if (thenAcceptAction !=null) {
                completableFuture.thenAccept(keyValue ->{
                    thenAcceptAction.apply(split,keyValue);
                });
            }
            futures.add(completableFuture);
        }
        return futures;
    }







    /**
     * 根据线程数分割每个线程获取到执行任务;
     */
    default <T> List<List<T>> arrayFixedSplit(List<T> source, int fixedNum) {
        if (source == null || source.size() == 0) {
            return new ArrayList<>(new ArrayList<>(0));
        }

        if (fixedNum <= 0) {
            List<List<T>> list = new ArrayList<>(1);
            list.add(new ArrayList<>(source));
            return list;
        }

        List<List<T>> list = new ArrayList<>(fixedNum);
        if (source.size() <= fixedNum) {
            for (int i = 0; i < source.size(); i++) {
                list.add(new ArrayList<>(Arrays.asList(source.get(i))));
            }
            for (int i = source.size(); i < fixedNum; i++) {
                list.add(new ArrayList<>());
            }
            return list;
        }

        int count = source.size() / fixedNum;
        if (source.size() % fixedNum == 0) {
            for (int i = 0; i < fixedNum; i++) {
                list.add(new ArrayList<>(source.subList(i * count, (i + 1) * count)));
            }
        } else {
            for (int i = 0; i < fixedNum - 1; i++) {
                list.add(new ArrayList<>(source.subList(i * count, (i + 1) * count)));
            }
            list.add(new ArrayList<>(source.subList(count * (fixedNum - 1), source.size())));
        }
        return list;
    }

    /**
     * 实际异步执行方法;
     */
    default <V> List<V> completableFutureFinish(List<CompletableFuture<List<V>>> futures){
        List<V> vArrayList = new ArrayList<>();
        CompletableFuture[] arrays = new CompletableFuture[futures.size()];
        CompletableFuture.allOf(futures.toArray(arrays));
        for (CompletableFuture<List<V>> future : futures) {
            if (future == null) {
                continue;
            }
            try {
                List<V> temp = future.get();
                if (temp !=null && temp.size()>0) {
                    vArrayList.addAll(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vArrayList;
    }

}
