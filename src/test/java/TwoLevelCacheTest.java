import com.company.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

class TwoLevelCacheTest {
    private static final int CACHE_CAPACITY = 10;
    private TwoLevelCache<Integer, Integer> cache;
    private Path cachePath = Paths.get("src/test/resources").toAbsolutePath();

    @BeforeEach
    void init() {
        IEviction<Integer> ramEviction = new Eviction<>(new EvictionDataFactory());
        ICache<Integer, Integer> firstLevelCache = new RAMCache<>(ramEviction, CACHE_CAPACITY);
        IEviction<Integer> fileEviction = new Eviction<>(new EvictionDataFactory());
        ICache<Integer, Integer> secondLevelCache = new FileCache<>(fileEviction, CACHE_CAPACITY, cachePath);
        cache = new TwoLevelCache<>(firstLevelCache, secondLevelCache);
    }

    @Test
    void getTest() {
        cache.put(1, 2);
        Assertions.assertEquals(Integer.valueOf(2), cache.get(1).orElse(null));
    }

    @Test
    void removeTest() {
        cache.put(7, 8);
        cache.put(3, 4);
        Assertions.assertEquals(2, cache.size());
        cache.remove(3);
        Assertions.assertAll(
                () -> Assertions.assertEquals(Optional.empty(), cache.get(2)),
                () -> Assertions.assertEquals(Integer.valueOf(8), cache.get(7).orElse(null)),
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
                () -> Assertions.assertEquals(CACHE_CAPACITY * 2, cache.size()));
    }

    @Test
    void evictTest() {
        cache.put(7, 8);
        cache.put(3, 4);
        cache.put(5, 6);
        cache.evict();
        Assertions.assertEquals(2, cache.size());
        for (int i = 10; i < 30; i++) {
            cache.put(i, i);
        }
        cache.evict();
        Assertions.assertEquals(CACHE_CAPACITY * 2 - 1, cache.size());
    }

    @AfterEach
    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    void after() throws IOException {
        Arrays.stream(new File(cachePath.toUri()).listFiles()).forEach(File::delete);
    }
}
