package com.enihsyou.TourCollection;

/**
 * B-Tree实现，Google的Go语言版本用Java语言实现
 * https://github.com/google/btree
 * https://github.com/enihsyou/TourCollection
 * 源代码协议Apache License 2.0
 *
 * @param <K> 存储的类型，需要可比较
 */
public class BTree<K extends Comparable<K>> implements Tree<K> {
    /**
     * 一棵B-Tree节点度数，节点最少的子节点数量children数量
     */
    static private int DEGREE = 3;
    /**
     * 除根节点外，每个节点都至少包含的键key数
     */
    static private int LOWER_BOUND = DEGREE - 1;
    /**
     * 包括根节点在内的所有节点，都至多包含的键key数
     */
    static private int UPPER_BOUND = DEGREE * 2 - 1;
    /**
     * 树的根节点
     */
    private BNode root;
    /**
     * 树中元素数量，每次插入和删除都会修改这个值，如果替换元素并不会修改数量大小
     */
    private int count;

    public static void main(String[] args) {
        BTree<Integer> tree = new BTree<>();
        for (int i = 0; i < 29; i++) {
            tree.insertOrReplace(i);
        }
        tree.print();
        System.out.println();
    }

    public BTree() {
        root = new BNode();
    }

    /**
     * 使用指定的度树创建B-Tree，度的定义是每个节点包含的最少子节点数量
     * 元素(key)的数量是这个数值-1，最大子节点数量是这个数值的两倍-1
     *
     * @param degree 指定的树度树
     */
    public BTree(final int degree) {
        if (degree < 2)
            throw new IllegalArgumentException("B-Tree的节点度数至少为2");
        DEGREE = degree;
        LOWER_BOUND = degree - 1;
        UPPER_BOUND = degree * 2 - 1;
        root = new BNode();
    }

    /**
     * 简单地创建重复一定次数的字符串，用于空白填充
     *
     * @param times 重复次数
     * @return 生成的字符串
     */
    private static String repeatString(final int times) {
        final char[] chars = new char[times];
        for (int i = 0; i < times; i++)
            chars[i] = ' ';
        return new String(chars);
    }

    @Override
    public int elementCount() {
        return count;
    }

    @Override
    public K insertOrReplace(final K insert_key) {
        if (insert_key == null)
            throw new NullPointerException("键不能为null");
        /*根节点已满*/
        if (root.keys.length() >= UPPER_BOUND) {
            final BNode.SplitResult split_result = root.split(UPPER_BOUND / 2);
            BNode old_root = this.root;
            root = new BNode();

            root.keys.append(split_result.grow_up_key);
            root.children.append(old_root);
            root.children.append(split_result.new_child_node);
        }
        /*分裂完后重新尝试插入*/
        K insert = root.insert(insert_key);
        if (insert == null) {
            count++;
            return null;
        }
        return insert;
    }

    @Override
    public K getValue(final K search_key) {
        if (search_key == null)
            throw new NullPointerException("查询键不能为null");

        return root.get(search_key);
    }

    @Override
    public K getNodeItem(final K search_key) {
        if (search_key == null)
            throw new NullPointerException("查询键不能为null");

        return root.get(search_key);
    }

    @Override
    public SinglyLinkedList<K> keys(final Direction direction) {
        final SinglyLinkedList<K> list = new SinglyLinkedList<>();
        switch (direction) {
            case ASCEND:
                ascend(list::add);
                return list;
            case DESCEND:
                descend(list::add);
                return list;
            default:
                throw new IllegalArgumentException("不正确的类型");
        }
    }

    @Override
    public K delete(final K delete_key) {
        if (delete_key == null)
            throw new IllegalArgumentException("删除键不能为null");

        return deleteItem(delete_key, RemoveType.REMOVE_ITEM);
    }

    @Override
    public K deleteMin() {
        return deleteItem(null, RemoveType.REMOVE_MIN);
    }

    @Override
    public K deleteMax() {
        return deleteItem(null, RemoveType.REMOVE_MAX);
    }

    @Override
    public void print() {
        root.print(0, 0);
    }

    @Override
    public void ascend(final ItemIterator<K> item_iterator) {
        root.iterate(null, null, Direction.ASCEND, false, false, item_iterator);
    }

    @Override
    public void ascendRange(final K greater_or_equal, final K less_than, final ItemIterator<K> item_iterator) {
        root.iterate(greater_or_equal, less_than, Direction.ASCEND, true, false, item_iterator);
    }

