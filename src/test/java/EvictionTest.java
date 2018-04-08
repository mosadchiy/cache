import com.company.Eviction;
import com.company.EvictionDataFactory;
import com.company.IEviction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

class EvictionTest {
    private IEviction<Integer> eviction;
    private HashMap keyToEvictionData;

    @BeforeEach
    @SuppressWarnings("JavaReflectionMemberAccess")
    void init() throws NoSuchFieldException, IllegalAccessException {
        eviction = new Eviction<>(new EvictionDataFactory());
        Field keyToEvictionDataField = eviction.getClass().getDeclaredField("keyToEvictionData");
        keyToEvictionDataField.setAccessible(true);
        this.keyToEvictionData = (HashMap) keyToEvictionDataField.get(eviction);
    }

    @Test
    void setKeyTest() {
        eviction.setKey(1);
        Assertions.assertEquals(1, keyToEvictionData.size());
    }

    @Test
    void updateKeyTest() {
        eviction.setKey(1);
        eviction.setKey(2);
        eviction.setKey(3);
        eviction.updateKey(1);
        Assertions.assertEquals(Integer.valueOf(1), eviction.getExpiredKey());
    }

    @Test
    void removeKeyTest() {
        eviction.setKey(4);
        eviction.setKey(5);
        Assertions.assertEquals(2, keyToEvictionData.size());
        eviction.removeKey(5);
        Assertions.assertEquals(1, keyToEvictionData.size());
    }

    @Test
    void getExpiredKeyTest() {
        eviction.setKey(6);
        eviction.setKey(7);
        eviction.setKey(8);
        Assertions.assertEquals(Integer.valueOf(8), eviction.getExpiredKey());
    }
}
