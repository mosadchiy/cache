package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Two-level implementation of {@link ICache}
 *
 * @param <K> the type of keys maintained by {@link HashMap}
 * @param <V> the type of mapped values
 * @see RAMCache
 * @see FileCache
 */
public class TwoLevelCache<K, V> implements ICache<K, V> {
    private ICache<K, V> firstLevelCache;
    private ICache<K, V> secondLevelCache;

    public TwoLevelCache(ICache<K, V> firstLevelCache, ICache<K, V> secondLevelCache) {
        this.firstLevelCache = firstLevelCache;
        this.secondLevelCache = secondLevelCache;
    }

    @Override
    public boolean isFull() {
        return firstLevelCache.isFull() && secondLevelCache.isFull();
    }

    @Override
    public int size() {
        return firstLevelCache.size() + secondLevelCache.size();
    }

    @Override
    public Optional<V> get(K key) {
        Optional<V> takenValue = firstLevelCache.get(key);
        if (takenValue.isPresent()) return takenValue;
        return secondLevelCache.get(key);
    }

    @Override
    public Optional<V> remove(K key) {
        Optional<V> removedValue = firstLevelCache.remove(key);
        if (removedValue.isPresent()) return removedValue;
        return secondLevelCache.remove(key);
    }

    @Override
    public Map<K, V> put(K key, V value) {
        Map<K, V> evictedFirstLevelEntry = firstLevelCache.put(key, value);
        if (evictedFirstLevelEntry.isEmpty()) return evictedFirstLevelEntry;
        Map<K, V> evictedSecondLevelEntry = new HashMap<>();
        evictedFirstLevelEntry.forEach((k, v) -> evictedSecondLevelEntry.putAll(secondLevelCache.put(k, v)));
        return evictedSecondLevelEntry;
    }

    @Override
    public Map<K, V> evict() {
        if (secondLevelCache.size() != 0) return secondLevelCache.evict();
        return firstLevelCache.evict();
    }
}
