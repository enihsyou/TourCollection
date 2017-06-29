import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class ListArray<T> implements Array<T> {
    final Object[] elementData;
    final private int ARRAY_SIZE;
    int length;

    public ListArray(int array_size) {
        this.ARRAY_SIZE = array_size;
        this.elementData = new Object[array_size];
    }

    public static void main(String[] args) {
        Array<Integer> array = new ListArray<>(4);
        array.append(1);
        array.append(3);
        array.append(5);
        array.append(7);
        array.removeAt(0);
        System.out.println();
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public T get(final int get_from) {
        rangeCheck(get_from);
        return (T) elementData[get_from];
    }

    @Override
    public T first() {
        return get(0);
    }

    @Override
    public T last() {
        return get(length - 1);
    }

    /**
     * 元素索引号合法性判断
     *
     * @param index 元素位置
     */
    private void rangeCheck(final int index) {
        if (index >= length || index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void append(final T item) {
        insertAt(length, item);
    }

    @Override
    public void append(final Array<T> collection, final int copy_from) {
        for (int i = copy_from; i < collection.length(); i++) {
            append(collection.get(i));
        }
    }

    /**
     * 插入位置合法性判断
     *
     * @param index 插入位置
     */
    private void rangeCheck2(final int index) {
        if (index > length || index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void insertAt(int index, T insert_item) {
        rangeCheck2(index);
        if (length == ARRAY_SIZE) {
            throw new ArrayIndexOutOfBoundsException("数组已满");
        }
        System.arraycopy(elementData, index, elementData, index + 1, length - index);
        elementData[index] = insert_item;
        length++;
    }

    @Override
    public void rightShift(T insert_item) {
        insertAt(0, insert_item);
    }

    @Override
    public void truncate(int cut_from) {
        rangeCheck2(cut_from);
        final int cut_to = length;
        for (int i = cut_from; i < cut_to; i++) {
            elementData[i] = null;
            length--;
        }
    }

    @Override
    public T popLast() {
        return removeAt(length - 1);
    }

    @Override
    public T popFirst() {
        return removeAt(0);
    }

    @Override
    public void replace(int index, T replace_with) {
        rangeCheck(index);
        elementData[index] = replace_with;
    }

    @Override
    public T removeAt(int index) {
        rangeCheck(index);
        if (length == 0) {
            throw new NoSuchElementException("数组为空");
        }
        final Object result = elementData[index];
        System.arraycopy(elementData, index + 1, elementData, index, length - 1 - index);
        elementData[--length] = null;
        return (T) result;
    }

    /**
     * 搜索指定key，如果找到则isFound为true，否则为false
     * 如果找到getPosition()返回元素索引号，否则返回-1
     *
     * @param search_for 搜索元素
     * @return 搜索结果
     */
    @Override
    public FindResult find(T search_for) {
        for (int i = 0; i < length; i++) {
            if (elementData[i] == search_for) {
                return new FindResult(i, true);
            }
        }
        return new FindResult(-1, false);
    }

    @Override
    public String toString() {
        return makeString(elementData);
    }
}
