package com.vae1970.tool.util;

import lombok.AllArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * @author dongzhou.gu
 * @date 2019/11/20
 */
@AllArgsConstructor
public class ConcurrentObject<T> {

    private T data;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public <V> V get(Function<T, V> function) {
        r.lock();
        try {
            return function.apply(data);
        } finally {
            r.unlock();
        }
    }

    public <V> V set(Function<T, V> function) {
        w.lock();
        try {
            return function.apply(data);
        } finally {
            w.unlock();
        }
    }

}
