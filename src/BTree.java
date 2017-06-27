import java.util.ArrayList;

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

    void insertElement(final Integer key) {
        if (root == null){
            root = new BNode();
            root.keys.put(key);
            height++;
        }else {
            if (root.keys.length() >= UPPER_BOUND){
                final Pair split = root.split(DEGREE);
                final Integer item = split.a;
                final BNode second = split.b;
                BNode old_root = this.root;
                root = new BNode();
                root.keys.put(item);
                root.children.add(old_root);
                root.children.add(second);
            }
        }
        Integer insert = root.insert(key);
        if (insert == null) height++;
    }


    final class BNode {
        final private Array<Integer> keys = new Array<>(UPPER_BOUND);
        final private ArrayList<BNode> children = new ArrayList<>(UPPER_BOUND + 1);
        BNode parent;
        boolean isLeaf = true;
        private int memberCount = 0;

        Pair split(int index) {
            final Integer item = keys.get(index);
            final BNode next = new BNode();
            for (int i = index + 1; i < memberCount; i++) {
                next.keys.put(keys.get(i));
            }
            keys.truncate(index);
            if (children.size() > 0) {
                for (int i = index + 1; i < memberCount; i++) {
                    next.children.add(children.get(i));
                }
                for (int i = index + 1; i < memberCount; i++) {
                    children.remove(i);
                }

            }
            return new Pair(item, next);
        }

        boolean maybeSplitChild(int index) {
            if (children.get(index).keys.length() < UPPER_BOUND) { return false; }
            final BNode first = children.get(index);
            final Pair split = first.split(DEGREE);
            final Integer item = split.a;
            final BNode second = split.b;
            keys.insertAt(index, item);
            children.add(index + 1, second);
            return true;
        }

        Integer insert(Integer item) {
            final Array.SearchResult searchResult = keys.find(item);
            int i = searchResult.getPosition();
            if (searchResult.isFound()) {
                final Integer integer = keys.get(i);
                keys.replace(i, item);
                return integer;
            }
            if (children.size() == 0) {
                keys.insertAt(i, item);
                return null;
            }
            if (maybeSplitChild(i)) {
                final Integer in_tree = keys.get(i);
                switch (in_tree.compareTo(item)) {
                    case 1:
                        return children.get(i).insert(item);
                    case 0:
                        final Integer integer = keys.get(i);
                        keys.replace(i, item);
                        return integer;
                    case -1:
                        return children.get(i + 1).insert(item);
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
