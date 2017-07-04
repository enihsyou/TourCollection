package com.enihsyou.TourCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;

/**
 * 数据都存在tree上，每次的选择变动，都从树中获取数据，保证每次的同步。
 */
public class GUI {
    final static private String[] COLUMN_NAMES = {"编号", "姓名", "性别", "年龄"};

    final private BTree<Tour> tree;
    final private SinglyLinkedList<Tourist> tourists;
    final private DefaultListModel<Tour> tourListModel;
    final private DefaultListModel<Tourist> touristListModel;
    final private DefaultTableModel memberTableModel;

    private JPanel rootPanel; // 窗口根视图，用作新建对话框的居中基准

    private JTable memberTable;
    private JList<Tour> tourList;
    private JList<Tourist> touristList;

    private JTextField tourSearchInput;
    private JButton performSearchButton;
    private JButton addPlaceButton;
    private JButton removePlaceButton;
    private JButton joinGroupButton;
    private JButton leaveGroupButton;
    private JButton addGuestButton;
    private JButton removeGuestButton;
    private JTextPane informationPanel;


    private GUI() {
        /*初始化*/
        tree = new BTree<>(); // 使用B-Tree模型存储数据
        tourists = new SinglyLinkedList<>(); // 保存已注册的用户信息
        tourListModel = new DefaultListModel<>(); // 旅行地点选择列表数据存储结构
        touristListModel = new DefaultListModel<>(); // 游客选择列表数据存储结构
        memberTableModel = new MyDefaultTableModel(); // 参团人员表格数据存储结构

        tourists.add(new Tourist("001", "a", Gender.FEMALE, 1));
        tourists.add(new Tourist("002", "b", Gender.FEMALE, 2));
        tourists.add(new Tourist("003", "c", Gender.FEMALE, 3));
        tourists.add(new Tourist("004", "d", Gender.FEMALE, 4));
        tourists.add(new Tourist("005", "e", Gender.FEMALE, 5));
        tourists.add(new Tourist("006", "f", Gender.FEMALE, 6));
        tourists.add(new Tourist("007", "g", Gender.FEMALE, 7));
        tourists.add(new Tourist("008", "h", Gender.FEMALE, 8));
        tree.insertOrReplace(new Tour("北京", new Date(1, 2, 3)));
        tree.insertOrReplace(new Tour("上海", new Date(1, 2, 3)));
        tree.insertOrReplace(new Tour("长春", new Date(1, 2, 4)));
        tree.insertOrReplace(new Tour("狮子山", new Date(1, 2, 5)));
        for (int i = 0; i < tourists.size(); i++) {
            Tourist tourist = tourists.get(i);
            touristListModel.addElement(tourist);
        }
        for (int i = 0; i < tree.elementCount(); i++) {
            Tour tour = tree.getIndex(i);
            tourListModel.addElement(tour);
        }
        tourList.setModel(tourListModel);
        touristList.setModel(touristListModel);
        memberTable.setModel(memberTableModel);
        memberTable.setRowSelectionAllowed(false); // 禁止选择表
        updateInformation();

        /*增加地点按钮*/
        addPlaceButton.addActionListener(e -> {
            final AddPlaceDialog dialog = new AddPlaceDialog();
            dialog.setPlaceListener(tour -> {
                if (!tree.has(tour)) { // 如果存在重复的就忽略
                    tree.insertOrReplace(tour); // 增加到存储结构中
                    tourListModel.addElement(tour); // 添加到GUI列表中
                    updateInformation(); // 设置提示信息
                }
            });
            showWindow(dialog, rootPanel);
        });
        /*移除地点按钮*/
        removePlaceButton.addActionListener(e -> {
            final int selected_index = tourList.getSelectedIndex();
            if (selected_index == -1) // 没有选中项
                return;
            final Tour tour = tourList.getSelectedValue();
            if (tour.getGuestsCount() > 0)
                JOptionPane.showMessageDialog(rootPanel, "当前地点还有未退出的旅客，不能移除", "移除失败", JOptionPane.ERROR_MESSAGE);
            else {
                tree.delete(tour); // 从树中删除数据
                tourListModel.remove(selected_index); // 从GUI列表中删除
                updateInformation();
            }
        });
        /*参团按钮*/
        joinGroupButton.addActionListener(e -> {
            final int selected_tour_index = tourList.getSelectedIndex(),
                    selected_tourist_index = touristList.getSelectedIndex();
            if (selected_tour_index == -1) // 没有选中地点项
                return;
            if (selected_tourist_index == -1) // 没有选择用户
                return;
            final Tour selected_trip = tourList.getSelectedValue();
            final Tourist selected_guest = touristList.getSelectedValue();
            if (selected_trip.getGuestsCount() >= 6) { // 处理人数超过6的情况，给出建议
                LinkedList<Tour> list = new LinkedList<>();
                tree.ascend(item -> {
                    if (item.getGuestsCount() < 6)
                        list.add(item);
                    return true;
                });
                StringBuilder builder = new StringBuilder(String.format("%s已经超出6人，提供有剩余额度的旅行团\n", selected_trip));
                for (int i = 0; i < list.size(); i++) {
                    final Tour tour = list.get(i);
                    builder.append(String.format("剩余额度: %d    %s\n", 6 - tour.getGuestsCount(), tour));
                }
                JOptionPane.showMessageDialog(rootPanel, builder.toString(), "人数已满，无法参团", JOptionPane.ERROR_MESSAGE);
            }
            if (!selected_trip.contain(selected_guest)) {
                if (selected_guest.hasCollision(selected_trip))
                    JOptionPane.showMessageDialog(rootPanel, "时间冲突，不能参团", "参团失败", JOptionPane.ERROR_MESSAGE);
                else {
                    selected_trip.addGuest(selected_guest);
                    selected_guest.addTour(selected_trip);
                    memberTableModel.addRow(selected_guest.toArray());
                }
            }
        });
        /*退团按钮*/
        leaveGroupButton.addActionListener(e -> {
            final int selected_tour_index = tourList.getSelectedIndex(),
                    selected_tourist_index = touristList.getSelectedIndex();
            if (selected_tour_index == -1) // 没有选中地点项
                return;
            if (selected_tourist_index == -1) // 没有选择用户
                return;
            final Tour selected_trip = tourList.getSelectedValue();
            final Tourist selected_guest = touristList.getSelectedValue();
            if (selected_trip.getGuestsCount() <= 3) { // 处理人数少于3的情况，取消团队
                LinkedList<Tour> list = new LinkedList<>();
                tree.ascend(item -> {
                    if (item.getGuestsCount() < 6)
                        list.add(item);
                    return true;
                });

                for (int i = 0; i < selected_trip.getGuestsCount(); i++) {
                    selected_trip.removeGuest(selected_trip.getGuest(i));
                }
                memberTableModel.setRowCount(0);
                tree.delete(selected_trip);
                tourListModel.remove(selected_tour_index);
                StringBuilder builder = new StringBuilder(String.format("%s已经少于3人，提供有剩余额度的旅行团，删除该团\n", selected_trip));
                for (int i = 0; i < list.size(); i++) {
                    final Tour tour = list.get(i);
                    builder.append(String.format("剩余额度: %d    %s\n", 6 - tour.getGuestsCount(), tour));
                }
                JOptionPane.showMessageDialog(rootPanel, builder.toString(), "人数不足，删除该团", JOptionPane.ERROR_MESSAGE);
            } else {
                selected_guest.removeTour(selected_trip);
                selected_trip.removeGuest(selected_guest);
                final Tour currentTour = tourList.getSelectedValue();
                memberTableModel.setRowCount(0);
                for (int i = 0; i < currentTour.getGuestsCount(); i++) {
                    memberTableModel.addRow(currentTour.getGuest(i).toArray());
                }
            }
            updateInformation();
        });
        /*在列表中选定一个旅行地点（旅行团）*/
        tourList.addListSelectionListener(e -> {
            if (tourList.getSelectedIndex() == -1) return;
            final Tour currentTour = tourList.getSelectedValue();
            memberTableModel.setRowCount(0);
            for (int i = 0; i < currentTour.getGuestsCount(); i++) {
                memberTableModel.addRow(currentTour.getGuest(i).toArray());
            }
            updateInformation();
        });
        touristList.addListSelectionListener(e -> updateInformation());
        /*新增用户记录*/
        addGuestButton.addActionListener(e -> {
            final MakeCharacterDialog dialog = new MakeCharacterDialog();
            // 预设置用户编号为当前数量+1
            dialog.setCodeNumber(String.format("%03d", tourists.size() + 1));
            dialog.setJoinListener(who -> {
                if (tourists.contains(who))
                    return; // 忽略重复添加
                tourists.add(who); // 增加到记录中
                touristListModel.addElement(who); // 增加到列表GUI中
                updateInformation();
            });
            showWindow(dialog, rootPanel);
        });
        /*移除用户记录*/
        removeGuestButton.addActionListener(e -> {
            if (touristList.getSelectedIndex() == -1)
                return;
            final Tourist tourist = touristList.getSelectedValue();
            if (tourist.getTourCount() > 0)
                JOptionPane.showMessageDialog(rootPanel, "当前旅客还有未退出的旅行团，不能移除", "移除失败", JOptionPane.ERROR_MESSAGE);
            else {
                tourists.remove(tourist); // 从记录中删除
                touristListModel.removeElement(tourist); // 从GUI列表中移除
            }
        });
        /*搜索按钮*/
        performSearchButton.addActionListener(e -> {
            final String search_for = tourSearchInput.getText();
            final Tour search_key = new Tour(search_for);
            if (tree.has(search_key)) {
                tourList.setSelectedIndex(tree.keys(Tree.Direction.ASCEND).indexOf(tree.getNodeItem(search_key)));
            }
        });
        /*搜索框*/
        tourSearchInput.addActionListener(e -> {
            final String search_for = tourSearchInput.getText();
            final Tour search_key = new Tour(search_for);
            if (tree.has(search_key)) {
                tourList.setSelectedIndex(tree.keys(Tree.Direction.ASCEND).indexOf(tree.getNodeItem(search_key)));
            }
        });
    }