    @Override
    public void ascendLessThan(final K pivot, final ItemIterator<K> item_iterator) {
        root.iterate(null, pivot, Direction.ASCEND, false, false, item_iterator);
    }

    @Override
    public void ascendGreaterOrEqual(final K pivot, final ItemIterator<K> item_iterator) {
        root.iterate(pivot, null, Direction.ASCEND, true, false, item_iterator);
    }

    @Override
    public void descend(final ItemIterator<K> item_iterator) {
        root.iterate(null, null, Direction.DESCEND, false, false, item_iterator);
    }

    @Override
    public void descendRange(final K less_or_equal, final K greater_than, final ItemIterator<K> item_iterator) {
        root.iterate(less_or_equal, greater_than, Direction.DESCEND, true, false, item_iterator);
    }

    @Override
    public void descendGreaterThan(final K pivot, final ItemIterator<K> item_iterator) {
        root.iterate(null, pivot, Direction.DESCEND, false, false, item_iterator);
    }

    @Override
    public void descendLessOrEqual(final K pivot, final ItemIterator<K> item_iterator) {
        root.iterate(pivot, null, Direction.DESCEND, true, false, item_iterator);
    }

    @Override
    public boolean has(final K search_key) {
        return getValue(search_key) != null;
    }

    @Override
    public K min() {
        return root.min();
    }

    @Override
    public K max() {
        return root.max();
    }

    public K getIndex(final int i) {
        return keys(Direction.ASCEND).get(i);
    }

    private K deleteItem(final K delete_key, final RemoveType remove_type) {
        /*空树，什么也不用做*/
        if (root == null || root.keys.length() == 0)
            return null;
        /*先从树中删除元素，包括后续操作*/
        K result = root.remove(delete_key, remove_type);/*如果这次删除是成功的，计数器递减*/
        if (result != null)
            count--;

        /*如果删除后 根节点变空了，抓取子节点上来*/
        if (root.keys.length() == 0 && root.children.length() > 0)
            root = root.children.get(0);

        return result;
    }

    public enum RemoveType {
        REMOVE_MAX, REMOVE_MIN, REMOVE_ITEM
    }

    static class ResultPair {
        final boolean hit, ok;

        ResultPair(final boolean hit, final boolean ok) {
            this.hit = hit;
            this.ok = ok;
        }
    }

    final private class BNode extends TreeNode<K> {
        /**
         * 存储元素的键，作为主搜索键
         */
        final private Array<K> keys = new ComparableArray<>(UPPER_BOUND);
        /**
         * 充当向下一层引用的子节点指针
         */
        final private Array<BNode> children = new ListArray<>(UPPER_BOUND + 1);

        /**
         * 将当前节点在指定位置分裂成两个节点。当前节点缩小
         *
         * @param index 分割位置
         * @return 分裂结果，返回一个二元组，第一个是向上浮的元素key，第二个是包含剩余元素的新节点
         */
        private SplitResult split(int index) {
            final K item = this.keys.get(index);
            final BNode next = new BNode();

            next.keys.append(this.keys, index + 1);
            this.keys.truncate(index);
            /*如果有存在子节点，重分配*/
            if (this.children.length() > 0) {
                next.children.append(this.children, index + 1);
                this.children.truncate(index + 1);
            }

            return new SplitResult(item, next);
        }

        /**
         * 检查一个子节点是否需要进行split操作，如果需要则进行
         *
         * @param index 预计的分裂位置
         * @return 是否进行了分裂操作
         */
        private boolean maybeSplitChild(int index) {
            /*当前节点还有空间 没有填满 不需要分裂*/
            final BNode child_node = children.get(index);
            if (child_node.keys.length() < UPPER_BOUND)
                return false;

            final SplitResult split_result = child_node.split(UPPER_BOUND / 2);

            keys.insertAt(index, split_result.grow_up_key);
            children.insertAt(index + 1, split_result.new_child_node);

            return true;
        }

        /**
         * 在当前节点子树中搜索键
         *
         * @param key 搜索的键
         * @return 键对应的值，搜索失败为null
         */
        @Override
        K get(K key) {
            Array.FindResult find_result = keys.find(key);
            if (find_result.isFound())
                return keys.get(find_result.getPosition());

            if (children.length() > 0)
                return children.get(find_result.getPosition()).get(key);

            return null;
        }

        /**
         * @return 子树中的最小元素
         */
        @Override
        K min() {
            BNode bNode = this;
            while (bNode.children.length() > 0)
                bNode = bNode.children.first();

            if (bNode.keys.length() == 0)
                return null;

            return bNode.keys.first();
        }

