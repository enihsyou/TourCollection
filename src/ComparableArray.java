/**
 * 固定大小的数组，为避免泛型数组的错误
 * 内部由小到大排列
 *
 * @param <T> 存储类型
 */
@SuppressWarnings("unchecked")
public class ComparableArray<T extends Comparable> extends ListArray<T> {
    public ComparableArray(int array_size) {
        super(array_size);
    }

    public SearchResult find(final T key) {
        int i = 0;
        // 找到第一个大于等于键值的位置
        while (i < length && ((Comparable)elementData[i]).compareTo(key) < 0) {
            i++;
        }
        if (i < length && elementData[i] == key) {
            return new SearchResult(i, true);
        }
        return new SearchResult(i, false);
    }

    public static void main(String[] args) {
        Array<Integer> array = new ComparableArray<>(4);
        array.append(1);
        array.append(2);
        array.append(3);
        array.append(4);
        array.find(3);
        array.removeAt(0);
        System.out.println();
    }
}
