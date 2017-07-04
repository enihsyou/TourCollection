package com.enihsyou.TourCollection;

public interface Tree<K extends Comparable<K>> {
    /**
     * @return 当前树中元素数量
     */
    int elementCount();

    /**
     * 在树中插入元素，如果已存在则替换原有的元素，将被替换的值返回
     * 不允许插入null元素
     *
     * @param insert_key 插入的键
     *
     * @return 键原先的值，null或者
     */
    K insertOrReplace(final K insert_key);

    /**
     * 搜索树上的节点，查询键对应的值，查询失败返回null，查询键不能为null
     *
     * @param search_key 搜索的键
     *
     * @return 搜索结果，失败返回null
     */
    K getValue(final K search_key);

    /**
     * 通过搜索键获得节点元素
     *
     * @param search_key 搜索的键
     *
     * @return 搜索结果，失败返回null
     */
    K getNodeItem(final K search_key);

    SinglyLinkedList<K> keys(Direction direction);

    /**
     * 从树中删除键，如果不存在则什么也不做
     *
     * @param delete_key 要删除的键
     *
     * @return 被删除的键值，操作失败则为null
     */
    K delete(final K delete_key);

    /**
     * 移除最小元素并返回它，如果不存在这样的最小值，返回null
     *
     * @return 移除的元素
     */
    K deleteMin();

    /**
     * 移除最大元素并返回它，如果不存在这样的最大值，返回null
     *
     * @return 移除的元素
     */
    K deleteMax();

    /**
     * 按层次打印树
     */
    void print();

    /**
     * 对每个在[first, last]范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void ascend(final ItemIterator<K> item_iterator);

    /**
     * 对每个在[greater_or_equal, less_than)范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param greater_or_equal 下限，包含在内
     * @param less_than        上限，不包含
     * @param item_iterator    对元素的操作方法，判断是否继续进行
     */
    void ascendRange(final K greater_or_equal, final K less_than, final ItemIterator<K> item_iterator);

    /**
     * 对每个在[first, pivot)范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param pivot         上限，不包含
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void ascendLessThan(final K pivot, final ItemIterator<K> item_iterator);

    /**
     * 对每个在[pivot, last]范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param pivot         下限，包含在内
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void ascendGreaterOrEqual(final K pivot, final ItemIterator<K> item_iterator);

    /**
     * 对每个在[last, first]范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void descend(final ItemIterator<K> item_iterator);

    /**
     * 对每个在[less_or_equal, greater_than)范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param less_or_equal 下限，包含在内
     * @param greater_than  上限，不包含
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void descendRange(final K less_or_equal, final K greater_than, final ItemIterator<K> item_iterator);

    /**
     * 对每个在(pivot, last]范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param pivot         上限，不包含
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void descendGreaterThan(final K pivot, final ItemIterator<K> item_iterator);

    /**
     * 对每个在[pivot, first]范围内的元素，调用item_iterator，直到这方法返回false
     *
     * @param pivot         下限，包含在内
     * @param item_iterator 对元素的操作方法，判断是否继续进行
     */
    void descendLessOrEqual(final K pivot, final ItemIterator<K> item_iterator);

    /**
     * 判断指定键是否在树中存在
     *
     * @param search_key 搜索键
     *
     * @return 是否存在
     */
    boolean has(final K search_key);

    /**
     * @return 树中最小元素，如果不存在这样的返回null
     */
    K min();

    /**
     * @return 树中最大元素，如果不存在这样的返回null
     */
    K max();

     K getIndex(final int i);
    enum Direction {
        ASCEND, DESCEND
    }

    abstract class TreeNode<K extends Comparable<K>> {
        abstract K get(final K key);

        abstract K min();

        abstract K max();
    }

    /**
     * 对K进行操作，并决定是否继续迭代
     *
     * @param <K> 节点类型
     */
    @FunctionalInterface
    interface ItemIterator<K> {
        boolean accept(K item);
    }
}

