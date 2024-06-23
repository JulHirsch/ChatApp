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
        SwingUtilities.invokeLater(ChatClient::ConnectToServer);
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

    private static String getDisplayMessage(Message message) {
        String displayMessage;
        if (message.getSender().equals("Server")) {
            displayMessage = String.format(message.getText());
        } else if (!message.getReceiver().equals(Message.GLOBAL_RECEIVER)) {
            displayMessage = String.format("%s: %s", message.getCustomName(), message.getText());
        } else {
            displayMessage = String.format("%s (%s): %s", message.getCustomName(), message.getSender(), message.getText());
        }
        return displayMessage;
    }

    private static void ConnectToServer() {
        String address = promptForIPAddress();
        if (address == null) {
            return;
        }

        ConnectionManager connectionManager = new ConnectionManager(address, 3141);
        String clientName = promptForClientName();
        ChatClient chatClient = new ChatClient(connectionManager, clientName);
        connectionManager.setChatClient(chatClient);
        connectionManager.start();
    }

    public void appendMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            processMessage(message, false);
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String text = _inputTextField.getText();
            String recipient = _recipientTextField.getText();
            if (recipient.isEmpty()) {
                recipient = Message.GLOBAL_RECEIVER;
            }
            if (!text.isEmpty()) {
                Message message = new Message(text, _clientName, recipient, "", "");
                _connectionManager.sendMessage(message);
                processMessage(message, true);
                _inputTextField.setText("");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }

    private void initializeGUI() {
        _recipientTextField = new JTextField();
        _recipientTextField.setBorder(BorderFactory.createTitledBorder("Recipient address"));
        _recipientTextField.addKeyListener(this);

        _tabbedPane = new JTabbedPane();
        _tabbedPane.addChangeListener(e -> updateRecipientField());

        addChatTab("Global", Message.GLOBAL_RECEIVER);

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);

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
        if (DoesRecipientAlreadyExist(recipient)) {
            _tabbedPane.setSelectedComponent(_chatAreas.get(recipient).getParent());
            return;
        }

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        _tabbedPane.addTab(title, scrollPane);

        _chatAreas.put(recipient, chatArea);
        _messageHistory.put(recipient, new CopyOnWriteArrayList<>());
    }

    private boolean DoesRecipientAlreadyExist(String recipient) {
        return _chatAreas.containsKey(recipient);
    }

    private void updateRecipientField() {
        int selectedIndex = _tabbedPane.getSelectedIndex();
        String title = _tabbedPane.getTitleAt(selectedIndex);
        _recipientTextField.setText(title.equals("Global") ? "" : title);
    }

    private void processMessage(Message message, boolean isOutgoing) {
        String recipient;
        if (isOutgoing) {
            recipient = message.getReceiver();
        } else {
            recipient = message.getReceiver().equals(Message.GLOBAL_RECEIVER) ? Message.GLOBAL_RECEIVER : message.getSender();
        }

        JTextArea chatArea = _chatAreas.get(recipient);
        if (chatArea == null) {
            addChatTab(recipient, recipient);
            chatArea = _chatAreas.get(recipient);
        }

        String displayMessage = getDisplayMessage(message);
        chatArea.append(displayMessage + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        _messageHistory.get(recipient).add(message);
    }
}