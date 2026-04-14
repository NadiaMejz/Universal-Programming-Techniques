package org.example.zad1;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ChatClient implements AutoCloseable {
    private final String nick;
    private final String room;
    private final Connection conn;
    private final Channel channel;
    private final BlockingQueue<String> inbound = new LinkedBlockingQueue<>();

    public ChatClient(String nick, String room) throws Exception {
        this.nick = nick;
        this.room = room;

        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        conn = f.newConnection();
        channel = conn.createChannel();

        // fanout exchange per room
        String exchange = "room." + room;
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);

        // unique queue for this client
        String queue = "client." + UUID.randomUUID();
        channel.queueDeclare(queue, false, true, true, null);
        channel.queueBind(queue, exchange, "");

        // async consumer thread
        channel.basicConsume(queue, true, (tag, msg) ->
                inbound.add(new String(msg.getBody(), StandardCharsets.UTF_8)), tag -> {});
    }

    /** non-blocking poll by UI thread */
    public String poll() {
        return inbound.poll();
    }

    /** send a plain “nick: message” line */
    public void send(String text) throws Exception {
        String body = nick + ": " + text;
        channel.basicPublish("room." + room, "", null, body.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws Exception {
        channel.close();
        conn.close();
    }
}

