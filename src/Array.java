public interface Array<T> {
    int length();

    T get(final int index);

    void append(final T item);

    void append(final Array<T> collection, final int copy_from);

    void insertAt(final int index, final T item);

    void truncate(final int cut_from);

    T pop();

    void replace(final int index, final T replace_with);

    T removeAt(final int index);

    SearchResult find(final T key);
    static class SearchResult {
        final private int position;
        final private boolean found;

        SearchResult(final int position, final boolean found) {
            this.position = position;
            this.found = found;
        }

        public int getPosition() {
            return position;
        }

        public boolean isFound() {
            return found;
        }
    }
}
