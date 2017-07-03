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
    final private LinkedList<Tourist> guests;
    final private DefaultListModel<Tour> tourListModel;
    final private DefaultListModel<Tourist> guestListModel;
    final private DefaultTableModel memberTableModel;

    private JPanel rootPanel; // 窗口根视图，用作新建对话框的居中基准

    private JTable memberTable;
    private JList<Tour> tourList;
    private JList<Tourist> guestList;

    private JTextField tourSearchInput;
    private JButton performSearchButton;
    private JButton addPlaceButton;
    private JButton removePlaceButton;
    private JButton joinGroupButton;
    private JButton leaveGroupButton;
    private JButton addGuestButton;
    private JButton removeGuestButton;

    private JLabel groupInformation;


    private GUI() {
        /*初始化*/
        tree = new BTree<>(); // 使用B-Tree模型存储数据
        guests = new LinkedList<>(); // 保存已注册的用户信息
        tourListModel = new DefaultListModel<>(); // 旅行地点选择列表数据存储结构
        guestListModel = new DefaultListModel<>(); // 游客选择列表数据存储结构
        memberTableModel = new MyDefaultTableModel(); // 参团人员表格数据存储结构

        tourList.setModel(tourListModel);
        guestList.setModel(guestListModel);
        memberTable.setModel(memberTableModel);
        memberTable.setRowSelectionAllowed(false); // 禁止选择表
//        memberTable.setAutoCreateRowSorter(true); // 排序表头
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
            final int selected_trip_index = tourList.getSelectedIndex(),
                    selected_guest_index = guestList.getSelectedIndex();
            if (selected_trip_index == -1) // 没有选中地点项
                return;
            if (selected_guest_index == -1) // 没有选择用户
                return;
            final Tour selected_trip = tourList.getSelectedValue();
            final Tourist selected_guest = guestList.getSelectedValue();
            if (selected_trip.getGuestsCount() >= 6) { // 处理人数超过6的情况，给出建议
                LinkedList<Tour> list = new LinkedList<>();
                tree.ascend(item -> {
                    if (item.getGuestsCount() < 6)
                        list.add(item);
                    return true;
                });
                JOptionPane.showMessageDialog(rootPanel, list.toString());
            }
            if (!selected_trip.contain(selected_guest)) {
                if (selected_guest.hasCollision(selected_trip))
                    JOptionPane.showMessageDialog(rootPanel, "时间冲突，不能参团", "添加失败", JOptionPane.ERROR_MESSAGE);
                else {
                    selected_trip.addGuest(selected_guest);
                    selected_guest.addTour(selected_trip);
                    memberTableModel.addRow(selected_guest.toArray());
                }
            }
        });
        /*退团按钮*/
        leaveGroupButton.addActionListener(e -> {
            final int selected_list_index = tourList.getSelectedIndex(),
                    selected_table_row = memberTable.getSelectedRow();
            if (selected_list_index == -1) // 没有选中地点项
                return;
            if (selected_table_row == -1) // 没有选择成员
                return;
            final Tour selected_trip = tourList.getSelectedValue();
            final Tourist selected_guest = guestList.getSelectedValue();
            if (selected_trip.getGuestsCount() <= 3) { // 处理人数少于3的情况，取消团队
                LinkedList<Tour> list = new LinkedList<>();
                tree.ascend(item -> {
                    if (item.getGuestsCount() < 6)
                        list.add(item);
                    return true;
                });
                JOptionPane.showMessageDialog(rootPanel, list.toString());
                for (int i = 0; i < selected_trip.getGuestsCount(); i++) {
                    selected_trip.removeGuest(selected_trip.getGuest(i));
                }
                memberTableModel.setRowCount(0);
                tree.delete(selected_trip);
                tourListModel.remove(selected_list_index);
            } else {
                selected_guest.removeTour(selected_trip);
                selected_trip.removeGuest(selected_guest);
                memberTableModel.removeRow(selected_table_row);
            }
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
        /*新增用户记录*/
        addGuestButton.addActionListener(e -> {
            final MakeCharacterDialog dialog = new MakeCharacterDialog();
            // 预设置用户编号为当前数量+1
            dialog.setCodeNumber(String.format("%03d", guests.size() + 1));
            dialog.setJoinListener(who -> {
                if (guests.contains(who))
                    return; // 忽略重复添加
                guests.add(who); // 增加到记录中
                guestListModel.addElement(who); // 增加到列表GUI中
                updateInformation();
            });
            showWindow(dialog, rootPanel);
        });
        /*移除用户记录*/
        removeGuestButton.addActionListener(e -> {
            if (guestList.getSelectedIndex() == -1)
                return;
            final Tourist tourist = guestList.getSelectedValue();
            if (tourist.getTourCount() > 0)
                JOptionPane.showMessageDialog(rootPanel, "当前旅客还有未退出的旅行团，不能移除", "移除失败", JOptionPane.ERROR_MESSAGE);
            else {
                guests.remove(tourist); // 从记录中删除
                guestListModel.removeElement(tourist); // 从GUI列表中移除
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
        final int guest_count = guests.size();
        groupInformation.setText(String.format("总计%d旅行团；%d游客", place_count, guest_count));
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
            super(GUI.COLUMN_NAMES, 0);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false; // 禁止编辑表格单元格
        }
    }
}
