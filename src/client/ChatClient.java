package client;

import common.Message;
import common.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatClient extends JFrame implements KeyListener {
    private final ConnectionManager _connectionManager;
    private final String _clientName;

    // GUI
    private JTextArea _outputTextArea;
    private JTextField _inputTextField;
    private JTextField _recipientTextField;
    private JScrollPane _outputScrollPane;

    public ChatClient(ConnectionManager connectionManager, String clientName) {
        super("Chat");
        _connectionManager = connectionManager;
        _clientName = clientName;
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
        _outputTextArea = new JTextArea();
        _outputTextArea.setEditable(false);
        _outputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        _outputScrollPane = new JScrollPane(_outputTextArea);

        _recipientTextField = new JTextField();
        _recipientTextField.setBorder(BorderFactory.createTitledBorder("Recipient address"));
        _recipientTextField.addKeyListener(this);

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(_recipientTextField, BorderLayout.NORTH);
        inputPanel.add(_inputTextField, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(_outputScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void appendMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            String displayMessage;
            if (message.getSender().equals("Server")) {
                displayMessage = String.format("Server: %s", message.getText());
            } else if (!message.getReceiver().equals(Message.GLOBAL_RECEIVER)) {
                //TODO check if this allows to write a private message to everyone by using 127.0.0.1
                displayMessage = String.format("Private message from %s: %s", message.getCustomName(), message.getText());
            } else {
                displayMessage = String.format("%s (%s): %s", message.getCustomName(), message.getSender(), message.getText());
            }
            _outputTextArea.append(displayMessage + "\n");
            _outputScrollPane.getVerticalScrollBar().setValue(_outputScrollPane.getVerticalScrollBar().getMaximum());
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
            String recipient = _recipientTextField.getText().isEmpty() ? Message.GLOBAL_RECEIVER : _recipientTextField.getText();
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
