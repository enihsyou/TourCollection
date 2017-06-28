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
        } else if (root.keys.length() >= UPPER_BOUND) {
            final Pair split_result = root.split(UPPER_BOUND / 2);
            BNode old_root = this.root;
            root = new BNode();
            root.keys.append(split_result.a);
            root.children.append(old_root);
            root.children.append(split_result.b);
        }

        Integer insert = root.insert(key);
        if (insert == null) count++;
    }


    final class BNode {
        final private Array<Integer> keys = new ComparableArray<>(UPPER_BOUND);
        final private Array<BNode> children = new ListArray<>(UPPER_BOUND + 1);
        BNode parent;
        boolean isLeaf = true;
        private int memberCount;

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
