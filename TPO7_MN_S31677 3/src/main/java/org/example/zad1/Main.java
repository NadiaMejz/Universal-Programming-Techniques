package org.example.zad1;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Main {
    public static void main(String[] args) throws Exception {

        String nick;
        String room;

        if (args.length >= 2) {          // CLI path (still works for testing)
            nick = args[0];
            room = args[1];
        } else {                         // GUI prompt path (demo-friendly)
            nick = JOptionPane.showInputDialog(
                    null, "Enter nickname:", "Login", JOptionPane.PLAIN_MESSAGE);
            if (nick == null || nick.isEmpty()) System.exit(0);

            room = JOptionPane.showInputDialog(
                    null, "Enter room name:", "Choose / create room", JOptionPane.PLAIN_MESSAGE);
            if (room == null || room.isEmpty()) System.exit(0);
        }

        ChatClient client = new ChatClient(nick.trim(), room.trim());
        SwingUtilities.invokeLater(() ->
                new ChatUI(client, nick.trim(), room.trim()).setVisible(true)
        );
    }
}
