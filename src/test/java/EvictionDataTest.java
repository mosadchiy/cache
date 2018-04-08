import com.company.EvictionData;
import com.company.IEvictionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EvictionDataTest {
    private IEvictionData evictionData;

    @BeforeEach
    void init() {
        evictionData = new EvictionData();
    }

    @Test
    @SuppressWarnings("unchecked")
    void compareToTest() {
        IEvictionData newEvictionData = new EvictionData();
        evictionData.update();
        Assertions.assertEquals(-1, evictionData.compareTo(newEvictionData));
    }

    @Test
    @SuppressWarnings("JavaReflectionMemberAccess")
    void updateTest() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Field timeUsed = evictionData.getClass().getDeclaredField("timeUsed");
        timeUsed.setAccessible(true);
        long timesUsedValue = (long) timeUsed.get(evictionData);
        evictionData.update();
        assertTrue(timesUsedValue < ((long) timeUsed.get(evictionData)));
    }
}
