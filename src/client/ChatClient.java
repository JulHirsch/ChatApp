package client;

import common.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatClient extends JFrame implements KeyListener {
    private final ConnectionManager _connectionManager;

    // GUI
    private JTextArea _outputTextArea;
    private JTextField _inputTextField;
    private JTextField _recipientTextField;
    private JScrollPane _outputScrollPane;

    public ChatClient(ConnectionManager connectionManager) {
        super("Chat");
        _connectionManager = connectionManager;
        initializeGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String address = promptForIPAddress();
            if (address != null) {
                ConnectionManager connectionManager = new ConnectionManager(address, 3141);
                ChatClient chatClient = new ChatClient(connectionManager);
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

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            _outputTextArea.append(message + "\n");
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
            String recipient = _recipientTextField.getText();
            if (!text.isEmpty()) {
                Message message = new Message(text);

                // TODO check here an on the server if valid ip address
                if(!recipient.isEmpty()){
                    message.Receiver = recipient;
                }

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
