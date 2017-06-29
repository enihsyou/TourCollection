public class Main {

    public static void main(String[] args) {
        // BTree<Tour, Guest> tree =  new BTree<>();
        BTree<Integer, Integer> tree = new BTree<>();
        tree.insertOrReplace(1, 2);
        tree.insertOrReplace(2, 2);
        tree.insertOrReplace(3, 2);
        tree.insertOrReplace(4, 2);
        tree.insertOrReplace(5, 2);
        tree.insertOrReplace(6, 2);
        tree.insertOrReplace(7, 2);
        tree.insertOrReplace(8, 2);
        tree.insertOrReplace(9, 2);
        tree.insertOrReplace(10, 2);
        tree.insertOrReplace(11, 2);
        tree.insertOrReplace(12, 2);
        tree.insertOrReplace(13, 2);
        tree.insertOrReplace(14, 2);
        tree.insertOrReplace(15, 2);

        tree.descendGreaterThan(3, item -> {
            System.out.println(item);
            return true;
        });
        tree.print();
        System.out.println("Hello World!");
    }
}
