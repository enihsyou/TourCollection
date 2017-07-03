public class Main {

    public static void main(String[] args) {
        // BTree<Tour, Tourist> tree =  new BTree<>();
        BTree<Integer> tree = new BTree<>(3);
//        for (int i = 0; i < 1000; i++) {
//            tree.insertOrReplace(i);
//        }
        tree.insertOrReplace(1);
        tree.insertOrReplace(2);
        tree.insertOrReplace(3);
        tree.insertOrReplace(4);
        tree.insertOrReplace(5);
        tree.insertOrReplace(6);
        tree.insertOrReplace(7);
        tree.insertOrReplace(8);
        tree.insertOrReplace(9);
        tree.insertOrReplace(9);
        tree.insertOrReplace(9);
        tree.insertOrReplace(10);
        tree.insertOrReplace(11);
        tree.insertOrReplace(12);
        tree.insertOrReplace(13);
        tree.insertOrReplace(14);
        tree.insertOrReplace(15);

        //        tree.descendGreaterThan(3, item -> {
        //            System.out.print(item);
        //            return true;
        //        });
        //        tree.print();
        //         for (int i = 0; i < 1000; i++) {
        //             tree.delete(i);
        //         }
        tree.print();
        System.out.println(tree.elementCount());
        System.out.println("Hello World!");
    }
}
