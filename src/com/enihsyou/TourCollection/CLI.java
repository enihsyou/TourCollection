package com.enihsyou.TourCollection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CLI {
    final private BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
    //    final private Console stream = System.console(); // 最后inline化
    /**
     * B-Tree类型结构用于保存所有旅行团相关数据
     */
    final private Tree<Tour> tree;
    /**
     * 单链表保存这个类使用的已添加的游客的信息
     */
    final private SinglyLinkedList<Tourist> tourists;

    private CLI() {
        tree = new BTree<>();
        tourists = new SinglyLinkedList<>();
        tourists.add(new Tourist("001", "游客A", Gender.FEMALE, 1));
        tourists.add(new Tourist("002", "游客B", Gender.FEMALE, 2));
        tourists.add(new Tourist("003", "游客C", Gender.FEMALE, 3));
        tourists.add(new Tourist("004", "游客D", Gender.FEMALE, 4));
        tourists.add(new Tourist("005", "游客E", Gender.FEMALE, 5));
        tourists.add(new Tourist("006", "游客F", Gender.FEMALE, 6));
        tourists.add(new Tourist("007", "游客G", Gender.FEMALE, 7));
        tourists.add(new Tourist("008", "游客H", Gender.FEMALE, 8));
        tree.insertOrReplace(new Tour("北京", new Date(2017, 7, 3)));
        tree.insertOrReplace(new Tour("上海", new Date(2017, 7, 3)));
        tree.insertOrReplace(new Tour("长春", new Date(2017, 7, 4)));
        tree.insertOrReplace(new Tour("狮子", new Date(2017, 7, 5)));
        tree.insertOrReplace(new Tour("大连", new Date(2017, 7, 5)));
        tree.insertOrReplace(new Tour("吉林", new Date(2017, 7, 5)));
        tree.insertOrReplace(new Tour("黄山", new Date(2017, 7, 1)));
        printMenu();
        while (true) {
            try {
                choiceMenu();
            } catch (NumberFormatException ignored) {
                System.err.println("无法解析的输入");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                printMenu();
            }
        }
    }

    private static void printMenu() {
        System.out.println("菜单：");
        System.out.println("1. 创建旅行团");
        System.out.println("2. 创建旅客");
        System.out.println("3. 旅客参团");
        System.out.println("4. 旅客退团");
        System.out.println("5. 删除旅行团");
        System.out.println("6. 打印状态");
        System.out.println("9. 退出");
        System.out.println();
    }

    private void choiceMenu() throws Exception {
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
            case 5: // 删除旅行团
                cancelTour();
                break;
            case 6: // 打印状态
                printStatus();
                break;
            case 9:
                System.exit(0);
        }
    }

    static private int getInteger(Produce<Integer> a) throws Exception {
        return a.produce();
    }

    private int parseInteger() throws Exception {
        final String s = parseString();
        return Integer.parseInt(s);
    }

    private String parseString() throws Exception {
        return stream.readLine().trim();
    }

    private void newTour() throws Exception {
        final Tour tour = makeTour();
        if (!tree.has(tour))
            tree.insertOrReplace(tour);
        else
            System.err.println("已存在相同的旅行团");
    }

    private void newTourist() throws Exception {
        final Tourist tourist = makeTourist();
        if (!tourists.contains(tourist))
            tourists.add(tourist);
        else
            System.err.println("已存在相同的旅客");
    }

    /**
     * 游客参团
     * 先指定游客序号再指定旅行团序号，对不存在的序号什么也不做，如果时间冲突给出错误
     * 如果当前旅行团人数大于6人，无法参团，给出有剩余额度的旅行团
     *
     * @throws Exception IOException
     */
    private void joinGroup() throws Exception {
        final int tourist_index = getInteger(() -> {
            System.out.print("输入旅客序号：");
            return parseInteger() - 1;
        });
        if (tourist_index >= tourists.size() || tourist_index < 0) {
            System.err.println("没有这个序号的游客");
            return;
        }

        final int trip_index = getInteger(() -> {
            System.out.print("输入要参加的旅行团序号：");
            return parseInteger() - 1;
        });
        if (trip_index >= tree.elementCount() || trip_index < 0) {
            System.err.println("没有这个序号的旅行团");
            return;
        }

        final Tour tour = tree.getIndex(trip_index);
        final Tourist tourist = tourists.get(tourist_index);

        if (tour.contain(tourist)) {
            System.err.println("旅客已经加入了");
        } else if (tour.getGuestsCount() >= 6) {
            System.err.format("%s已经超出6人，提供有剩余额度的旅行团\n", tour);
            SinglyLinkedList<Tour> list = new SinglyLinkedList<>();
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

    /**
     * 游客退团
     * 先指定游客序号再指定旅行团序号，对不存在的序号什么也不做，游客如果不在指定的旅行团里也不做
     * 如果当前旅行团人数小于等于3人，退出会连同旅行团一起删除，给出有剩余额度的旅行团
     *
     * @throws Exception IOException
     */
    private void leaveGroup() throws Exception {
        final int tourist_index = getInteger(() -> {
            System.out.print("输入旅客序号：");
            return parseInteger() - 1;
        });
        if (tourist_index >= tourists.size() || tourist_index < 0) {
            System.err.println("没有这个序号的游客");
            return;
        }

        final int trip_index = getInteger(() -> {
            System.out.print("输入要退出的旅行团序号：");
            return parseInteger() - 1;
        });
        if (trip_index >= tree.elementCount() || trip_index < 0) {
            System.err.println("没有这个序号的旅行团");
            return;
        }

        final Tour tour = tree.getIndex(trip_index);
        final Tourist tourist = tourists.get(tourist_index);
        if (!tour.contain(tourist)) {
            System.err.println("旅客本来就没加入");
        } else if (tour.getGuestsCount() <= 3) {
            System.err.format("%s已经少于3人，提供有剩余额度的旅行团\n", tour);
            SinglyLinkedList<Tour> list = new SinglyLinkedList<>();
            tree.ascend(item -> {
                if (item.getGuestsCount() < 6 && !item.equals(tour))
                    list.add(item);
                return true;
            });
            printMore(list);

            final int ch = getInteger(() -> {
                System.out.print("是否要退出？（1代表是，其他代表否）：");
                return parseInteger();
            });
            // 删除该团
            if (ch == 1) {
                System.out.println("退出并删除旅行团，同行其他旅客也一并退出");
                removeTour(tour);
            }
        } else {
            tour.removeGuest(tourist);
            tourist.removeTour(tour);
        }

    }

    /**
     * 移除旅行团，从树中删除节点，同时把旅客释放
     *
     * @throws Exception IOException
     */
    private void cancelTour() throws Exception {
        final int trip_index = getInteger(() -> {
            System.out.print("输入要移除的旅行团序号：");
            return parseInteger() - 1;
        });
        if (trip_index >= tree.elementCount() || trip_index < 0) {
            System.err.println("没有这个序号的旅行团");
            return;
        }

        removeTour(tree.getIndex(trip_index));
    }

    /**
     * 展示当前状态
     * 先输出内部存储结构，调用自带的print方法
     * 如果有旅行团便输出，旅行团中若有已参加的游客，输出它的状态，序号按顺序从1开始
     * 如果有游客便输出，游客中若有已参加的旅行团，输出它的状态，序号按顺序从1开始，而不是编号
     * 最后输出总的旅行团和游客数量
     */
    private void printStatus() {
        System.out.println("存储结构：");
        tree.print();

        if (tree.elementCount() > 0) {
            System.out.println();
            System.out.println("旅行团：");
            for (int i = 0; i < tree.elementCount(); i++) {
                final Tour tour = tree.getIndex(i);
                if (tour.getGuestsCount() > 0)
                    System.out.println(
                            String.format("序号%d  %s\n    下列游客已参加：%s\n    共计%d人\n", i + 1, tour, tour.getGuestsString(),
                                    tour.getGuestsCount()));
                else
                    System.out.format("序号%d  %s\n", i + 1, tour);
            }
        }

        if (tourists.size() > 0) {
            System.out.println();
            System.out.println("旅客：");
            for (int i = 0; i < tourists.size(); i++) {
                final Tourist tourist = tourists.get(i);
                if (tourist.getTourCount() > 0)
                    System.out.println(String.format("序号%d  %s\n    已参加下列旅行团：%s\n    共计%d个\n", i + 1, tourist,
                            tourist.getToursString(), tourist.getTourCount()));
                else
                    System.out.format("序号%d  %s\n", i + 1, tourist);
            }
        }

        System.out.format("总计旅行团%d；总计旅客%d\n", tree.elementCount(), tourists.size());
        System.out.println();
    }

    private Tour makeTour() throws Exception {
        final String where = getString(() -> {
            System.out.print("输入旅行团名称：");
            return parseString();
        });
        return new Tour(where, makeDate());
    }

    /**
     * 创建游客对象，包含编号、姓名、性别和年龄字段
     * 确保输入的编号为三位的数字和字母组合，否则设置为当前人数+1，不对编号重叠性进行检测和纠正
     * 输入文本前后的空格会被裁减移除掉
     *
     * @return 新建的Tourist对象
     * @throws Exception IOException
     */
    private Tourist makeTourist() throws Exception {
        final String code = getString(() -> {
            System.out.print("输入三位编号:");
            final String s = parseString();
            if (!s.matches("[\\d\\w]{3}")) {
                final String format = String.format("%03d", tourists.size() + 1);
                System.err.format("不是三位数字字母编号，设置为%s\n", format);
                return format;
            }
            return s;
        });
        final String name = getString(() -> {
            System.out.print("输入旅客姓名:");
            return parseString();
        });
        final Gender gender = getGender(() -> {
            System.out.print("是否为男性（1代表是，其他代表否）:");
            return parseInteger() == 1 ? Gender.MALE : Gender.FEMALE;
        });
        final int age = getInteger(() -> {
            System.out.print("输入旅客年龄：");
            return parseInteger();
        });
        return new Tourist(code, name, gender, age);
    }

    /**
     * 打印列表中的元素，显示有剩余额度的量，使用定值6去减，必须确保数量小于6 否则会出现负数
     *
     * @param list 要展示的链表
     */
    private void printMore(final SinglyLinkedList<Tour> list) {
        for (int i = 0; i < list.size(); i++) {
            final Tour tour = list.get(i);
            System.out.format("剩余额度: %d    %s\n", 6 - tour.getGuestsCount(), tour);
        }
        System.out.println();
    }

    private void removeTour(Tour tour) {
        for (int i = 0; i < tour.getGuestsCount(); i++) {
            final Tourist guest = tour.getGuest(i);
            guest.removeTour(tour);
        }
        tree.delete(tour);
    }

    static private String getString(Produce<String> a) throws Exception {
        return a.produce();
    }

    /**
     * 创建日期对象，不对输入值进行检测和纠正
     *
     * @return 新的Date对象
     * @throws Exception IOException
     */
    private Date makeDate() throws Exception {
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

    static private Gender getGender(Produce<Gender> a) throws Exception {
        return a.produce();
    }

    public static void main(String[] args) throws Exception {
        new CLI();
    }

    @FunctionalInterface
    private interface Produce<K> {
        K produce() throws Exception;
    }
}
