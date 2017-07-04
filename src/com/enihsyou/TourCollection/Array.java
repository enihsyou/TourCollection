package com.enihsyou.TourCollection;

/**
 * 对数组进行包装，添加一些便捷的方法，内部不限定元素顺序，但是元素之间紧缩无null
 *
 * @param <T>
 */
public interface Array<T> {
    /**
     * 获取当前内部储存的元素数量，保证也理应是非负整数
     *
     * @return 储存的元素数量，0为空
     */
    int length();

    /**
     * 获取index位置上的元素，相当于array[i]表达式，越界会抛出ArrayIndexOutOfBoundsException
     *
     * @param get_from 元素位置
     * @return 位置上的元素
     */
    T get(final int get_from);

    /**
     * @return 返回第一个元素，不存在的情况下抛出ArrayIndexOutOfBoundsException
     */
    T first();

    /**
     * @return 返回最后一个元素，不存在的情况下抛出NoSuchElementException
     */
    T last();

    /**
     * 在列表尾部增加元素，预设还有空位，否则抛出ArrayIndexOutOfBoundsException
     *
     * @param item 增加的元素
     */
    void append(final T item);

    /**
     * 类似于[this] += collection[copy_from:]，从copy_from位置开始，按原顺序添加到本列表尾部
     * 超过空间的大小会抛出ArrayIndexOutOfBoundsException
     *
     * @param collection 要复制的列表
     * @param copy_from  复制起始索引位置
     */
    void append(final Array<T> collection, final int copy_from);

    /**
     * 在index位置上插入item，其后元素向后顺移，没空间或者index不存在都会抛出ArrayIndexOutOfBoundsException
     * 如 [1, 2, 3].insertAt(1, 0) => [1, 0, 2, 3]
     *
     * @param index       插入位置
     * @param insert_item 插入的元素
     */
    void insertAt(final int index, final T insert_item);

    /**
     * 在第一个位置插入元素
     *
     * @param insert_item 要插入的元素
     */
    void rightShift(final T insert_item);

    /**
     * 从cut_from位置移除元素，切断尾巴
     * 如 [1, 2, 3].truncate(1) => [1, ]
     *
     * @param cut_from 剪切点
     */
    void truncate(final int cut_from);

    /**
     * 移除最后一个元素，列表为空抛出ArrayIndexOutOfBoundsException
     *
     * @return 被移除的元素
     */
    T popLast();

    /**
     * 移除第一个元素，列表为空抛出ArrayIndexOutOfBoundsException
     *
     * @return 被移除的元素
     */
    T popFirst();

    /**
     * 替换index位置上的元素，位置不正确抛出ArrayIndexOutOfBoundsException
     * 相当于 this[index] = replace_with
     *
     * @param index        替换的位置
     * @param replace_with 替换成
     */
    void replace(final int index, final T replace_with);

    /**
     * 把指定位置的元素移除，位置上元素不存在抛出ArrayIndexOutOfBoundsException
     *
     * @param index 移除元素的索引号
     * @return 被移除的元素
     */
    T removeAt(final int index);

    /**
     * 搜索指定key
     *
     * @param search_for 搜索元素
     * @return 搜索结果
     */
    FindResult find(final T search_for);

    class FindResult {
        final private int position;
        final private boolean found;

        FindResult(final int position, final boolean found) {
            this.position = position;
            this.found = found;
        }

        int getPosition() {
            return position;
        }

        boolean isFound() {
            return found;
        }
    }

    default String makeString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i <= iMax; i++) {
            if (a[i] == null)
                continue;
            if (b.length() > 1) b.append(", ");
            b.append(a[i]);
        }
        return b.append(']').toString();
    }
}
