package com.enihsyou.TourCollection;

public class SinglyLinkedList<Item> {
    private SinglyNode<Item> head;
    private int count;

    public SinglyLinkedList() {
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
     * @return 是否插入成功
     */
    public boolean add(final Item item) {
        if (item == null) {
//            throw new NullPointerException();
            return false; // null 插入不允许
        }
        head = new SinglyNode<>(item, head);
        count++;
        return true;
    }

    public void remove(final Item tourist) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < count; i++) {
            if (head.getItem().equals(tourist)) {
                remove(i);
                return;
            }
            head = head.getNextNode();
        }
    }

    public Item remove(final int index) {// TODO: 2017/6/26  check
        checkElementIndex(index);
        if (index == 0)
            popFirst();
        final SinglyNode<Item> prev = node(index - 1);
        final Item item = prev.getNextNode().getItem();
        prev.setNextNode(prev.getNextNode().getNextNode());
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

        final Item item = head.getItem();
        head = head.getNextNode();
        count--;
        return item;
    }

    /**
     * 获取第i个节点，确保调用时index存在
     *
     * @param index 第几个
     * @return 节点
     */
    private SinglyNode<Item> node(final int index) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < index; i++) {
            head = head.getNextNode();
        }
        return head;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count <= 0;
    }

    public boolean contains(final Item o) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < count; i++) {
            if (head.getItem().equals(o)) {
                return true;
            }
            head = head.getNextNode();
        }
        return false;
    }

    public void clear() {
        while (head != null) {
            popFirst();
        }
    }

    public int indexOf(final Item item) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < count; i++) {
            if (head.getItem().equals(item)) {
                return i;
            }
            head = head.getNextNode();
        }
        return -1;
    }

    public Item get(final int index) {
        checkElementIndex(index);
        return node(index).getItem();
    }

    public void set(final int index, final Item element) {
        checkElementIndex(index);
        node(index).setItem(element);
    }

    public void add(final int index, final Item element) {
        checkPositionIndex(index);
        final SinglyNode<Item> prev = node(index - 1);
        final SinglyNode<Item> new_node = new SinglyNode<>(element, prev.getNextNode());
        prev.setNextNode(new_node);
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
        for (int i = 0; i <= iMax; i++) {
            if (node.nextNode == null) {
                b.append(node.item);
                return b.append(']').toString();
            }
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

        SinglyNode<T> getNextNode() {
            return nextNode;
        }

        void setNextNode(final SinglyNode<T> nextNode) {
            this.nextNode = nextNode;
        }

        T getItem() {
            return item;
        }

        void setItem(final T item) {
            this.item = item;
        }
    }
}
