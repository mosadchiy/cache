import com.company.Eviction;
import com.company.EvictionDataFactory;
import com.company.FileCache;
import com.company.IEviction;
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

class FileCacheTest {
    private static final int CACHE_CAPACITY = 10;
    private FileCache<Integer, Integer> cache;
    private Path cachePath = Paths.get("src/test/resources").toAbsolutePath();

    @BeforeEach
    void init() {
        IEviction<Integer> eviction = new Eviction<>(new EvictionDataFactory());
        cache = new FileCache<>(eviction, CACHE_CAPACITY, cachePath);
    }

    @Test
    void getTest() {
        cache.put(1, 2);
        Assertions.assertEquals(Integer.valueOf(2), cache.get(1).orElse(null));
    }

    @Test
    void removeTest() {
        cache.put(3, 4);
        cache.put(5, 6);
        cache.put(7, 8);
        Assertions.assertEquals(3, cache.size());
        cache.remove(7);
        Assertions.assertAll(
                () -> Assertions.assertEquals(Optional.empty(), cache.get(7)),
                () -> Assertions.assertEquals(Integer.valueOf(6), cache.get(5).orElse(null)),
                () -> Assertions.assertEquals(2, cache.size())
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
        cache.put(7, 8);
        cache.put(9, 10);
        cache.evict();
        Assertions.assertAll(
                () -> Assertions.assertEquals(Optional.empty(), cache.get(9)),
                () -> Assertions.assertEquals(2, cache.size()));
    }

    @AfterEach
    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    void after() throws IOException {
        Arrays.stream(new File(cachePath.toUri()).listFiles()).forEach(File::delete);
    }
}
