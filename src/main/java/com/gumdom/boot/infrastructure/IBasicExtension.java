package com.gumdom.boot.infrastructure;

import com.gumdom.boot.infrastructure.caching.DelegateAction2;
import com.gumdom.boot.infrastructure.caching.DelegateFunction;
import com.gumdom.boot.infrastructure.database.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    /**
     * 转成map
     */
    default Map<String, String> toMap(List<StringKeyValuePair> keyValuePairs) {
        if (keyValuePairs == null) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>(keyValuePairs.size());
        for (StringKeyValuePair keyValuePair : keyValuePairs) {
            map.put(keyValuePair.getKey(), keyValuePair.getValue());

        }
        return map;
    }

    /**
     * 转成Map
     */
    default <T> Map toMap(T value) {
        if (value == null) {
            return new HashMap();
        }
        return new HashMap(BeanMap.create(value));
    }

    /**
     * 返回失败Map
     */
    default StringKeyObjectValueMap failMap() {
        return this.failMap("");
    }

    default StringKeyObjectValueMap failMap(String message) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "0");
        if (StringUtils.isEmpty(message)) {
            return map;
        }
        map.put("retMsg", message);
        return map;
    }

    default StringKeyObjectValueMap failMap(List<StringKeyValuePair> keyValuePairs) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "0");
        if (keyValuePairs == null || keyValuePairs.size() <= 0) {
            return map;
        }
        map.put("retMsg", keyValuePairs.get(0).getValue());
        return map;
    }

    default StringKeyObjectValueMap failMap(String key, Object value) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "0");
        if (StringUtils.isEmpty(key)) {
            return map;
        }
        map.put(key, value);
        return map;
    }


    default StringKeyObjectValueMap succMap() {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "1");
        return map;
    }

    default StringKeyObjectValueMap succMap(String message) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "1");
        if (StringUtils.isEmpty(message)) {
            return map;
        }
        map.put("retMsg", message);
        return map;
    }

    default StringKeyObjectValueMap succMap(Object value) {

        return this.succMap("retData", value);
    }

    default StringKeyObjectValueMap succMap(String key, Object value) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "1");
        if (StringUtils.isEmpty(key)) {
            return map;
        }
        map.put(key, value);
        return map;
    }

    /**
     * 数据转换Page入口
     */
    default <E, V> StringKeyObjectValueMap succMapPage(Page<E> pages, Function<? super E, ? extends V> mapper) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(4);
        map.put("retCode", "1");
        Page<V> V = this.changePage(pages, mapper);
        map.put("retData", V);
        return map;
    }

    /**
     * 数据转换执行方法
     */
    default <E, V> Page<V> changePage(Page<E> pages, Function<? super E, ? extends V> mapper) {
        if (pages == null || mapper == null) {
            return new Page<>();
        }

        Page<V> data = new Page<>();
        data.setTotalNum(pages.getTotalNum());
        data.setBeginNum(pages.getBeginNum());
        data.setFetchNum(pages.getFetchNum());
        if (mapper != null) {
            data.setList(pages.getList().stream().map(mapper).collect(Collectors.toList()));
        }
        return data;
    }

    /**
     * list转成Map
     */
    default <E, V> StringKeyObjectValueMap succMapList(List<E> list, Function<? super E, ? extends V> mapper) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "1");
        List<V> V = this.changeList(list, mapper);
        map.put("retData", V);
        return map;
    }

    default <E, V> List<V> changeList(List<E> list, Function<? super E, ? extends V> mapper) {
        if (list == null || mapper == null) {
            return new ArrayList<>();
        }
        List<V> data = list.stream().map(mapper).collect(Collectors.toList());
        return data;
    }

    /**
     * 单一值转换;
     */
    default <E, V> StringKeyObjectValueMap succMapValue(E value, Function<? super E, ? extends V> mapper) {
        StringKeyObjectValueMap map = new StringKeyObjectValueMap(1);
        map.put("retCode", "1");
        V v = this.changeValue(value, mapper);
        map.put("retData", v);
        return map;
    }

    default <E, V> V changeValue(E value, Function<? super E, ? extends V> mapper) {
        if (value == null || mapper == null) {
            return null;
        }
        return mapper.apply(value);
    }

    default boolean isSucc(Map<String, Object> map) {
        return map != null && map.containsKey("retCode") && this.isEquals("1", map.get("retCode"));
    }

    /**
     * 是否相等的判断法则
     */
    default boolean isEquals(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return value1.equals(value2);
    }

    /**
     * 交个朋友
     * 取两个都有的
     */
    default <T> List<T> overlap(List<T> one, List<T> two) {
        if (this.isNullOrEmpty(one) || this.isNullOrEmpty(two)) {
            return one == null ? two : one;
        }
        List<T> list = new ArrayList<>(one.size() <= two.size() ? one.size() : two.size());
        Iterator<T> iterator = one.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (two.contains(next)) {
                list.add(next);
                continue;
            }
        }
        return list;
    }

    default boolean isNullOrEmpty(List<?> list) {
        if (list == null || list.size() == 0 || list.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 差个朋友
     * 只取two没有的东西呢
     */
    default <T> List<T> except(List<T> one, List<T> two) {
        if (this.isNullOrEmpty(one) || this.isNullOrEmpty(two)) {
            return one == null ? two : one;
        }
        List<T> list = new ArrayList<>(one.size());
        for (T v : one) {
            if (two.contains(v)) {
                continue;
            }
            list.add(v);
        }
        return list;
    }


    /**
     * 并个朋友
     * 两个合体(去重)
     */
    default <T> List<T> union(List<T> one, List<T> two) {
        return this.union(one, two, (o1, o2) -> o1 == o2);
    }

    default <T> List<T> union(List<T> one, List<T> two, IEquatabler<T, T> comparator) {
        List<T> nList = one == null ? new ArrayList<>() : one;
        List<T> mList = two == null ? new ArrayList<>() : two;
        List<T> target = new ArrayList<>(nList.size() + mList.size());
        for (T n : nList) {
            if (target.size() > 0 && target.stream().anyMatch(f -> comparator.isEquals(n, f))) {
                continue;
            }
            target.add(n);
        }
        for (T m : mList) {
            if (target.size() > 0 && target.stream().anyMatch(f -> comparator.isEquals(m, f))) {
                continue;
            }
            target.add(m);
        }
        return target;
    }

    /**
     * 并个朋友(重复)
     */
    default <T> List<T> unionAll(List<T> one, List<T> two) {
        List<T> nList = one == null ? new ArrayList<>() : one;
        List<T> mList = two == null ? new ArrayList<>() : two;
        List<T> target = new ArrayList<>(nList.size() + mList.size());
        target.addAll(nList);
        target.addAll(mList);
        return target;
    }
}
