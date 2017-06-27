/**
 * 固定大小的数组，为避免泛型数组的错误
 * 内部由小到大排列
 *
 * @param <T> 存储类型
 */
@SuppressWarnings("unchecked")
public class Array<T extends Comparable> {
    final private Comparable[] elementData;
    /**
     * 固定大小
     */
    final private int size;
    /**
     * 存放元素数量
     */
    private int length;

    public Array(final int size) {
        this.size = size;
        elementData = new Comparable[size];
    }

    public int length() {
        return length;
    }

    public T get(final int index) {
        rangeCheck(index);
        //noinspection unchecked
        return (T) elementData[index];
    }

    private void rangeCheck(final int index) {
        if (index >= length || index < 0) { throw new IndexOutOfBoundsException(); }
    }
public void insertAt(final int index, final T item){
    for (int i = size - 1; i >index; i--) {
        elementData[i] = elementData[i - 1];
    }elementData[index] = item;
    length++;
}
    public void truncate(final int index) {
        for (int i = index; i < length; i++) {
            elementData[i] = null;
            length--;
        }
    }

    /**
     * 在指定位置插入元素，后面的元素全部向后移动，超出部分被消失忽略
     *
     * @param item 插入元素
     */
    public void put(final T item) {
        if (length == size) { throw new IndexOutOfBoundsException(); }
        elementData[length++] = item;
    }

    private void rangeCheckPut(final int index) {
        if (index > length || index < 0) { throw new IndexOutOfBoundsException(); }
    }

    public void replace(final int index, T item) {
        rangeCheck(index);
        elementData[index] = item;
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

    public SearchResult find(final T key) {
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

    public static void main(String[] args) {

    }
}
