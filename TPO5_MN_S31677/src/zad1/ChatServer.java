package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * One‑thread, selector‑based chat server (Java 8 version).
 */
public class ChatServer {

    private final String host;
    private final int    port;

    /* NIO primitives */
    private ServerSocketChannel server;
    private Selector            selector;

    /* selector thread */
    private Thread   loopThread;
    private volatile boolean running;

    /* per‑client attachment */
    private static final class ClientCtx {
        final SocketChannel sc;
        final ByteBuffer    in  = ByteBuffer.allocate(8 * 1024);
        final Queue<ByteBuffer> out = new ArrayDeque<ByteBuffer>();
        volatile boolean    eof = false;          // true after we saw -1
        ClientCtx(SocketChannel sc) { this.sc = sc; }
    }

    /* log */
    private final StringBuilder          log = new StringBuilder();
    private final DateTimeFormatter      TS  = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /* ============ public API ============ */

    public void startServer() throws IOException {
        if (running) return;

        /* open listening channel */
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(host, port));

        /* selector and first registration */
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        running = true;
        loopThread = new Thread(new Runnable() {
            @Override public void run() { eventLoop(); }
        }, "ChatServer-loop");
        loopThread.start();

        System.out.println("Server started\n");
    }

    public void stopServer() {
        running = false;
        if (selector != null) selector.wakeup();   // leave select()

        try { loopThread.join(); } catch (InterruptedException ignored) {}

        try { server.close();   } catch (IOException ignored) {}
        try { selector.close(); } catch (IOException ignored) {}

        System.out.println("\nServer stopped\n");
    }

    public String getServerLog() {
        return log.toString();
    }

    /* ============ internal ================= */

    private void eventLoop() {
        try {
            while (running) {
                selector.select();                 // blocking

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    try {
                        if (key.isValid() && key.isAcceptable()) accept();
                        if (key.isValid() && key.isReadable())   read(key);
                        if (key.isValid() && key.isWritable())   write(key);
                    } catch (IOException io) {
                        closeKey(key);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("*** server loop: " + ex);
        }

        /* final sweep */
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey k : keys) closeKey(k);
    }

    private void accept() throws IOException {
        SocketChannel sc = server.accept();
        if (sc == null) return;

        sc.configureBlocking(false);
        SelectionKey k = sc.register(selector, SelectionKey.OP_READ);
        k.attach(new ClientCtx(sc));
    }

    private void read(SelectionKey key) throws IOException {
        ClientCtx c = (ClientCtx) key.attachment();

        int n = c.sc.read(c.in);
        if (n == -1) {                       // client half‑closed output
            c.eof = true;                    // remember
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
            return;                          // but keep the channel open

        }

        /* parse lines delimited by '\n' */
        c.in.flip();
        while (c.in.hasRemaining()) {
            int lineStart = c.in.position();
            boolean foundNL = false;

            while (c.in.hasRemaining()) {
                if (c.in.get() == (byte) '\n') { foundNL = true; break; }
            }
            int lineEnd = c.in.position();
            if (!foundNL) break;             // incomplete line

            int len = lineEnd - lineStart - 1;   // trim '\n'
            byte[] arr = new byte[len];
            c.in.position(lineStart);
            c.in.get(arr);
            c.in.get();                          // consume '\n'

            String line = new String(arr, StandardCharsets.UTF_8);
            if (!line.isEmpty()) broadcast(line);
        }
        c.in.compact();
    }

    private void write(SelectionKey key) throws IOException {
        ClientCtx c = (ClientCtx) key.attachment();
        while (!c.out.isEmpty()) {
            ByteBuffer buf = c.out.peek();
            c.sc.write(buf);
            if (buf.hasRemaining()) break;   // socket buffer full
            c.out.poll();
        }
        if (c.out.isEmpty()) key.interestOps(SelectionKey.OP_READ);

        if (c.eof && c.out.isEmpty()) closeKey(key);
    }

    /* ---------- helpers ---------- */

    private void broadcast(String plain) {
        String stamped = LocalTime.now().format(TS) + ' ' + plain;
        log.append(stamped).append('\n');

        ByteBuffer msg = StandardCharsets.UTF_8.encode(CharBuffer.wrap(plain + '\n'));

        for (SelectionKey k : selector.keys()) {
            SelectableChannel ch = k.channel();
            if (ch instanceof SocketChannel && k.isValid()) {
                ClientCtx c = (ClientCtx) k.attachment();
                if (c == null) continue;          // the server key
                c.out.add(msg.duplicate());
                k.interestOps(k.interestOps() | SelectionKey.OP_WRITE);
            }
        }
        selector.wakeup();                        // trigger OP_WRITE set ‑up
    }

    private static void closeKey(SelectionKey key) {
        try { key.channel().close(); } catch (IOException ignored) {}
        key.cancel();
    }
}
