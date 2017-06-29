public class BTree<K extends Comparable<K>, V> {
    /**
     * 一棵B-Tree节点度数
     */
    static private int DEGREE = 2;
    /**
     * 除根节点外，每个节点都至少包含的键数
     */
    static private int LOWER_BOUND = DEGREE - 1;
    /**
     * 包括根节点在内的所有节点，都至多包含的键数
     */
    static private int UPPER_BOUND = DEGREE * 2 - 1;

    private BNode<K, V> root;
    private int count;

    public BTree() {
        root = new BNode<>();
    }

    public BTree(final int degree) {
        if (degree < 2)
            throw new IllegalArgumentException("B-Tree的节点度数至少为2");
        DEGREE = degree;
        LOWER_BOUND = degree - 1;
        UPPER_BOUND = degree * 2 - 1;
        new BTree<K, V>();
    }

    /**
     * 搜索树上的节点，查询键对应的值，查询失败返回null，查询键不能为null
     *
     * @param key 搜索的键
     * @return 搜索结果，失败返回null
     */
    public V get(final K key) {
        if (key == null)
            throw new NullPointerException("查询建不能为null");
        return root.get(key).value;
    }

    /**
     * 在树中插入元素，如果已存在则替换原有的元素，不允许插入null元素
     *
     * @param key 插入的键
     */
    public void insertOrReplaceElement(final K key, final V value) {
        if (key == null)
            throw new NullPointerException("键不能为null");
        /*当前为空树*/
        if (root == null) {
            root = new BNode<>();
            root.insert(key, value);
            count++;
            return;
        }
        /*根节点已满*/
        else if (root.keys.length() >= UPPER_BOUND) {
            final BNode<K, V>.SplitResult split_result = root.split(UPPER_BOUND / 2);
            BNode<K, V> old_root = this.root;
            root = new BNode<>();
            root.keys.append(split_result.grow_up_key);
            root.children.append(old_root);
            root.children.append(split_result.new_child_node);
        }
        /*分裂完后重新尝试插入*/
        BNode<K, V>.NodeItem insert = root.insert(key, value);
        if (insert.value == null) {
            count++;
        }
    }

    /**
     * 从树中删除键，如果不存在则什么也不做
     *
     * @param key 要删除的键
     * @return 被删除的键值，操作失败则为null
     */
    public V delete(final K key) {
        if (key == null) {
            throw new IllegalArgumentException("删除键不能为null");
        }
        return deleteItem(key, RemoveType.REMOVE_ITEM);
    }

    private V deleteItem(final K key, final RemoveType remove_type) {
        /*空树，什么也不用做*/
        if (root == null || root.keys.length() == 0)
            return null;
        /*先从树中删除元素，包括后续操作*/
        BNode<K, V>.NodeItem result = root.remove(key, remove_type);/*如果这次删除是成功的，计数器递减*/
        if (result != null)
            count--;

        /*如果删除后 根节点变空了，抓取子节点上来*/
        if (root.keys.length() == 0 && root.children.length() > 0) {
            root = root.children.get(0);
        }
        return result.value;
    }

    public int getCount() {
        return count;
    }

    public enum RemoveType {
        REMOVE_MAX, REMOVE_MIN, REMOVE_ITEM
    }

    final static class BNode<K extends Comparable<K>, V> {
        /**
         * 存储元素的键，作为主搜索键
         */
        final private Array<NodeItem> keys = new ComparableArray<>(UPPER_BOUND);
        /**
         * 充当向下一层引用的子节点指针
         */
        final private Array<BNode<K, V>> children = new ListArray<>(UPPER_BOUND + 1);

        /**
         * 将当前节点在指定位置分裂成两个节点。当前节点缩小
         *
         * @param index 分割位置
         * @return 分裂结果，返回一个二元组，第一个是向上浮的元素key，第二个是包含剩余元素的新节点
         */
        private SplitResult split(int index) {
            final NodeItem item = this.keys.get(index);
            final BNode<K, V> next = new BNode<>();
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
            final BNode<K, V> child_node = children.get(index);
            if (child_node.keys.length() < UPPER_BOUND)
                return false;

            final SplitResult split_result = child_node.split(UPPER_BOUND / 2);
            keys.insertAt(index, split_result.grow_up_key);
            children.insertAt(index + 1, split_result.new_child_node);
            return true;
        }

        /**
         * 在当前子树根节点插入元素，保证子树中没有节点超过最大元素限制
         * 如果指定元素key已存在，将会被替换
         *
         * @param insert_key 插入键
         * @return 插入位置上原先存放的元素
         */
        NodeItem insert(K insert_key, V insert_value) {
            final NodeItem insert_item = new NodeItem(insert_key, insert_value);

            final Array.FindResult find_result = keys.find(insert_item);
            final int i = find_result.getPosition();
            /*发现相同键的元素，进行替换*/
            if (find_result.isFound()) {
                final NodeItem original_value = keys.get(i);
                keys.replace(i, insert_item);
                return original_value;
            }
            /*当前是叶子节点，没有子节点，在合适的有序位置插入*/
            if (children.length() == 0) {
                keys.insertAt(i, insert_item);
                return new NodeItem(insert_key);
            }
            /*在内部中间节点发现了指定键，检查是否需要分裂*/
            if (maybeSplitChild(i)) {
                switch (keys.get(i).compareTo(insert_item)) {
                    case 1: // 在左边（更小的）节点插入
                        return children.get(i).insert(insert_key, insert_value);
                    case -1: // 在右边（更大的）节点插入
                        return children.get(i + 1).insert(insert_key, insert_value);
                    case 0: // 相等 替换
                        final NodeItem original_value = keys.get(i);
                        keys.replace(i, insert_item);
                        return original_value;
                }
            }
            /*在有空位了的节点进行插入*/
            return children.get(i).insert(insert_key, insert_value);
        }

        /**
         * 在当前节点子树中搜索键
         *
         * @param key 搜索的键
         * @return 键对应的值，搜索失败为null
         */
        NodeItem get(K key) {
            Array.FindResult find_result = keys.find(new NodeItem(key));
            if (find_result.isFound())
                return keys.get(find_result.getPosition());

            if (children.length() > 0)
                return children.get(find_result.getPosition()).get(key);

            return new NodeItem(key);
        }

        /**
         * @return 子树中的最小元素
         */
        NodeItem min() {
            BNode<K, V> bNode = this;
            while (bNode.children.length() > 0)
                bNode = bNode.children.first();
            if (bNode.keys.length() == 0)
                return null;
            return bNode.keys.first();
        }

        /**
         * @return 子树重点最大元素
         */
        NodeItem max() {
            BNode<K, V> bNode = this;
            while (bNode.children.length() > 0)
                bNode = bNode.children.last();
            if (bNode.keys.length() == 0)
                return null;
            return bNode.keys.last();
        }

        /**
         * 从子树中删除元素
         *
         * @param key         删除的键
         * @param remove_type 删除类型
         * @return 被移除的元素
         */
        private NodeItem remove(K key, RemoveType remove_type) {
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
                    Array.FindResult find_result = keys.find(new NodeItem(key));
                    i = find_result.getPosition();
                    is_found = find_result.isFound();
                    if (children.length() == 0)
                        return is_found ? keys.removeAt(i) : new NodeItem(key);
                    break;
                default:
                    throw new IllegalArgumentException("无效的删除类型");
            }
            /*如果到这了，表明节点存在子节点*/
            BNode<K, V> child = children.get(i);
            if (child.keys.length() <= LOWER_BOUND) {
                return growChildAndRemove(i, key, remove_type);
            }
            /*不论我们是否有足够的元素，或者已经做了合并/偷取，因为已经处理完了，可以准备返回元素了*/
            if (is_found) {
                /*在i位置的元素，以及选择的child能够给我们一个前列，能保证超过LOWER_BOUND个元素在里面*/
                NodeItem result = keys.get(i);
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
        private NodeItem growChildAndRemove(int i, K remove_key, RemoveType remove_type) {
            BNode<K, V> child = children.get(i);
            if (i > 0 && children.get(i - 1).keys.length() > LOWER_BOUND) {
                /*从左孩子窃取元素*/
                final BNode<K, V> steal_from = children.get(i - 1);
                NodeItem stolen_item = steal_from.keys.popLast();
                child.keys.rightShift(keys.get(i - 1));
                keys.replace(i - 1, stolen_item);
                if (steal_from.children.length() > 0)
                    child.children.rightShift(steal_from.children.popLast());
            } else if (i < keys.length() && children.get(i + 1).keys.length() > LOWER_BOUND) {
                /*从有孩子窃取元素*/
                final BNode<K, V> steal_from = children.get(i + 1);
                NodeItem stolen_item = steal_from.keys.popFirst();
                child.keys.append(keys.get(i));
                keys.replace(i, stolen_item);
                if (steal_from.children.length() > 0)
                    child.children.append(steal_from.children.popFirst());

            } else {
                if (i >= keys.length())
                    child = children.get(--i);
                /*和右孩子合并*/
                final NodeItem merge_item = keys.removeAt(i);
                final BNode<K, V> merge_child = children.removeAt(i + 1);
                child.keys.append(merge_item);
                child.keys.append(merge_child.keys, 0);
                child.children.append(merge_child.children, 0);
            }
            return remove(remove_key, remove_type);
        }

        @Override
        public String toString() {
            return keys.toString();
        }

        class NodeItem implements Comparable<NodeItem> {
            K key;
            V value;

            public NodeItem(K key, V value) {
                this.key = key;
                this.value = value;
            }

            NodeItem(K search_for) {
                this.key = search_for;
                this.value = null;
            }

            @Override
            public int compareTo(NodeItem o) {
                return key.compareTo(o.key);
            }

            @Override
            public String toString() {
                return "<" + key + ", " + value + '>';
            }
        }

        class SplitResult {
            NodeItem grow_up_key;
            BNode<K, V> new_child_node;

            SplitResult(final NodeItem grow_up_key, final BNode<K, V> new_child_node) {
                this.grow_up_key = grow_up_key;
                this.new_child_node = new_child_node;
            }
        }
    }
}


