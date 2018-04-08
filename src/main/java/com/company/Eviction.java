package com.company;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link IEviction} based on comparing {@link IEvictionData}
 *
 * @param <K> the type of key
 */
public class Eviction<K> implements IEviction<K> {
    private Map<K, IEvictionData> keyToEvictionData;
    private IEvictionDataFactory iEvictionDataFactory;

    public Eviction(IEvictionDataFactory iEvictionDataFactory) {
        keyToEvictionData = new HashMap<>();
        this.iEvictionDataFactory = iEvictionDataFactory;
    }

    @Override
    public void setKey(K key) {
        if (keyToEvictionData.containsKey(key)) {
            updateKey(key);
        } else {
            keyToEvictionData.put(key, iEvictionDataFactory.createEvictionData());
        }
    }

    @Override
    public void updateKey(K key) {
        Optional.ofNullable(keyToEvictionData.get(key)).ifPresent(IEvictionData::update);
    }

    @Override
    public void removeKey(K key) {
        keyToEvictionData.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public K getExpiredKey() {
        Comparator<Map.Entry<K, IEvictionData>> comparator = Comparator.comparing(Map.Entry::getValue);
        return keyToEvictionData.entrySet().stream()
                .min(comparator)
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("Key map is empty"));
    }
}
