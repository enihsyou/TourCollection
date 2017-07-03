import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MakeCharacterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField guestName;
    private JTextField guestAge;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JTextField guestCode;
    private JoinGroupListener joinGroupListener;

    public MakeCharacterDialog() {
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
        final String guest_name = guestName.getText();
        final String guest_age_text = guestAge.getText();
        final String guest_code_text = guestCode.getText();
        final Gender guest_gender = maleRadioButton.isSelected() ? Gender.MALE : Gender.FEMALE;

        int guest_age = -1;
        try {
            guest_age = Integer.parseInt(guest_age_text);
            if (guest_age < 0)
                throw new RuntimeException();
            if (joinGroupListener != null)
                joinGroupListener.joinGroup(new Tourist(guest_code_text, guest_name, guest_gender, guest_age));

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
        MakeCharacterDialog dialog = new MakeCharacterDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setJoinListener(final JoinGroupListener joinGroupListener) {
        this.joinGroupListener = joinGroupListener;
    }

    public void setCodeNumber(final String s) {
        guestCode.setText(s);
    }
}
