package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link ICache} based on {@link HashMap}
 *
 * @param <K> the type of keys maintained by {@link HashMap}
 * @param <V> the type of mapped values
 * @see FileCache
 * @see TwoLevelCache
 */
public class RAMCache<K, V> implements ICache<K, V> {
    private HashMap<K, V> keyToValue;
    private IEviction<K> eviction;
    private int cacheCapacity;

    public RAMCache(IEviction<K> eviction, int cacheCapacity) {
        this.eviction = eviction;
        keyToValue = new HashMap<>(cacheCapacity, 1);
        this.cacheCapacity = cacheCapacity;
    }

    @Override
    public boolean isFull() {
        return keyToValue.size() == cacheCapacity;
    }

    @Override
    public int size() {
        return keyToValue.size();
    }

    @Override
    public Optional<V> get(K key) {
        eviction.updateKey(key);
        return Optional.ofNullable(keyToValue.get(key));
    }

    @Override
    public Optional<V> remove(K key) {
        eviction.removeKey(key);
        return Optional.ofNullable(keyToValue.remove(key));
    }

    @Override
    public Map<K, V> put(K key, V value) {
        Map<K, V> evictedEntry = new HashMap<>();
        if (isFull()) evictedEntry = evict();
        keyToValue.put(key, value);
        eviction.setKey(key);
        return evictedEntry;
    }

    @Override
    public Map<K, V> evict() {
        K expiredKey = eviction.getExpiredKey();
        return remove(expiredKey).map(v -> Map.of(expiredKey, v)).orElseGet(HashMap::new);
    }
}
