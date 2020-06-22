package ru.geekbrains.java3.client.view;

import ru.geekbrains.java3.client.controller.ClientController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientChat extends JFrame {
    private JPanel pnlMain;
    private JList<String> usersList;
    private JTextField txtMessage;
    private JButton btnSend;
    private JTextArea txtChatArea;
    private JButton btnUpdateNickname;
    private ClientController controller;

    public ClientChat(ClientController controller) {

        this.controller = controller;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setContentPane(pnlMain);
        addListeners();


        addWindowListener(new WindowAdapter() {
        @Override
        public void windowOpened(WindowEvent e) {
            //Загрузить историю чата в окно сообщений
            List<String> history = null;
            try {
                history = controller.getMessageArchive();
            } catch (IOException e1) {
                showError("Ошибка при попытке загрузить историю сообщений!");
            }

            if (history==null) return;

            if (history.size()!=0) {
                uploadMessageHistory(history);
            } else
                showError("История сообщений: нет данных!");
        }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });
    }


    private void addListeners() {
        btnSend.addActionListener(e->sendMessage());
        txtMessage.addActionListener(e->sendMessage());
        btnUpdateNickname.addActionListener(e ->updateNickname());
    }

    private void updateNickname() {
        String newUsername =
                JOptionPane.showInputDialog(
                        "Новый nickname?","newUser");
        if (newUsername==null || newUsername.trim().isEmpty())
            return;
        controller.updateUsername(newUsername.trim());
    }


    private void sendMessage() {
        String message =
                txtMessage.getText().trim();
        if (message.isEmpty())
            return;
        appendOwnMessage(message);

        if (usersList.getSelectedIndex() < 1) {
            controller.sendMessageToAllUsers(message);
        }
        else {
            String username = usersList.getSelectedValue();
            controller.sendPrivateMessage(username, message);
        }

        txtMessage.setText(null);
    }

    public void appendMessage(String message) {
        try { // Сохранить сообщение в истории чата
            controller.saveMessageInArchive(message);
        } catch (IOException e) {
            showError("Не удалось сохранить сообщение в архив чата!");        }
        SwingUtilities.invokeLater(()->{
            txtChatArea.append(message);
            txtChatArea.append(System.lineSeparator());
        });
    }

    private void uploadMessageHistory(List<String> messages) {
        SwingUtilities.invokeLater(()->{
            for (String message : messages) {
                txtChatArea.append(message);
                txtChatArea.append(System.lineSeparator());
            }
        });
    }

    private void appendOwnMessage(String message) {

        appendMessage("Я: "+message);
    }

    public void showError(String err_message) {
        JOptionPane.showMessageDialog(this,
                err_message);
    }

    public void updateUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<String> model =
                    new DefaultListModel<>();
//            model.addAll(users);
            for(String elem:users) {
                model.addElement(elem);
            }
            usersList.setModel(model);
        });
    }

 }

