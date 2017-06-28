import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparableArrayTest extends ListArrayTest {
    @Test
    void find() {
        ComparableArray<Integer> array = new ComparableArray<>(10);
        for (int i = 0; i < SIZE; i += 2) {
            array.append(i);
        }
        Array.SearchResult r1 = array.find(5);
        assertFalse(r1.isFound());
        assertEquals(3, r1.getPosition());
        Array.SearchResult r2 = array.find(6);
        assertTrue(r2.isFound());
        assertEquals(3, r2.getPosition());
    }

}
