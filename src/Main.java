public class Main {

    public static void main(String[] args) {
        // BTree<Tour, Guest> tree =  new BTree<>();
        BTree<Integer, Integer> tree = new BTree<>();
        tree.insertOrReplaceElement(1, 1);
        tree.insertOrReplaceElement(2, 1);
        tree.insertOrReplaceElement(3, 1);
        tree.insertOrReplaceElement(4, 1);
        tree.insertOrReplaceElement(5, 1);
        tree.insertOrReplaceElement(6, 1);
        tree.insertOrReplaceElement(7, 1);
        tree.insertOrReplaceElement(8, 1);
        tree.insertOrReplaceElement(9, 1);
        tree.insertOrReplaceElement(10, 2);
        tree.insertOrReplaceElement(11, 2);
        tree.insertOrReplaceElement(12, 2);
        tree.insertOrReplaceElement(13, 2);
        tree.insertOrReplaceElement(14, 2);
        tree.insertOrReplaceElement(15, 2);
        tree.delete(12);
        System.out.println("Hello World!");
    }
}
