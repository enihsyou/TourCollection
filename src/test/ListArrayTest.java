import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListArrayTest {
    final static int SIZE = 10;
    private ListArray<Integer> array;

    @BeforeEach
    void setUp() {
        array = new ListArray<>(SIZE);
    }

    @Test
    void length() {
        /*空列表 0个元素*/
        assertEquals(0, array.length());
        /*插入一个后 1个元素*/
        array.append(0);
        assertEquals(1, array.length());
        /*连续插入 长度递增*/
        for (int i = 1; i < SIZE; i++) {
            array.append(i);
            assertEquals(i + 1, array.length());
        }
        assertEquals(SIZE, array.length());
        for (int i = SIZE; i > 0; i--) {
            array.popLast();
            assertEquals(i - 1, array.length());
        }
        assertEquals(0, array.length());
    }

    @Test
    void get() {
        for (int i = 0; i < SIZE; i++) {
            array.append(i);
            assertEquals(i, array.get(i).intValue());
        }
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(SIZE + 1));
    }


    @Test
    void insertAt() {
        array.insertAt(0, 0);
        assertEquals(0, array.get(0).intValue());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(1));

        array.insertAt(0, 1);
        assertEquals(1, array.get(0).intValue());
        assertEquals(0, array.get(1).intValue());

        for (int i = 2; i < SIZE; i++) {
            array.insertAt(0, i);
            assertEquals(i, array.get(0).intValue());
        }
    }

    @Test
    void truncate() {
        for (int i = 0; i < SIZE; i++) {
            array.append(i);
        }
        array.truncate(1);
        assertEquals(0, array.get(0).intValue());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> array.get(1));
    }

    @Test
    void popFirst() {
        for (int i = 0; i < SIZE; i++) {
            array.append(i);
        }
        for (int i = 0; i < SIZE; i++) {
            assertEquals(i, array.popFirst().intValue());
        }
    }

    @Test
    void popLast() {
        for (int i = 0; i < SIZE; i++) {
            array.append(i);
        }
        for (int i = SIZE - 1; i > 0; i--) {
            assertEquals(i, array.popLast().intValue());
        }
    }

    @Test
    void find() {
        for (int i = 0; i < SIZE; i += 2) {
            array.append(i);
        }
        Array.FindResult r1 = array.find(5);
        assertFalse(r1.isFound());
        assertEquals(-1, r1.getPosition());
        Array.FindResult r2 = array.find(6);
        assertTrue(r2.isFound());
        assertEquals(3 ,r2.getPosition());
    }
}
