public class Array<T> {
    final private Object[] elementData;
    final private int size;

    public Array(final int size) {
        this.size = size;
        elementData = new Object[size];
    }

    private void rangeCheck(final int index) {
        if (index >= size || index < 0) { throw new IndexOutOfBoundsException(); }
    }

    public void put(final int index, T item) {
        rangeCheck(index);
        elementData[index] = item;
    }

    public T get(final int index) {
        rangeCheck(index);
        //noinspection unchecked
        return (T) elementData[index];
    }
}
