package com.enihsyou.TourCollection;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddPlaceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField placeInput;
    private JTextField yearInput;
    private JTextField monthInput;
    private JTextField dayInput;
    private GUI.AddPlaceListener placeListener;

    public AddPlaceDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        final String place = placeInput.getText();
        try {
            final int year = Integer.parseInt(yearInput.getText());
            final int month = Integer.parseInt(monthInput.getText());
            final int day = Integer.parseInt(dayInput.getText());

            if (placeListener != null)
                placeListener.addPlace(new Tour(place, new Date(year, month, day)));
        } catch (NumberFormatException e) {
            //无法解析时间字符串
            e.printStackTrace();
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AddPlaceDialog dialog = new AddPlaceDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setPlaceListener(final GUI.AddPlaceListener placeListener) {
        this.placeListener = placeListener;
    }
}
