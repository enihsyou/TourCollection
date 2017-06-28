import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class ListArray<T> implements Array<T> {
    final Object[] elementData;
    final private int ARRAY_SIZE;
    int length;

    public ListArray(int array_size) {
        this.ARRAY_SIZE = array_size;
        this.elementData = new Object[array_size];
        this.length = 0;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public T get(int index) {
        rangeCheck(index);
        return (T) elementData[index];
    }

    private void rangeCheck(int index) {
        if (index >= length || index < 0) throw new IndexOutOfBoundsException();
    }

    private void rangeCheck2(int index) {
        if (index > length || index < 0) throw new IndexOutOfBoundsException();
    }

    @Override
    public void append(T item) {
        if (length == ARRAY_SIZE) throw new IndexOutOfBoundsException();
        elementData[length++] = item;
    }

    @Override
    public void append(Array<T> collection, int copy_from) {
        for (int i = copy_from; i < collection.length(); i++) {
            append(collection.get(i));
        }
    }

    @Override
    public void insertAt(int index, T item) {
        rangeCheck2(index);
        for (int i = length - 1; i > index; i--) {
            elementData[i] = elementData[i - 1];
        }
        elementData[index] = item;
        length++;
    }

    @Override
    public void truncate(int cut_from) {
        rangeCheck(cut_from);
        final int cut_to = length;
        for (int i = cut_from; i < cut_to; i++) {
            elementData[i] = null;
            length--;
        }
    }

    @Override
    public T pop() {
        if (length == 0) throw new NoSuchElementException();
        final Object result = elementData[--length];
        elementData[length + 1] = null;
        return (T) result;
    }

    @Override
    public void replace(int index, T replace_with) {
        rangeCheck(index);
        elementData[index] = replace_with;
    }

    @Override
    public T removeAt(int index) {
        rangeCheck(index);
        final Object result = elementData[index];
        for (int i = index; i < length - 1; i++) {
            elementData[i] = elementData[i + 1];
        }
        elementData[--length] = null;
        return (T) result;
    }

    @Override
    public SearchResult find(T key) {
        for (int i = 0; i < elementData.length; i++) {
            Object elementDatum = elementData[i];
            if (elementDatum == key) return new SearchResult(i, true);
        }
        return new SearchResult(-1, false);
    }

    public static void main(String[] args) {
        Array<Integer> array = new ListArray<>(4);
        array.append(1);
        array.append(3);
        array.append(5);
        array.append(7);
        array.removeAt(0);
        System.out.println();
    }
}
