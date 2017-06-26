public interface BST<Key extends Comparable<Key>, Value> extends Tree{
    Value get(Key key);
    void put(Key key, Value value);
    Value search(Key key);
}
