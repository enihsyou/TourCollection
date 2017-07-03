import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class CLI {
    final private BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
    final private BTree<Tour> tree = new BTree<>();
    final private LinkedList<Tourist> tourists = new LinkedList<>();

    private CLI() {
        tourists.add(new Tourist("001", "a", Gender.FEMALE, 1));
        tourists.add(new Tourist("002", "b", Gender.FEMALE, 2));
        tourists.add(new Tourist("003", "c", Gender.FEMALE, 3));
        tourists.add(new Tourist("004", "d", Gender.FEMALE, 4));
        tourists.add(new Tourist("005", "e", Gender.FEMALE, 5));
        tourists.add(new Tourist("006", "f", Gender.FEMALE, 6));
        tourists.add(new Tourist("007", "g", Gender.FEMALE, 7));
        tourists.add(new Tourist("008", "h", Gender.FEMALE, 8));
        tree.insertOrReplace(new Tour("A", new Date(1, 2, 3)));
        tree.insertOrReplace(new Tour("B", new Date(1, 2, 3)));
        tree.insertOrReplace(new Tour("C", new Date(1, 2, 4)));
        tree.insertOrReplace(new Tour("D", new Date(1, 2, 5)));
        printMenu();
        while (true) {
            try {
                choiceMenu();
            } catch (IOException ignored) { }
        }
    }

    private static void printMenu() {
        System.out.println("菜单：");
        System.out.println("1. 创建旅行团");
        System.out.println("2. 创建旅客");
        System.out.println("3. 旅客参团");
        System.out.println("4. 旅客退团");
        System.out.println("5. 打印状态");
        System.out.println("9. 退出");
    }

    private void choiceMenu() throws IOException {
        final int ch = getInteger(() -> {
            System.out.print("输入菜单选项：");
            return parseInteger();
        });
        switch (ch) {
            case 1: // 创建旅行团
                newTour();
                break;
            case 2: // 创建旅客
                newTourist();
                break;
            case 3: // 旅客参团
                joinGroup();
                break;
            case 4: // 旅客退团
                leaveGroup();
                break;
            case 5: // 打印状态
                printStatus();
                break;
            case 9:
                System.exit(0);
        }
        printMenu();
    }

    static private int getInteger(Produce<Integer> a) throws IOException {
        return a.produce();
    }

    private int parseInteger() throws IOException {
        final String s = stream.readLine();
        return Integer.parseInt(s);
    }

    private void newTour() throws IOException {
        final Tour tour = makeTour();
        if (!tree.has(tour))
            tree.insertOrReplace(tour);
        else
            System.err.println("已存在相同的旅行团");
    }

    private void newTourist() throws IOException {
        final Tourist tourist = makeTourist();
        if (!tourists.contains(tourist))
            tourists.add(tourist);
        else
            System.err.println("已存在相同的旅客");
    }

    private void joinGroup() throws IOException {
        final int tourist_index = getInteger(() -> {
            System.out.print("输入旅客序号：");
            return parseInteger();
        });
        final int trip_index = getInteger(() -> {
            System.out.print("输入要参加的旅行团序号：");
            return parseInteger();
        });
        final Tourist tourist = tourists.get(tourist_index - 1);
        final Tour tour = tree.getIndex(trip_index - 1);
        if (tour.contain(tourist)) {
            System.err.println("旅客已经加入了");
        } else if (tour.getGuestsCount() >= 6) {
            LinkedList<Tour> list = new LinkedList<>();
            tree.ascend(item -> {
                if (item.getGuestsCount() < 6)
                    list.add(item);
                return true;
            });
            printMore(list);
        } else if (tourist.hasCollision(tour)) {
            System.err.println("时间冲突");
        } else {
            tour.addGuest(tourist);
            tourist.addTour(tour);
        }
    }

    private void leaveGroup() throws IOException {
        final int tourist_index = getInteger(() -> {
            System.out.print("输入旅客序号：");
            return parseInteger() - 1;
        });
        final int trip_index = getInteger(() -> {
            System.out.print("输入要退出的旅行团序号：");
            return parseInteger() - 1;
        });
        final Tourist tourist = tourists.get(tourist_index);
        final Tour tour = tree.getIndex(trip_index);
        if (!tour.contain(tourist)) {
            System.err.println("旅客本来就没加入");
        } else if (tour.getGuestsCount() <= 3) {
            LinkedList<Tour> list = new LinkedList<>();
            tree.ascend(item -> {
                if (item.getGuestsCount() < 6)
                    list.add(item);
                return true;
            });
            printMore(list);
        } else {
            tour.removeGuest(tourist);
            tourist.removeTour(tour);
        }

    }

    private void printStatus() {
        System.out.println("存储结构：");
        tree.print();

        if (tree.elementCount() > 0) {
            System.out.println("旅行团：");
            final LinkedList<Tour> keys = tree.keys(Tree.Direction.ASCEND);
            for (int i = 0; i < keys.size(); i++) {
                final Tour tour = keys.get(i);
                if (tour.getGuestsCount() > 0)
                    System.out.println(
                        String.format("序号%d  %s\n    下列游客已参加：%s\n    共计%d人", i + 1, tour, tour.getGuestsString(),
                            tour.getGuestsCount()));
                else
                    System.out.println(String.format("序号%d  %s", i + 1, tour));
            }
        }

        if (tourists.size() > 0) {
            System.out.println("旅客：");
            for (int i = 0; i < tourists.size(); i++) {
                final Tourist tourist = tourists.get(i);
                if (tourist.getTourCount() > 0)
                    System.out.println(
                        String.format("序号%d  %s\n    已参加下列旅行团：%s\n    共计%d个", i + 1, tourist, tourist.getToursString(),
                            tourist.getTourCount()));
                else
                    System.out.println(String.format("序号%d  %s", i + 1, tourist));
            }
        }

        System.out.println(String.format("总计旅行团%d；总计旅客%d", tree.elementCount(), tourists.size()));
    }

    private Tour makeTour() throws IOException {
        final String where = getString(() -> {
            System.out.print("输入旅行团名称：");
            return stream.readLine();
        });
        return new Tour(where, makeDate());
    }

    private Tourist makeTourist() throws IOException {
        final String code = getString(() -> {
            System.out.print("输入三位编号:");
            return stream.readLine();
        });
        final String name = getString(() -> {
            System.out.print("输入旅客姓名:");
            return stream.readLine();
        });
        final Gender gender = getGender(() -> {
            System.out.print("是否是男性（1代表是）:");
            return parseInteger() == 1 ? Gender.MALE : Gender.FEMALE;
        });
        final int age = getInteger(() -> {
            System.out.print("输入旅客年龄：");
            return parseInteger();
        });
        return new Tourist(code, name, gender, age);
    }

    private void printMore(final LinkedList<Tour> list) {
        for (Tour tour : list) {
            System.out.println(String.format("剩余额度: %d    %s", 6 - tour.getGuestsCount(), tour));
        }
    }

    static private String getString(Produce<String> a) throws IOException {
        return a.produce();
    }

    private Date makeDate() throws IOException {
        final int year = getInteger(() -> {
            System.out.print("输入出发年份：");
            return parseInteger();
        });
        final int month = getInteger(() -> {
            System.out.print("输入出发月份：");
            return parseInteger();
        });
        final int day = getInteger(() -> {
            System.out.print("输入出发日份：");
            return parseInteger();
        });
        return new Date(year, month, day);
    }

    static private Gender getGender(Produce<Gender> a) throws IOException {
        return a.produce();
    }

    public static void main(String[] args) throws IOException {
        new CLI();
    }
}
