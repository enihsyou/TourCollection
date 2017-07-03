public class SinglyLinkedList<Item> {
    private SinglyNode<Item> head;
    private int count;

    public SinglyLinkedList() { }

    public static void main(String[] args) {
        SinglyLinkedList<Integer> linkedList = new SinglyLinkedList<>();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.remove(2);
        System.out.println(linkedList);
    }

    public void add(final Item item) {
        if (item == null) { throw new NullPointerException(); }
        head = new SinglyNode<>(item, head);
        count++;
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

    private void checkElementIndex(final int index) {
        if (!isElementIndex(index)) { throw new IndexOutOfBoundsException(); }
    }

    public Item popFirst() {
        if (head == null) { throw new ArrayIndexOutOfBoundsException(); }
        final Item item = head.getItem();
        head = head.getNextNode();
        count--;
        return item;
    }

    private SinglyNode<Item> node(final int index) {
        SinglyNode<Item> head = this.head;
        for (int i = 0; i < index; i++) {
            head = head.getNextNode();
        }
        return head;
    }

    private boolean isElementIndex(final int index) {
        return index >= 0 && index < count;
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
            if (head.getItem().equals(o)) { return true; }
            head = head.getNextNode();
        }
        return false;
    }

    public void clear() {
        while (head != null) { popFirst(); }
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

    private void checkPositionIndex(final int index) {
        if (!isPositionIndex(index)) { throw new IndexOutOfBoundsException(); }
    }

    private boolean isPositionIndex(final int index) {
        return index >= 0 && index <= count;
    }

    final static private class SinglyNode<T> {
        private SinglyNode<T> nextNode;
        private T item;

        public SinglyNode(final T item) {
            this.item = item;
        }

        public SinglyNode(final T item, final SinglyNode<T> next_node) {
            this.nextNode = next_node;
            this.item = item;
        }

        public SinglyNode<T> getNextNode() {
            return nextNode;
        }

        public void setNextNode(final SinglyNode<T> nextNode) {
            this.nextNode = nextNode;
        }

        public T getItem() {
            return item;
        }

        public void setItem(final T item) {
            this.item = item;
        }

    }
}
