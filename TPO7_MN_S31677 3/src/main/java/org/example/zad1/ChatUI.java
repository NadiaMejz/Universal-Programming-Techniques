package org.example.zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class ChatUI extends JFrame {
    private final ChatClient client;
    private final JTextArea chat = new JTextArea();
    private final JTextField input = new JTextField();

    public ChatUI(ChatClient client, String nick, String room) {
        super("Room: " + room + "  |  User: " + nick);
        this.client = client;

        chat.setEditable(false);
        JScrollPane scroll = new JScrollPane(chat);

        JButton send = new JButton("Send");
        send.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                try { client.send(text); } catch (Exception ex) { ex.printStackTrace(); }
                input.setText("");
            }
        });

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(input, BorderLayout.CENTER);
        south.add(send, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        // periodic poll -> append messages
        new Timer(100, e -> {
            String msg;
            while ((msg = client.poll()) != null) {
                chat.append(msg + "\n");
            }
        }).start();

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                try { client.close(); } catch (Exception ignore) {}
                dispose();
                System.exit(0);
            }
        });
    }
}
