package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatClient extends JFrame implements KeyListener {
    private final ConnectionManager _connectionManager;

    // GUI
    private JTextArea _outputTextArea;
    private JTextField _inputTextField;
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
                new ChatClient(connectionManager);
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

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);

        setLayout(new BorderLayout());
        add(_outputScrollPane, BorderLayout.CENTER);
        add(_inputTextField, BorderLayout.SOUTH);

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
            String message = _inputTextField.getText();
            if (!message.isEmpty()) {
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