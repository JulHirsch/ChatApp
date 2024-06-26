package client;

import com.formdev.flatlaf.FlatLightLaf;
import common.Encryption.*;
import common.Messages.BaseMessage;
import common.Messages.IMessage;
import common.Messages.KeyExchangeMessage;
import common.Messages.TextMessage;
import common.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatClient extends JFrame implements KeyListener, IChatClient {
    public static final String GLOBAL_TAB_NAME = BaseMessage.GLOBAL_RECEIVER;
    private final ConnectionManager _connectionManager;
    private final String _clientName;

    private JTabbedPane _tabbedPane;
    private JTextField _inputTextField;
    private JTextField _recipientTextField;
    private Map<String, JTextArea> _chatAreas;
    private Map<String, EncryptionInfo> _chatEncryptionInfos;
    private Map<String, List<TextMessage>> _messageHistory;
    private boolean _isInitialized = false;

    public ChatClient(ConnectionManager connectionManager, String clientName) {
        super("Chat");
        _connectionManager = connectionManager;
        _clientName = clientName;
        _chatAreas = new HashMap<>();
        _messageHistory = new HashMap<>();
        _chatEncryptionInfos = new HashMap<>();
        initializeGUI();
    }

    public static void main(String[] args) {
        try {
            // Set FlatLaf Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(ChatClient::connectToServer);
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

    private static void connectToServer() {
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

    private static boolean isTextMessageEncrypted(boolean isOutgoing, String recipient, TextMessage textMessage) {
        return !isOutgoing && !Objects.equals(recipient, BaseMessage.GLOBAL_RECEIVER) && textMessage.getEncryptionType() != EncryptionType.NONE;
    }

    private static boolean doesTextMessageNeedsToBeEncrypted(String recipient, EncryptionInfo encryptionInfo) {
        return !recipient.equals(GLOBAL_TAB_NAME) && encryptionInfo != null && encryptionInfo.getType() != EncryptionType.NONE;
    }

    public void appendTextMessage(TextMessage message) {
        SwingUtilities.invokeLater(() -> {
            processTextMessage(message, false);
        });
    }

    @Override
    public void appendKeyExchangeMessage(KeyExchangeMessage keyExchangeMessage) {
        if (keyExchangeMessage.getEncryptionType() != EncryptionType.RSA) {
            return;
        }

        RSAPublicKey rsaPublicKey = RSAPublicKey.fromBase64String(keyExchangeMessage.getEncryptionKey());
        String recipient = keyExchangeMessage.getSender();

        if (!_chatEncryptionInfos.containsKey(recipient)) {
            RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair();

            _chatEncryptionInfos.put(recipient,
                    new EncryptionInfo(
                            EncryptionType.RSA,
                            rsaKeyPair,
                            rsaPublicKey
                    ));
            IMessage newKeyExchangeMessage = new KeyExchangeMessage("", recipient, _clientName, rsaKeyPair.getPublicKey().toBase64String(), EncryptionType.RSA);
            _connectionManager.sendMessage(newKeyExchangeMessage);
            return;
        }

        _chatEncryptionInfos.get(recipient).setOtherPublicKey(rsaPublicKey);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }

        String text = _inputTextField.getText();
        if (text.isEmpty()) return;
        _inputTextField.setText("");

        String recipient = _recipientTextField.getText();
        if (recipient.isEmpty()) recipient = BaseMessage.GLOBAL_RECEIVER;

        TextMessage message = new TextMessage(text, recipient, "", _clientName);
        processTextMessage(message, true);

        // handle encryption
        EncryptionInfo encryptionInfo = _chatEncryptionInfos.get(recipient);

        if (doesTextMessageNeedsToBeEncrypted(recipient, encryptionInfo)) {
            String cipherText = "";

            if (!_chatEncryptionInfos.containsKey(recipient)) {
                JOptionPane.showMessageDialog(null, "Key exchange failed.");
                return;
            }
            if (_chatEncryptionInfos.get(recipient).getType() == EncryptionType.RSA && _chatEncryptionInfos.get(recipient).getOtherPublicKey() == null) {
                JOptionPane.showMessageDialog(null, "Key not received. For your information, RSA encryption with yourself is not possible.");
                return;
            }

            switch (encryptionInfo.getType()) {
                case CAESAR:
                    CaesarEncryptionService caesarService = new CaesarEncryptionService();
                    cipherText = caesarService.encrypt(text, (CaesarKey) encryptionInfo.getSymmetricKey());
                    break;
                case RSA:
                    RSAEncryptionService rsaEncryptionService = new RSAEncryptionService();
                    cipherText = rsaEncryptionService.encrypt(text, encryptionInfo.getOtherPublicKey());
                    break;
                case NONE:
                default:
                    // No encryption needed
                    break;
            }

            message = new TextMessage(cipherText, recipient, "", _clientName, encryptionInfo.getType());
        }

        _connectionManager.sendMessage(message);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }

    private void initializeGUI() {
        // Set up the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem addTabMenuItem = new JMenuItem("Add New Chat Tab");
        addTabMenuItem.addActionListener(e -> addNewChatTab());
        menu.add(addTabMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

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
        addButton.setPreferredSize(new Dimension(30, 30)); // Adjusted size
        addButton.setFocusPainted(false);
        addButton.setMargin(new Insets(2, 2, 2, 2)); // Adjusted margins
        addButton.addActionListener(e -> addNewChatTab());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout(5, 5));
        inputPanel.add(_recipientTextField, BorderLayout.NORTH);
        inputPanel.add(_inputTextField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        setLayout(new BorderLayout(10, 10));
        add(_tabbedPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        _isInitialized = true;
    }

    private void addNewChatTab() {
        String recipient = JOptionPane.showInputDialog("Enter recipient name");
        if (recipient == null || recipient.trim().isEmpty()) {
            _tabbedPane.setSelectedIndex(0); // Go back to global tab if creation is cancelled
            return;
        }
        if (doesRecipientAlreadyExist(recipient)) {
            _tabbedPane.setSelectedComponent(_chatAreas.get(recipient).getParent().getParent());
            return;
        }

        // Using enum for encryption options
        EncryptionType[] encryptionOptions = EncryptionType.values();
        EncryptionType encryptionType = (EncryptionType) JOptionPane.showInputDialog(null, "Select Encryption Type",
                "Encryption", JOptionPane.QUESTION_MESSAGE, null, encryptionOptions, encryptionOptions[0]);

        EncryptionInfo encryptionInfo = null;

        switch (encryptionType) {
            case CAESAR:
                int shift;
                IKey encryptionKey = null;
                while (true) {
                    String shiftInput = JOptionPane.showInputDialog("Enter shift (integer, positive or negative)");
                    if (shiftInput == null) {
                        // User closed the dialog with the "X" button
                        return;
                    }
                    try {
                        shift = Integer.parseInt(shiftInput);
                        encryptionKey = new CaesarKey(shift);
                        break;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
                    }
                }
                encryptionInfo = new EncryptionInfo(encryptionType, encryptionKey);
                break;

            case RSA:
                RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair();
                encryptionInfo = new EncryptionInfo(EncryptionType.RSA, rsaKeyPair);
                IMessage message = new KeyExchangeMessage("", recipient, _clientName, rsaKeyPair.getPublicKey().toBase64String(), EncryptionType.RSA);
                _connectionManager.sendMessage(message);
                break;

            case NONE:
                encryptionInfo = new EncryptionInfo(EncryptionType.NONE);
                break;
        }

        _chatEncryptionInfos.put(recipient, encryptionInfo);
        addChatTab(recipient, recipient);
        _tabbedPane.setSelectedIndex(_tabbedPane.getTabCount() - 1); // Select the newly added tab
    }

    private void addChatTab(String title, String recipient) {
        if (doesRecipientAlreadyExist(recipient)) {
            _tabbedPane.setSelectedComponent(_chatAreas.get(recipient).getParent().getParent());
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

    private boolean doesRecipientAlreadyExist(String recipient) {
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

    private void processTextMessage(TextMessage message, boolean isOutgoing) {
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

        if (isTextMessageEncrypted(isOutgoing, recipient, message)) {
            message = decryptTextMessage(message, recipient);
        }
        String displayMessage = getDisplayMessage(message);
        chatArea.append(displayMessage + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        _messageHistory.get(recipient).add(message);
    }

    private TextMessage decryptTextMessage(TextMessage textMessage, String recipient) {
        String plainText = "";

        switch (textMessage.getEncryptionType()) {
            case CAESAR:
                if (!_chatEncryptionInfos.containsKey(recipient)) {
                    askClientForCaesarKey(recipient);
                }
                CaesarEncryptionService caesarService = new CaesarEncryptionService();
                plainText = caesarService.decrypt(textMessage.getText(), (CaesarKey) _chatEncryptionInfos.get(recipient).getSymmetricKey());
                break;
            case RSA:
                if (!_chatEncryptionInfos.containsKey(recipient)) {
                    //TODO handle
                    break;
                }
                RSAEncryptionService rsaEncryptionService = new RSAEncryptionService();
                plainText = rsaEncryptionService.decrypt(textMessage.getText(), _chatEncryptionInfos.get(recipient).getOwnKeyPair().getPrivateKey());
                break;
            default:
                break;
        }

        return new TextMessage(plainText, textMessage.getReceiver(), textMessage.getSender(), textMessage.getCustomName());
    }

    private void askClientForCaesarKey(String recipient) {
        CaesarKey encryptionKey = null;
        int shift;
        while (true) {
            String shiftInput = JOptionPane.showInputDialog("Enter shift (integer, positive or negative)");
            if (shiftInput == null) {
                // User closed the dialog with the "X" button
                //TODO
                shiftInput = "0";
            }
            try {
                shift = Integer.parseInt(shiftInput);
                encryptionKey = new CaesarKey(shift);
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
            }
        }
        _chatEncryptionInfos.put(recipient, new EncryptionInfo(EncryptionType.CAESAR, encryptionKey));
    }
}
