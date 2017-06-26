public class BTree<K extends Comparable<K>, V> implements BST<K, V> {
    /**
     * A B-Tree is defined by the term minimum degree ‘t’.
     */
    final static private int DEGREE = 3;
    /**
     * Every node except root must contain at least t-1 keys. Root may contain minimum 1 key.
     */
    final static private int LOWER_BOUND = DEGREE - 1;
    /**
     * All nodes (including root) may contain at most 2t – 1 keys.
     */
    final static private int UPPER_BOUND = DEGREE * 2 - 1;

    private BNode<K, V> root;
    private int height;
    private int count;

    public BTree() {
        root = new BNode<>();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public V get(final K key) {
        return null;
    }

    @Override
    public void put(final K key, final V value) {

    }

    @Override
    public V search(final K k) {
        return null;
    }

    final static private class BNode<K extends Comparable, V> {
        private int memberCount;
        private Array<Entry<K, V>> nodes = new Array<>(UPPER_BOUND);
        private Array<BNode<K, V>> children = new Array<>(UPPER_BOUND + 1);

        boolean isLeaf() {
            return memberCount == 0;
        }

        BNode<K, V> getChildAt(final int keyIndex, Direction direction) {
            if (isLeaf()) { return null; }
            int next_index = keyIndex + direction.gap;
            if (next_index < 0 || next_index > memberCount) { return null; }
            return children.get(next_index);
        }

        BNode<K, V> getRightChild(final int keyIndex) {
            return getChildAt(keyIndex, Direction.RIGHT);
        }

        BNode<K, V> getLeftChild(final int keyIndex) {
            return getChildAt(keyIndex, Direction.LEFT);
        }

        enum Direction {
            RIGHT(1), LEFT(-1);
            final int gap;

            Direction(final int gap) {
                this.gap = gap;
            }
        }
    }

    final static private class Entry<K extends Comparable, V> {
        private K key;
        private V value;
    }
}
