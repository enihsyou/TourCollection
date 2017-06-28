public class BTree {
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

    private BNode root;
    private int count;

    public BTree() { }

    public BTree(final int degree) {
        if (degree < 2) throw new IllegalArgumentException("B-Tree的节点度数至少为2");
        DEGREE = degree;
        LOWER_BOUND = degree - 1;
        UPPER_BOUND = degree * 2 - 1;
    }

    /**
     * 搜索树上的节点，查询键对应的值，查询失败返回null，查询键不能为null
     *
     * @param key 搜索的键
     *
     * @return 搜索结果，失败返回null
     */
    Integer get(final Integer key) {
        if (key == null)
            throw new NullPointerException("查询建不能为null");
        return root.get(key);
    }

    /**
     * 在树中插入元素，如果已存在则替换原有的元素
     * @param key 插入的键
     */
    void insertOrReplaceElement(final Integer key) {
        if (key == null)
            throw new NullPointerException("键不能为null");
        /*当前为空树*/
        if (root == null) {
            root = new BNode();
            root.keys.append(key);
            count++;
            return;
        }
        /*根节点已满*/
        else if (root.keys.length() >= UPPER_BOUND) {
            final SplitResult split_result = root.split(UPPER_BOUND / 2);
            BNode old_root = this.root;
            root = new BNode();
            root.keys.append(split_result.grow_up_key);
            root.children.append(old_root);
            root.children.append(split_result.new_child_node);
        }
        /*分裂完后重新尝试插入*/
        Integer insert = root.insert(key);
        if (insert == null) { count++; }
    }

    /**
     * 从树中删除键，如果不存在则什么也不做
     * @param key 要删除的键
     *
     * @return 被删除的键值，操作失败则为null
     */
    Integer delete(Integer key) {return deleteItem(key, removeType.REMOVE_ITEM);}

    private Integer deleteItem(Integer key, removeType remove_type) {
        if (root == null || root.keys.length() == 0) { return null; }
        Integer result = root.remove(key, remove_type);
        if (root.keys.length() == 0 && root.children.length() > 0) {
            BNode old_root = this.root;
            root = root.children.get(0);
            // old_root.freeNode();
        }
        if (result != null) { count--; }
        return result;
    }

    enum removeType {
        REMOVE_MAX, REMOVE_MIN, REMOVE_ITEM
    }

    final static class BNode {
        final private Array<Integer> keys = new ComparableArray<>(UPPER_BOUND);
        final private Array<BNode> children = new ListArray<>(UPPER_BOUND + 1);

        SplitResult split(int index) {
            final Integer item = this.keys.get(index);
            final BNode next = new BNode();
            next.keys.append(this.keys, index + 1);
            this.keys.truncate(index);
            if (this.children.length() > 0) {
                next.children.append(this.children, index + 1);
                this.children.truncate(index + 1);
            }
            return new SplitResult(item, next);
        }

        boolean maybeSplitChild(int index) {
            if (children.get(index).keys.length() < UPPER_BOUND) {
                return false;
            }
            final BNode first = children.get(index);
            final SplitResult split_result = first.split(UPPER_BOUND / 2);
            keys.insertAt(index, split_result.grow_up_key);
            children.insertAt(index + 1, split_result.new_child_node);
            return true;
        }

        Integer insert(Integer item) {
            final ComparableArray.SearchResult searchResult = keys.find(item);
            final int i = searchResult.getPosition();
            if (searchResult.isFound()) { // replace
                final Integer original_value = keys.get(i);
                keys.replace(i, item);
                return original_value;
            }
            if (children.length() == 0) {
                keys.insertAt(i, item);
                return null;
            }
            if (maybeSplitChild(i)) {
                final Integer in_tree = keys.get(i);
                switch (in_tree.compareTo(item)) {
                    case 1:
                        return children.get(i).insert(item);
                    case -1:
                        return children.get(i + 1).insert(item);
                    case 0:
                        final Integer integer = keys.get(i);
                        keys.replace(i, item);
                        return integer;
                }
            }
            return children.get(i).insert(item);
        }

        Integer get(Integer key) {
            Array.SearchResult searchResult = keys.find(key);
            if (searchResult.isFound()) {
                return keys.get(searchResult.getPosition());
            }
            else if (children.length() > 0) {
                return children.get(searchResult.getPosition()).get(key);
            }
            return null;
        }

        Integer min() {
            BNode bNode = this;
            while (bNode.children.length() > 0) { bNode = bNode.children.get(0); }
            if (bNode.keys.length() == 0) { return null; }
            return bNode.keys.get(0);
        }

        Integer max() {
            BNode bNode = this;
            while (bNode.children.length() > 0) { bNode = bNode.children.get(bNode.children.length() - 1); }
            if (bNode.keys.length() == 0) { return null; }
            return bNode.keys.get(bNode.keys.length() - 1);
        }

        Integer remove(Integer key, removeType remove_type) {
            int i = -1;
            boolean is_found = false;
            switch (remove_type) {
                case REMOVE_MAX:
                    if (children.length() == 0) { return keys.popLast(); }
                    i = keys.length();
                    break;
                case REMOVE_MIN:
                    if (children.length() == 0) { return keys.removeAt(0); }
                    i = 0;
                    break;
                case REMOVE_ITEM:
                    Array.SearchResult searchResult = keys.find(key);
                    i = searchResult.getPosition();
                    is_found = searchResult.isFound();
                    if (children.length() == 0) { return is_found ? keys.removeAt(i) : null; }
                    break;
            }
            BNode child = children.get(i);
            if (child.keys.length() <= LOWER_BOUND) { return growChildAndRemove(i, key, remove_type); }
            if (is_found) {
                Integer result = keys.get(i);
                keys.replace(i, child.remove(null, removeType.REMOVE_MAX));
                return result;
            }
            return child.remove(key, remove_type);
        }

        Integer growChildAndRemove(int i, Integer item, removeType remove_type) {
            BNode child = children.get(i);
            if (i > 0 && children.get(i - 1).keys.length() > LOWER_BOUND) {
                BNode steal_from = children.get(i - 1);
                Integer stolen_item = steal_from.keys.popLast();
                child.keys.insertAt(0, keys.get(i - 1));
                keys.replace(i - 1, stolen_item);
                if (steal_from.children.length() > 0) { child.children.insertAt(0, steal_from.children.popLast()); }
            }
            else if (i > 0 && children.get(i + 1).keys.length() > LOWER_BOUND) {
                BNode steal_from = children.get(i + 1);
                Integer stolen_item = steal_from.keys.removeAt(0);
                child.keys.append(keys.get(i));
                keys.replace(i, stolen_item);
                if (steal_from.children.length() > 0) { child.children.append(steal_from.children.removeAt(0)); }
            }
            else {
                if (i >= keys.length()) {
                    i--;
                    child = children.get(i);
                }
                Integer merge_item = keys.removeAt(i);
                BNode merge_child = children.removeAt(i + 1);
                child.keys.append(merge_item);
                child.keys.append(merge_child.keys, 0);
                child.children.append(merge_child.children, 0);

                // freeNode();
            }
            return remove(item, remove_type);
        }

        private void freeNode() {
            keys.truncate(0);
            children.truncate(0);
        }


    }

    static class SplitResult {
        Integer grow_up_key;
        BTree.BNode new_child_node;

        public SplitResult(final Integer grow_up_key, final BTree.BNode new_child_node) {
            this.grow_up_key = grow_up_key;
            this.new_child_node = new_child_node;
        }
    }
}


