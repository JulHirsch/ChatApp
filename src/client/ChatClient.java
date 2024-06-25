package client;

import common.Messages.BaseMessage;
import common.Messages.TextMessage;
import common.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatClient extends JFrame implements KeyListener, IChatClient {
    public static final String GLOBAL_TAB_NAME = "global";
    private final ConnectionManager _connectionManager;
    private final String _clientName;

    private JTabbedPane _tabbedPane;
    private JTextField _inputTextField;
    private JTextField _recipientTextField;
    private Map<String, JTextArea> _chatAreas;
    private Map<String, List<TextMessage>> _messageHistory;
    private boolean _isInitialized = false;

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

    private static String getDisplayMessage(TextMessage message) {
        String displayMessage;
        if (message.getSender().equals("Server")) {
            displayMessage = String.format(message.getText());
        } else if (!message.isGlobal()) {
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

    public void appendMessage(TextMessage message) {
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
                recipient = BaseMessage.GLOBAL_RECEIVER;
            }
            if (!text.isEmpty()) {
                BaseMessage message = new TextMessage(text, recipient, "", _clientName);
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
        _recipientTextField.setVisible(false); // Hide initially

        _tabbedPane = new JTabbedPane();
        _tabbedPane.addChangeListener(e -> updateRecipientField());

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);

        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> addNewChatTab());

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.add(_tabbedPane, BorderLayout.CENTER);
        tabPanel.add(addButton, BorderLayout.EAST);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(_recipientTextField, BorderLayout.NORTH);
        inputPanel.add(_inputTextField, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(tabPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        _isInitialized = true;
    }

    private void addNewChatTab() {
        String recipient = JOptionPane.showInputDialog("Enter recipient name");
        if (recipient != null && !recipient.trim().isEmpty()) {
            String[] encryptionOptions = {"None", "RSA", "AES"};
            String encryptionType = (String) JOptionPane.showInputDialog(null, "Select Encryption Type",
                    "Encryption", JOptionPane.QUESTION_MESSAGE, null, encryptionOptions, encryptionOptions[0]);

            String encryptionKey = null;
            if (!encryptionType.equals("None")) {
                encryptionKey = JOptionPane.showInputDialog("Enter Encryption Key");
            }

            // Store encryption settings for the new chat
            //_encryptionTypes.put(recipient, encryptionType);
            //_encryptionKeys.put(recipient, encryptionKey);

            // Add chat tab and select it
            addChatTab(recipient, recipient);
            _tabbedPane.setSelectedIndex(_tabbedPane.getTabCount() - 1); // Select the newly added tab
        } else {
            _tabbedPane.setSelectedIndex(0); // Go back to global tab if creation is cancelled
        }
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
        if (!_isInitialized) return;

        int selectedIndex = _tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            String title = _tabbedPane.getTitleAt(selectedIndex);
            _recipientTextField.setText(title.equals(GLOBAL_TAB_NAME) ? "" : title);
            boolean isGlobal = title.equals(GLOBAL_TAB_NAME);
            _recipientTextField.setVisible(!isGlobal);
        }
    }

    private void processMessage(BaseMessage message, boolean isOutgoing) {
        String recipient;
        if (isOutgoing) {
            recipient = message.getReceiver();
        } else {
            recipient = message.isGlobal() ? BaseMessage.GLOBAL_RECEIVER : message.getSender();
        }

        JTextArea chatArea = _chatAreas.get(recipient);
        if (chatArea == null) {
            addChatTab(recipient, recipient);
            chatArea = _chatAreas.get(recipient);
        }

        if (message instanceof TextMessage) {
            String displayMessage = getDisplayMessage((TextMessage) message);
            chatArea.append(displayMessage + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            _messageHistory.get(recipient).add((TextMessage) message);
        }
    }
}
