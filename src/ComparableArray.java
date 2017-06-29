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

    /**
     * 搜索指定key，如果找到则isFound为true，否则为false
     * 不论找到没有，getPosition()都返回第一个不大于search_for的索引号，可以在那个位置进行有序的插入
     * 例如 [1, 3, 5, 7].find(5) => (3, true)
     * [1, 3, 5, 7].find(4) => (3, false)
     *
     * @param search_for 搜索元素
     *
     * @return 搜索结果
     */
    public FindResult find(final T search_for) {
        int i = 0;
        // 找到第一个大于等于键值的位置
        while (i < length && ((Comparable)elementData[i]).compareTo(search_for) < 0) {
            i++;
        }
        if (i < length && elementData[i] == search_for) {
            return new FindResult(i, true);
        }
        return new FindResult(i, false);
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
