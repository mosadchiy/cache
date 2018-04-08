import com.company.Eviction;
import com.company.EvictionDataFactory;
import com.company.IEviction;
import com.company.RAMCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class RAMCacheTest {
    private static final int CACHE_CAPACITY = 10;
    private RAMCache<Integer, Integer> cache;

    @BeforeEach
    void init() {
        IEviction<Integer> eviction = new Eviction<>(new EvictionDataFactory());
        cache = new RAMCache<>(eviction, CACHE_CAPACITY);
    }

    @Test
    void getTest() {
        cache.put(1, 2);
        Assertions.assertEquals(Integer.valueOf(2), cache.get(1).orElse(null));
    }

    @Test
    void removeTest() {
        cache.put(1, 2);
        cache.put(3, 4);
        Assertions.assertEquals(2, cache.size());
        cache.remove(3);
        Assertions.assertAll(
                () -> Assertions.assertEquals(Optional.empty(), cache.get(2)),
                () -> Assertions.assertEquals(Integer.valueOf(2), cache.get(1).orElse(null)),
                () -> Assertions.assertEquals(1, cache.size())
        );
    }

    @Test
    void putTest() {
        for (int i = 0; i < 25; i++) {
            cache.put(i, i);
        }
        Assertions.assertAll(
                () -> Assertions.assertTrue(cache.isFull()),
                () -> Assertions.assertEquals(CACHE_CAPACITY, cache.size()));
    }

    @Test
    void evictTest() {
        cache.put(1, 2);
        cache.put(3, 4);
        cache.put(5, 6);
        cache.evict();
        Assertions.assertAll(
                () -> Assertions.assertEquals(Optional.empty(), cache.get(5)),
                () -> Assertions.assertEquals(2, cache.size()));
    }
}