        /**
         * @return 子树重点最大元素
         */
        @Override
        K max() {
            BNode bNode = this;
            while (bNode.children.length() > 0)
                bNode = bNode.children.last();

            if (bNode.keys.length() == 0)
                return null;

            return bNode.keys.last();
        }

        /**
         * 在当前子树根节点插入元素，保证子树中没有节点超过最大元素限制
         * 如果指定元素key已存在，将会被替换
         *
         * @param insert_key 插入键
         * @return 插入位置上原先存放的元素
         */
        private K insert(K insert_key) {
            final Array.FindResult find_result = keys.find(insert_key);
            final int i = find_result.getPosition();
            /*发现相同键的元素，进行替换*/
            if (find_result.isFound()) {
                final K original_value = keys.get(i);
                keys.replace(i, insert_key);

                return original_value;
            }
            /*当前是叶子节点，没有子节点，在合适的有序位置插入*/
            if (children.length() == 0) {
                keys.insertAt(i, insert_key);

                return null;
            }
            /*在内部中间节点发现了指定键，检查是否需要分裂*/
            if (maybeSplitChild(i)) {
                switch (keys.get(i).compareTo(insert_key)) {
                    case 1: // 在左边（更小的）节点插入
                        return children.get(i).insert(insert_key);
                    case -1: // 在右边（更大的）节点插入
                        return children.get(i + 1).insert(insert_key);
                    case 0: // 相等 替换
                        final K original_value = keys.get(i);
                        keys.replace(i, insert_key);
                        return original_value;
                }
            }
            /*在有空位了的节点进行插入*/
            return children.get(i).insert(insert_key);
        }

        /**
         * 从子树中删除元素
         *
         * @param key         删除的键
         * @param remove_type 删除类型
         * @return 被移除的元素
         */
        private K remove(K key, RemoveType remove_type) {
            int i;
            boolean is_found = false;
            switch (remove_type) {
                case REMOVE_MAX:
                    if (children.length() == 0)
                        return keys.popLast();
                    i = keys.length();
                    break;
                case REMOVE_MIN:
                    if (children.length() == 0)
                        return keys.popFirst();
                    i = 0;
                    break;
                case REMOVE_ITEM:
                    Array.FindResult find_result = keys.find(key);
                    i = find_result.getPosition();
                    is_found = find_result.isFound();

                    if (children.length() == 0)
                        return is_found ? keys.removeAt(i) : key;
                    break;
                default:
                    throw new IllegalArgumentException("无效的删除类型");
            }
            /*如果到这了，表明节点存在子节点*/
            BNode child = children.get(i);
            if (child.keys.length() <= LOWER_BOUND)
                return growChildAndRemove(i, key, remove_type);

            /*不论我们是否有足够的元素，或者已经做了合并/偷取，因为已经处理完了，可以准备返回元素了*/
            if (is_found) {
                /*在i位置的元素，以及选择的child能够给我们一个前列，能保证超过LOWER_BOUND个元素在里面*/
                K result = keys.get(i);
                /*移除最大元素的方式把前列的元素i放到自己身上*/
                keys.replace(i, child.remove(null, RemoveType.REMOVE_MAX));
                return result;
            }
            /*最后使用递归调用，都走到这了，能确定元素不在当前节点，所以向足够大的子节点移动*/
            return child.remove(key, remove_type);
        }

