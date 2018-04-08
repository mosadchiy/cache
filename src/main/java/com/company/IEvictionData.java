package com.company;

/**
 * This interface based on {@link Comparable} supports eviction
 */
public interface IEvictionData<T> extends Comparable<T> {
    void update();
}
