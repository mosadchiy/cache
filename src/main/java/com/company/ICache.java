package com.company;

import java.util.Map;
import java.util.Optional;

/**
 * Key-value based {@link ICache}
 *
 * @param <K> the type of keys maintained by this {@link ICache}
 * @param <V> the type of mapped values
 * @see RAMCache
 * @see FileCache
 * @see TwoLevelCache
 */
public interface ICache<K, V> {
    /**
     * @return {@code true} if this {@link ICache} contains no free entries
     */
    boolean isFull();

    /**
     * @return the number of key-value entries in this {@link ICache}
     */
    int size();

    /**
     * Returns the value to which the specified key is mapped,
     * or {@link Optional#EMPTY} if this {@link ICache} contains no entry for the key.
     */
    Optional<V> get(K key);

    /**
     * @param key key whose entry is to be removed from the {@link ICache}
     * @return the previous value associated with {@code key}
     * or {@link Optional#EMPTY} if nothing was removed
     */
    Optional<V> remove(K key);

    /**
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return {@link Map} of evicted map entry
     */
    Map<K, V> put(K key, V value);

    /**
     * @return {@link Map} of evicted map entry
     */
    Map<K, V> evict();
}
