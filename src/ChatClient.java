import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter out;
    private Socket socket;

    public ChatClient(String serverAddress, int port) {
        setTitle("Java Chat Client");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        add(messageField, BorderLayout.SOUTH);

        messageField.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                out.println(message);
                messageField.setText("");
            }
        });

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class IncomingReader implements Runnable {
        public void run() {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = reader.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Connection closed.\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String serverAddress = JOptionPane.showInputDialog(
                    "Enter server IP address:", "localhost");
            ChatClient client = new ChatClient(serverAddress, 12345);
            client.setVisible(true);
        });
    }
}
