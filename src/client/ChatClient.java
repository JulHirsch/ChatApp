package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClient extends JFrame implements KeyListener {
    private static final String IP_REGEX =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
    private int _port;
    private String _address;

    //Connection
    private Socket _connectionToServer;
    private BufferedReader _fromServerReader;
    private PrintWriter _toServerWriter;

    //GUI
    private JTextArea _ouputTextArea;
    private JTextField _inputTextField;
    private JScrollPane _outputScrollPane;

    public ChatClient(int port) {
        super("Chat");
        _port = port;

        String address = JOptionPane.showInputDialog("IP address");

        while (address != null && !isValidIPAddress(address)) {
            JOptionPane.showMessageDialog(null, "Invalid IP address format");
            address = JOptionPane.showInputDialog("IP address");
        }

        if (address == null) {
            return;
        }

        _address = address;
        receiveMessages();
    }

    public static void main(String[] args) {
        new ChatClient(3141);
    }

    private void initializeGUI() {
        _ouputTextArea = new JTextArea();
        _ouputTextArea.setEditable(false);
        _ouputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));

        _outputScrollPane = new JScrollPane(_ouputTextArea);

        _inputTextField = new JTextField();
        _inputTextField.setBorder(BorderFactory.createTitledBorder("Type message"));
        _inputTextField.addKeyListener(this);


        add(_outputScrollPane, BorderLayout.CENTER);
        add(_inputTextField, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void receiveMessages() {
        try {
            _connectionToServer = new Socket(_address, _port);
            _fromServerReader = new BufferedReader(new InputStreamReader(_connectionToServer.getInputStream()));
            _toServerWriter = new PrintWriter(new OutputStreamWriter(_connectionToServer.getOutputStream()));

            initializeGUI();

            while (true) {
                String message = _fromServerReader.readLine();
                _ouputTextArea.append(message + "\n");
                _outputScrollPane.getVerticalScrollBar().setValue(_outputScrollPane.getVerticalScrollBar().getMaximum());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection to server with address \"" + _address + "\" failed.");
            dispose();
        } finally {
            if (_connectionToServer != null) {
                try {
                    _connectionToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (_fromServerReader != null) {
                try {
                    _fromServerReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (_toServerWriter != null) {
                _toServerWriter.close();
            }
        }
    }

    //TODO refactor
    private boolean isValidIPAddress(String address) {
        Matcher matcher = IP_PATTERN.matcher(address);
        return matcher.matches();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        String message = _inputTextField.getText();
        if (message.isEmpty()) {
            return;
        }
        //TODO first message not send!!!!
        _toServerWriter.println(message);
        _toServerWriter.flush();

        _inputTextField.setText("");
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
