package com.company;

/**
 * This interface manages process of eviction
 *
 * @param <K> the type of key
 */
public interface IEviction<K> {
    void setKey(K key);

    void updateKey(K key);

    void removeKey(K key);

    K getExpiredKey();
}
