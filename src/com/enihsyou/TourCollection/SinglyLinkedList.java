package com.enihsyou.TourCollection;

public class SinglyLinkedList<Item> {
    private SinglyNode<Item> head;
    private int count;

    public SinglyLinkedList() {
        head = null;
        count = 0;
    }

    public static void main(String[] args) {
        SinglyLinkedList<Integer> linkedList = new SinglyLinkedList<>();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.remove(2);
        System.out.println(linkedList);
    }

    /**
     * 在头部进行插入，不允许null元素
     *
     * @param item 要插入的元素
     *
     * @return 是否插入成功
     */
    public boolean add(final Item item) {
        if (item == null)
            //            throw new NullPointerException();
            return false; // null 插入不允许

        head = new SinglyNode<>(item, head);
        count++;
        return true;
    }

    public Item remove(final int index) {
        checkElementIndex(index);
        if (index == 0)
            return popFirst();
        final SinglyNode<Item> prev = node(index - 1);
        final Item item = prev.nextNode.item;
        prev.nextNode = prev.nextNode.nextNode;
        count--;
        return item;
    }

    /**
     * 检查位置是否存在元素
     *
     * @param index 位置
     */
    private void checkElementIndex(final int index) {
        if (!(index >= 0 && index < count))
            throw new IndexOutOfBoundsException("越界");
    }

    public Item popFirst() {
        if (head == null)
            throw new ArrayIndexOutOfBoundsException("链表为空");

        final Item item = head.item;
        head = head.nextNode;
        count--;
        return item;
    }

    /**
     * 获取第i个节点，确保调用时index存在
     *
     * @param index 第几个
     *
     * @return 节点
     */
    private SinglyNode<Item> node(final int index) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < index; i++)
            head = head.nextNode;
        return head;
    }

    public void remove(final Item item) {
        remove(indexOf(item));
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count <= 0;
    }

    public boolean contains(final Item o) {
        return indexOf(o) != -1;
    }

    public void clear() {
        while (head != null)
            popFirst();
    }

    public int indexOf(final Item item) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < count; i++) {
            if (head.item.equals(item))
                return i;
            head = head.nextNode;
        }
        return -1;
    }

    public Item get(final int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    public void set(final int index, final Item element) {
        checkElementIndex(index);
        node(index).item = element;
    }

    public void add(final int index, final Item element) {
        checkPositionIndex(index);
        final SinglyNode<Item> prev = node(index - 1);
        prev.nextNode = new SinglyNode<>(element, prev.nextNode);
    }

    /**
     * 检查是否是可插入位置
     *
     * @param index 位置
     */
    private void checkPositionIndex(final int index) {
        if (!(index >= 0 && index <= count))
            throw new IndexOutOfBoundsException("越界");
    }

    @Override
    public String toString() {
        int iMax = count;
        if (head == null || iMax == 0)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        SinglyNode<Item> node = this.head;
        b.append(node.item);
        node = node.nextNode;
        for (int i = 0; i <= iMax; i++, node = node.nextNode) {
            if (node == null)
                return b.append(']').toString();
            b.append(", ");
            b.append(node.item);
        }
        return b.append(']').toString();
    }

    final static private class SinglyNode<T> {
        private SinglyNode<T> nextNode;
        private T item;

        SinglyNode(final T item, final SinglyNode<T> next_node) {
            this.nextNode = next_node;
            this.item = item;
        }
    }
}