        /**
         * 在i位置的子节点扩展，保证能够从中移除元素，并且保证最小元素的限制，然后调用remove真正移除元素
         * 需要考虑两个边缘情况
         * 1) 元素在当前节点
         * 2) 元素在子节点
         * 这两种情况下，都需要处理两种子情况
         * A) 节点有足够的元素，能够禁得起删掉一个
         * B) 节点如果再少一个就不满足 > LOWER_BOUND的限制了
         * 对于B情况，需要检查
         * a) 左兄弟是否能拿一个过来
         * b) 有兄弟是否能拿一个过来
         * c) 左右都没有，需要进行合并
         * 为了简化代码，把情况1和情况2做相同处理：
         * 如果一个节点元素不足够多，那就去确保它变得足够多（处理情况a,b,c）
         * 然后就可以简化为重新进行remove操作，然后第二次（不论是情况1还是情况2）能够保证有足够多的
         * 元素在节点中了，这样就能保证解决情况A
         *
         * @param i           指定位置
         * @param remove_key  移除元素键
         * @param remove_type 删除类型
         * @return 移除的元素
         */
        private K growChildAndRemove(int i, K remove_key, RemoveType remove_type) {
            BNode child = children.get(i);
            if (i > 0 && children.get(i - 1).keys.length() > LOWER_BOUND) {
                /*从左孩子窃取元素*/
                final BNode steal_from = children.get(i - 1);
                K stolen_item = steal_from.keys.popLast();
                child.keys.rightShift(keys.get(i - 1));
                keys.replace(i - 1, stolen_item);

                if (steal_from.children.length() > 0)
                    child.children.rightShift(steal_from.children.popLast());
            } else if (i < keys.length() && children.get(i + 1).keys.length() > LOWER_BOUND) {
                /*从有孩子窃取元素*/
                final BNode steal_from = children.get(i + 1);
                K stolen_item = steal_from.keys.popFirst();
                child.keys.append(keys.get(i));
                keys.replace(i, stolen_item);

                if (steal_from.children.length() > 0)
                    child.children.append(steal_from.children.popFirst());
            } else {
                if (i >= keys.length())
                    child = children.get(--i);
                /*和右孩子合并*/
                final K merge_item = keys.removeAt(i);
                final BNode merge_child = children.removeAt(i + 1);

                child.keys.append(merge_item);
                child.keys.append(merge_child.keys, 0);
                child.children.append(merge_child.children, 0);
            }
            return remove(remove_key, remove_type);
        }

        private ResultPair iterate(final K start_key,
                                   final K stop_key,
                                   final Direction direction,
                                   final boolean include_start,
                                   boolean hit,
                                   final ItemIterator<K> item_iterator) {
            boolean ok;
            switch (direction) {
                case ASCEND:
                    for (int i = 0; i < keys.length(); i++) {
                        if (start_key != null && keys.get(i).compareTo(start_key) < 0)
                            continue;
                        if (children.length() > 0) {
                            final ResultPair pair = children.get(i)
                                    .iterate(start_key, stop_key, direction, include_start, hit, item_iterator);
                            hit = pair.hit;
                            ok = pair.ok;
                            if (!ok)
                                return new ResultPair(hit, false);
                        }
                        if (!include_start && !hit && start_key != null && keys.get(i).compareTo(start_key) <= 0) {
                            hit = true;
                            continue;
                        }
                        hit = true;
                        if (stop_key != null && keys.get(i).compareTo(stop_key) >= 0)
                            return new ResultPair(true, false);
                        if (!item_iterator.accept(keys.get(i)))
                            return new ResultPair(true, false);
                    }
                    if (children.length() > 0) {
                        final ResultPair pair =
                                children.last().iterate(start_key, stop_key, direction, include_start, hit, item_iterator);
                        hit = pair.hit;
                        ok = pair.ok;
                        if (!ok)
                            return new ResultPair(hit, false);
                    }
                    break;
                case DESCEND:
                    for (int i = keys.length() - 1; i >= 0; i--) {
                        if (start_key != null && start_key.compareTo(keys.get(i)) <= 0)
                            if (!include_start || hit || start_key.compareTo(keys.get(i)) < 0)
                                continue;
                        if (children.length() > 0) {
                            final ResultPair pair = children.get(i + 1)
                                    .iterate(start_key, stop_key, direction, include_start, hit, item_iterator);
                            hit = pair.hit;
                            ok = pair.ok;
                            if (!ok)
                                return new ResultPair(hit, false);
                        }
                        if (stop_key != null && keys.get(i).compareTo(stop_key) <= 0)
                            return new ResultPair(hit, false); // 继续
                        hit = true;
                        if (!item_iterator.accept(keys.get(i)))
                            return new ResultPair(true, false);
                    }
                    if (children.length() > 0) {
                        final ResultPair pair =
                                children.first().iterate(start_key, stop_key, direction, include_start, hit, item_iterator);
                        hit = pair.hit;
                        ok = pair.ok;
                        if (!ok)
                            return new ResultPair(hit, false);
                    }
                    break;
            }
            return new ResultPair(hit, true);
        }

        private void print(final int j, final int level) {
            System.out.format("%sNODE%d:%s\n", repeatString(2 * level), j, keys);
            for (int i = 0; i < children.length(); i++)
                children.get(i).print(i, level + 1);
        }

        @Override
        public String toString() {
            return keys.toString();
        }

        private class SplitResult {
            K grow_up_key;
            BNode new_child_node;

            SplitResult(final K grow_up_key, final BNode new_child_node) {
                this.grow_up_key = grow_up_key;
                this.new_child_node = new_child_node;
            }
        }
    }
}
