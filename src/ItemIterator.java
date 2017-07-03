@FunctionalInterface
public interface ItemIterator<K> {
    boolean accept(K item);
}