    private void updateInformation() {
        final int place_count = tree.elementCount();
        final int guest_count = tourists.size();
        StringBuilder builder = new StringBuilder(String.format("总计%d旅行团；%d游客\n", place_count, guest_count));
        final int selected_tour_index = tourList.getSelectedIndex(),
                selected_tourist_index = touristList.getSelectedIndex();
        if (selected_tour_index != -1) {// 选中地点项
            Tour tour = tourList.getSelectedValue();
            if (tour.getGuestsCount() > 0)
                builder.append(String.format("当前选中%s\n    下列游客已参加：%s\n    共计%d人\n", tour, tour.getGuestsString(), tour.getGuestsCount()));
            else
                builder.append(String.format("当前选中%s\n", tour));
        }
        if (selected_tourist_index != -1) { // 选择成员
            Tourist tourist = touristList.getSelectedValue();
            if (tourist.getTourCount() > 0)
                builder.append(String.format("当前选中%s旅客\n    已参加下列旅行团：%s\n    共计%d个\n", tourist, tourist.getToursString(), tourist.getTourCount()));
            else
                builder.append(String.format("当前选中%s旅客\n", tourist));
        }
        informationPanel.setText(builder.toString());
    }

    private static void showWindow(final Window window, final Container relative_to) {
        window.pack();
        window.setLocationRelativeTo(relative_to);
        window.setVisible(true);
    }

    public static void main(final String[] args) {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        showWindow(frame, null);
    }

    private static class MyDefaultTableModel extends DefaultTableModel {
        public MyDefaultTableModel() {
            super(COLUMN_NAMES, 0);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false; // 禁止编辑表格单元格
        }
    }

    @FunctionalInterface
    public interface AddPlaceListener {
        void addPlace(Tour tour);
    }

    @FunctionalInterface
    public interface JoinGroupListener {
        void joinGroup(Tourist who);
    }
}
