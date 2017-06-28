public class BTree {
    /**
     * A B-Tree is defined by the term minimum degree ‘t’.
     */
    final static private int DEGREE = 2;
    /**
     * Every node except root must contain at least t-1 keys. Root may contain minimum 1 key.
     */
    final static private int LOWER_BOUND = DEGREE - 1;
    /**
     * All nodes (including root) may contain at most 2t – 1 keys.
     */
    final static private int UPPER_BOUND = DEGREE * 2 - 1;

    BNode root;
    private int height;
    private int count;

    public BTree() {
        // root = new BNode();
    }
    //
    // void findElement(final Integer key) {
    //     root.findInNode(key);
    // }

    void insertOrReplaceElement(final Integer key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (root == null) {
            root = new BNode();
            root.keys.append(key);
            count++;
            return;
        }
        else if (root.keys.length() >= UPPER_BOUND) {
            final Pair split_result = root.split(UPPER_BOUND / 2);
            BNode old_root = this.root;
            root = new BNode();
            root.keys.append(split_result.a);
            root.children.append(old_root);
            root.children.append(split_result.b);
        }

        Integer insert = root.insert(key);
        if (insert == null) { count++; }
    }

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

        Pair split(int index) {
            final Integer item = this.keys.get(index);
            final BNode next = new BNode();
            next.keys.append(this.keys, index + 1);
            this.keys.truncate(index);
            if (this.children.length() > 0) {
                next.children.append(this.children, index + 1);
                this.children.truncate(index + 1);
            }
            return new Pair(item, next);
        }

        boolean maybeSplitChild(int index) {
            if (children.get(index).keys.length() < UPPER_BOUND) {
                return false;
            }
            final BNode first = children.get(index);
            final Pair split_result = first.split(UPPER_BOUND / 2);
            keys.insertAt(index, split_result.a);
            children.insertAt(index + 1, split_result.b);
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
                    if (children.length() == 0) { return keys.pop(); }
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
                Integer stolen_item = steal_from.keys.pop();
                child.keys.insertAt(0, keys.get(i - 1));
                keys.replace(i - 1, stolen_item);
                if (steal_from.children.length() > 0) { child.children.insertAt(0, steal_from.children.pop()); }
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
}

class Pair {
    Integer a;
    BTree.BNode b;

    public Pair(final Integer a, final BTree.BNode b) {
        this.a = a;
        this.b = b;
    }
}
