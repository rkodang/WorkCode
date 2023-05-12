package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.DelegateAction2;
import com.gumdom.boot.infrastructure.caching.DelegateFunction;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.*;
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
    default void runTaskAsync(boolean waitToExecuted, Runnable runnable) {
        if (waitToExecuted == false) {
            this.runTaskAsync(runnable);
            return;
        }
        this.runTaskAsync(Arrays.asList(1), 1, f -> {
            runnable.run();
            return f;
        });
    }

    /**
     * 并行执行(带返回值)
     * Log:
     * 1.传入V类型,返回V类型
     */
    default <V> V runTaskAsync(DelegateFunction<List<Integer>, List<V>> delegateCallBack) {
        List<V> values = this.runTaskAsync(Arrays.asList(0), 1, delegateCallBack);
        return values == null ? null : values.get(0);
    }

    /**
     * 并行执行(带返回值)[中转方法]
     * Log:
     * 1.传入T类型参数和 V类型返回值(返回实体),返回V类型;
     */
    default <T, V> List<V> runTaskAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack) {
        return this.runTaskAsync(targetList, threadNum, delegateCallBack, null);
    }

    /**
     * 并行执行(带返回值)[最终执行方法]
     */
    default <T, V> List<V> runTaskAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack, DelegateAction2<List<T>, List<V>> thenAcceptAction) {
        List<CompletableFuture<List<V>>> futures = this.runFutureTaskOnAsync(targetList, threadNum, delegateCallBack, thenAcceptAction);
        return this.completableFutureFinish(futures);
    }

    /**
     * 并行执行(带返回值)[传入的值是Integer]
     */
    default <V> CompletableFuture<V> runFutureTaskOnAsync(DelegateFunction<List<Integer>, V> delegateCallBack) {
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
     * 并行执行(中转方法)
     */
    default <T, V> List<CompletableFuture<List<V>>> runFutureTaskOnAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack) {
        return this.runFutureTaskOnAsync(targetList, threadNum, delegateCallBack, null);
    }

    /**
     * 并行执行(最终执行方法)
     */
    default <T, V> List<CompletableFuture<List<V>>> runFutureTaskOnAsync(List<T> targetList, int threadNum, DelegateFunction<List<T>, List<V>> delegateCallBack, DelegateAction2<List<T>, List<V>> thenAcceptAction) {
        List<CompletableFuture<List<V>>> futures = new ArrayList<>(threadNum);
        //根据线程数量进行任务的切分,如20个任务分给5个线程,则每条线程处理4个任务;
        List<List<T>> splits = this.arrayFixedSplit(targetList, threadNum);
        for (int i = 0; i < threadNum; i++) {
            List<T> split = splits.get(i);
            Supplier<List<V>> supplier = new Supplier<List<V>>() {
                @Override
                public List<V> get() {
                    return delegateCallBack.apply(split);
                }
            };
            //异步执行;
            CompletableFuture<List<V>> completableFuture = CompletableFuture.supplyAsync(supplier);
            //thenAcceptAction,是否有需要后置执行的方法;
            if (thenAcceptAction != null) {
                completableFuture.thenAccept(keyValue -> {
                    thenAcceptAction.apply(split, keyValue);
                });
            }
            futures.add(completableFuture);
        }
        return futures;
    }

    /**
     * [中转方法]传入future,获取对应返回值;
     */
    default <V> V completableFutureFinish(CompletableFuture<List<V>> future) {
        List<V> vs = this.completableFutureFinish(Arrays.asList(future));
        return vs == null ? null : vs.get(0);
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
     * 并返回对应返回值
     */
    default <V> List<V> completableFutureFinish(List<CompletableFuture<List<V>>> futures) {
        List<V> vArrayList = new ArrayList<>();
        CompletableFuture[] arrays = new CompletableFuture[futures.size()];
        CompletableFuture.allOf(futures.toArray(arrays));
        for (CompletableFuture<List<V>> future : futures) {
            if (future == null) {
                continue;
            }
            try {
                List<V> temp = future.get();
                if (temp != null && temp.size() > 0) {
                    vArrayList.addAll(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vArrayList;
    }

    /**
     * 带校验方式的简易复制~
     */
    default <S, T> T copyMembers(S source, T target) {
        if (source == null || target == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 带方法的简易复制;
     */
    default <S, T> T copyMembers(S source, T target, DelegateAction2<S, T> callBack) {
        if (source == null || target == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target);
        if (callBack != null) {
            callBack.apply(source, target);
        }
        return target;
    }

    /**
     * 找到第一个不为空的对象~
     */
    default <T> T selectNotNull(T... values) {
        if (values == null) {

        }

        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    //TODO 有个StringKeyValue

    /**
     * 转成Map
     */
    default <T> Map toMap(T value){
        if (value == null) {
            return new HashMap();
        }
        return new HashMap(BeanMap.create(value));
    }

}
