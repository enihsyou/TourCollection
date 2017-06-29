public class Main {

    public static void main(String[] args) {
        // BTree<Tour, Guest> tree =  new BTree<>();
        BTree<Integer, Integer> tree = new BTree<>();
        tree.insertOrReplace(1, 1);
        tree.insertOrReplace(13, 2);
        tree.insertOrReplace(3, 1);
        tree.insertOrReplace(2, 1);
        tree.insertOrReplace(12, 2);
        tree.insertOrReplace(5, 1);
        tree.insertOrReplace(8, 1);
        tree.insertOrReplace(10, 2);
        tree.insertOrReplace(6, 1);
        tree.insertOrReplace(9, 1);
        tree.insertOrReplace(4, 1);
        tree.insertOrReplace(15, 2);
        tree.insertOrReplace(7, 1);
        tree.insertOrReplace(11, 2);
        tree.insertOrReplace(14, 2);
        tree.descendGreaterThan(3, item -> {
            System.out.println(item);
            return true;
        });
        System.out.println("Hello World!");
    }
}
