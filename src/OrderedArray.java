import java.util.Arrays;

/**
 * 固定大小的数组，为避免泛型数组的错误
 * 内部由小到大排列
 */
@SuppressWarnings("unchecked")
public class OrderedArray {
    final private Integer [] elementData;
    /**
     * 固定大小
     */
    final private int size;
    /**
     * 存放元素数量
     */
    private int length;

    public OrderedArray(final int size) {
        this.size = size;
        elementData = new Integer[size];
    }

    public static void main(String[] args) {

    }

    /**
     * 在指定位置插入元素，后面的元素全部向后移动，超出部分被消失忽略
     *
     * @param item 插入元素
     */
    public void put(final Integer item) {
        if (length == size) { throw new IndexOutOfBoundsException(); }
        elementData[length++] = item;
        Arrays.sort(elementData, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return o1.compareTo(o2);
        });
    }

    public int length() {
        return length;
    }

    public void truncate(final int index) {
        for (int i = index; i < length; i++) {
            elementData[i] = null;
            length--;
        }
    }

    private void rangeCheckPut(final int index) {
        if (index > length || index < 0) { throw new IndexOutOfBoundsException(); }
    }

    public void replace(final int index, Integer item) {
        rangeCheck(index);
        elementData[index] = item;
    }

    private void rangeCheck(final int index) {
        if (index >= length || index < 0) { throw new IndexOutOfBoundsException(); }
    }

    /**
     * 在指定位置移除元素，其后元素向前位移
     *
     * @param index 删除元素的序号
     */
    public void removeAt(final int index) {
        rangeCheck(index);
        for (int i = index; i < size; i++) {
            elementData[i] = elementData[i + 1];
        }
    }

    public Integer get(final int index) {
        rangeCheck(index);
        //noinspection unchecked
        return (Integer) elementData[index];
    }

    /**
     * 搜索键，如果找到found为true，没找到返回由小变大的索引号
     * @param key
     *
     * @return
     */
    public SearchResult find(final Integer key) {
        int i = 0;
        // 找到第一个大于等于键值的位置
        while (i < length && elementData[i].compareTo(key) < 0) { i++; }
        if (i < length && elementData[i] == key) { return new SearchResult(i, true); }
        return new SearchResult(i, false);
    }

    static class SearchResult {
        private int position;
        private boolean found;

        SearchResult(final int position, final boolean found) {
            this.position = position;
            this.found = found;
        }

        public int getPosition() {
            return position;
        }

        public boolean isFound() {
            return found;
        }
    }
}
