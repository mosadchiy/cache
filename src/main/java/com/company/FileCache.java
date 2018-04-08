package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link ICache} based on hdd
 *
 * @param <K> the type of keys maintained by {@link HashMap}
 * @param <V> the type of mapped values
 * @see RAMCache
 * @see TwoLevelCache
 */
public class FileCache<K, V> implements ICache<K, V> {
    private final Path cachePath;
    private HashMap<K, Path> keyToPath;
    private IEviction<K> eviction;
    private int cacheCapacity;

    public FileCache(IEviction<K> eviction, int cacheCapacity, Path cachePath) {
        this.eviction = eviction;
        keyToPath = new HashMap<>(cacheCapacity, 1);
        this.cacheCapacity = cacheCapacity;
        this.cachePath = cachePath;
    }

    @Override
    public boolean isFull() {
        return keyToPath.size() == cacheCapacity;
    }

    @Override
    public int size() {
        return keyToPath.size();
    }

    @Override
    public Optional<V> get(K key) {
        eviction.updateKey(key);
        Path path = keyToPath.get(key);
        if (path == null || Files.notExists(path)) return Optional.empty();
        return Optional.ofNullable(readFile(path));
    }

    @Override
    public Optional<V> remove(K key) {
        Path path = keyToPath.get(key);
        eviction.removeKey(key);
        keyToPath.remove(key);
        if (path == null || Files.notExists(path)) return Optional.empty();
        return Optional.ofNullable(removeFile(path));
    }

    @Override
    public Map<K, V> put(K key, V value) {
        Map<K, V> evictedEntry = new HashMap<>();
        if (isFull()) evictedEntry = evict();
        Path generatedPath;
        if (keyToPath.get(key) != null) {
            generatedPath = keyToPath.get(key);
        } else {
            generatedPath = cachePath.resolve(UUID.randomUUID().toString());
        }
        eviction.setKey(key);
        keyToPath.put(key, generatedPath);
        writeFile(generatedPath, value);
        return evictedEntry;
    }

    @Override
    public Map<K, V> evict() {
        K expiredKey = eviction.getExpiredKey();
        return remove(expiredKey).map(v -> Map.of(expiredKey, v)).orElseGet(HashMap::new);
    }

    private synchronized void writeFile(Path path, V value) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException("Could not create file: " + path, e);
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(value);
        } catch (IOException e) {
            throw new RuntimeException("Could not write file: " + path, e);
        }
    }

    private synchronized V removeFile(Path path) {
        if (path == null) return null;
        V value = readFile(path);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + path, e);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private synchronized V readFile(Path path) {
        V value;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            value = (V) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Could not read file " + path, e);
        }
        return value;
    }
}
