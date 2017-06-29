@FunctionalInterface
public interface ItemIterator {
    boolean accept(BTree.BNode.NodeItem item);
}
