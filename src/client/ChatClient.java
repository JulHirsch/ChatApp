package client;

import common.Message;
import common.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatClient extends JFrame implements KeyListener {
    private final ConnectionManager _connectionManager;
    private final String _clientName;

    private JTabbedPane _tabbedPane;
    private JTextField _inputTextField;
    private JTextField _recipientTextField;
    private Map<String, JTextArea> _chatAreas;
    private Map<String, List<Message>> _messageHistory;

    public ChatClient(ConnectionManager connectionManager, String clientName) {
        super("Chat");
        _connectionManager = connectionManager;
        _clientName = clientName;
        _chatAreas = new HashMap<>();
        _messageHistory = new HashMap<>();
        initializeGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String address = promptForIPAddress();
            if (address != null) {
                ConnectionManager connectionManager = new ConnectionManager(address, 3141);
                String clientName = promptForClientName();
                ChatClient chatClient = new ChatClient(connectionManager, clientName);
                connectionManager.setChatClient(chatClient);
                connectionManager.start();
            }
        });
    }

    private static String promptForIPAddress() {
        String address = JOptionPane.showInputDialog("IP address");
        while (address != null && !Utils.isValidIPAddress(address)) {
            JOptionPane.showMessageDialog(null, "Invalid IP address format");
            address = JOptionPane.showInputDialog("IP address");
        }
        return address;
    }

    private static String promptForClientName() {
        String name = JOptionPane.showInputDialog("Your name");
        while (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty");
            name = JOptionPane.showInputDialog("Your name");
        }
        return name;
    }

    private void initializeGUI() {
        _recipientTextField = new JTextField();
        _recipientTextField.setBorder(BorderFactory.createTitledBorder("Recipient address"));
        _recipientTextField.addKeyListener(this);

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);

        _tabbedPane = new JTabbedPane();
        _tabbedPane.addChangeListener(e -> updateRecipientField());

        addChatTab("Global", Message.GLOBAL_RECEIVER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(_recipientTextField, BorderLayout.NORTH);
        inputPanel.add(_inputTextField, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(_tabbedPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addChatTab(String title, String recipient) {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        _tabbedPane.addTab(title, scrollPane);

        _chatAreas.put(recipient, chatArea);
        _messageHistory.put(recipient, new CopyOnWriteArrayList<>());
    }

    private void updateRecipientField() {
        int selectedIndex = _tabbedPane.getSelectedIndex();
        String title = _tabbedPane.getTitleAt(selectedIndex);
        _recipientTextField.setText(title.equals("Global") ? "" : title);
    }

    public void appendMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            String recipient = message.getReceiver().equals(Message.GLOBAL_RECEIVER) ? Message.GLOBAL_RECEIVER : message.getSender();
            JTextArea chatArea = _chatAreas.get(recipient);
            if (chatArea == null) {
                addChatTab(recipient, recipient);
                chatArea = _chatAreas.get(recipient);
            }

            String displayMessage = getDisplayMessage(message);
            chatArea.append(displayMessage + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            _messageHistory.get(recipient).add(message);
        });
    }

    private static String getDisplayMessage(Message message) {
        String displayMessage;
        if (message.getSender().equals("Server")) {
            displayMessage = String.format("Server: %s", message.getText());
        } else if (!message.getReceiver().equals(Message.GLOBAL_RECEIVER)) {
            displayMessage = String.format("Private message from %s (%s) to %s: %s", message.getCustomName(), message.getSender(), message.getReceiver(), message.getText());
        } else {
            displayMessage = String.format("%s (%s): %s", message.getCustomName(), message.getSender(), message.getText());
        }
        return displayMessage;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String text = _inputTextField.getText();
            int selectedIndex = _tabbedPane.getSelectedIndex();
            String recipient = _tabbedPane.getTitleAt(selectedIndex).equals("Global") ? Message.GLOBAL_RECEIVER : _tabbedPane.getTitleAt(selectedIndex);
            if (!text.isEmpty()) {
                Message message = new Message(text, _clientName, recipient, "", "");
                _connectionManager.sendMessage(message);
                _inputTextField.setText("");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }
}