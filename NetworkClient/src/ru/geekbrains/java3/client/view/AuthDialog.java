package ru.geekbrains.java3.client.view;

import ru.geekbrains.java3.client.controller.ClientController;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AuthDialog extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtLogin;
    private JPasswordField txtPassword;

    private ClientController controller;

    public AuthDialog(ClientController controller) {
        this.controller = controller;
        setContentPane(contentPane);
        //getRootPane().setDefaultButton(buttonOK);
        setSize(400,250);
        setLocationRelativeTo(null);

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
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
      String login = txtLogin.getText().trim();
      String password = new String(txtPassword.getPassword()).trim();
        try {
            controller.sendAuthMessage(login,password);
        } catch (IOException e) {
           showError("Ошибка при попытке аутентификации");
        }

    }

    private void onCancel() {
        System.exit(0);
    }

    public void showError(String err_message) {
        JOptionPane.showMessageDialog(this,
                err_message);
    }
}
