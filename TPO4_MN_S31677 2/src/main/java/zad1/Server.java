/**
 * @author Mejza Nadia S31677
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final String host;
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Thread serverThread;
    private volatile boolean running = false;

    private final Map<String, List<String>> clientLogs = new ConcurrentHashMap<>();
    private final List<String> serverLog = new ArrayList<>();
    private final Map<SocketChannel, ClientContext> clients = new ConcurrentHashMap<>();

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() throws IOException {

        /* ---------- kanał nasłuchujący ---------- */
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);

        /* ---------- selektor ---------- */
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        running = true;

        /* ---------- wątek serwera ---------- */
        serverThread = new Thread(() -> {
            try {
                while (running) {

                    selector.select();          // czekamy na pierwsze zdarzenie

                    /* 1) przyjmij wszystkie połączenia oczekujące w kolejce */
                    acceptPending();

                    /* 2) obsłuż tylko zdarzenia READ */
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (!key.isValid() || !key.isReadable()) continue;

                        try {
                            readFromClient(key);        // max jedna linia na iterację
                        } catch (IOException io) {      // problem z kanałem → zamknij
                            try { key.channel().close(); } catch (IOException ignored) {}
                        }
                    }
                }
            } catch (ClosedSelectorException ignored) {
                /* selektor zamknięty w stopServer() – normalne zakończenie */
            } catch (IOException e) {
                e.printStackTrace();                   // nieoczekiwany błąd
            }
        }, "Server-thread");

        serverThread.start();
    }





    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel srv = (ServerSocketChannel) key.channel();
        SocketChannel ch;

        /* weź jedno po drugim wszystkie czekające połączenia */
        while ((ch = srv.accept()) != null) {
            ch.configureBlocking(false);
            ch.register(selector, SelectionKey.OP_READ);
            clients.put(ch, new ClientContext());
            /* jeżeli chcesz zachować wpis „connected”, odkomentuj linię poniżej */
            // addServerLog(ch.toString() + " connected");
        }
    }


    private void readFromClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientContext context = clients.get(clientChannel);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            handleDisconnect(clientChannel);
            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String input = new String(bytes, StandardCharsets.UTF_8);
        context.buffer.append(input);

        processBuffer(clientChannel, context);
    }

    private void processBuffer(SocketChannel ch,
                               ClientContext ctx) throws IOException {

        int nl = ctx.buffer.indexOf("\n");
        if (nl == -1) return;                       // nie ma pełnej komendy

        String line = ctx.buffer.substring(0, nl).trim();
        ctx.buffer.delete(0, nl + 1);               // zostaw resztę na później
        handleCommand(ch, ctx, line);               // jedna komenda = jedna iteracja
    }


    private void handleCommand(SocketChannel ch,
                               ClientContext ctx,
                               String line) throws IOException {

        if (line.startsWith("login ")) {                      // login ×××
            ctx.id = line.substring(6);
            clientLogs.put(ctx.id, new ArrayList<>());
            addClientLog(ctx.id, "logged in");
            addServerLog(ctx.id + " logged in");
            sendResponse(ch, "logged in");

        } else if ("bye".equals(line)) {                      // samo „bye”
            handleDisconnect(ch);

        } else if ("bye and log transfer".equals(line)) {     // bye + log
            sendLogAndDisconnect(ch, ctx);
        } else {                                  // każda inna linia = zapytanie
            String[] parts = line.split(" ");
            if (parts.length >= 2) {
                String result = Time.getResult(parts[0], parts[1]);

                /* log klienta */
                addClientLog(ctx.id,
                        "Request: \"" + line + "\"\nResult:\n" + result);

                /* log serwera – dokładnie w żądanym formacie */
                addServerLogRaw(ctx.id + " request at " + now() +
                        ": \"" + line + "\"");

                sendResponse(ch, result);
            }

        }
    }

    private static String now() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }


    private void handleDataRequest(SocketChannel clientChannel,
                                   ClientContext context,
                                   String line) throws IOException {   // ← dopisujemy

        String[] parts = line.split(" ");
        if (parts.length < 3) return;

        String date1 = parts[1];
        String date2 = parts[2];
        String result = Time.getResult(date1, date2);

        addClientLog(context.id,
                "Request: " + line.substring(7) + "\nResult: " + result);
        addServerLog(context.id + " request: \"" + line.substring(7) + "\"");

        sendResponse(clientChannel, result);   // IOException propaguje się w górę
    }


    private void sendLogAndDisconnect(SocketChannel clientChannel, ClientContext context) throws IOException {
        List<String> log = clientLogs.get(context.id);
        String logStr = "=== " + context.id + " log start ===\n" + String.join("\n", log) + "\n=== " + context.id + " log end ===\n";
        sendResponse(clientChannel, logStr);
        handleDisconnect(clientChannel);
    }

    private void handleDisconnect(SocketChannel ch) throws IOException {
        ClientContext ctx = clients.get(ch);
        if (ctx.id != null) {
            addServerLog(ctx.id + " logged out");
            clientLogs.remove(ctx.id);
        }
        clients.remove(ch);
        ch.close();
    }


    private void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        response += "\n";
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }

    private void addClientLog(String id, String entry) {
        List<String> log = clientLogs.get(id);
        log.add(entry);
    }

    private void addServerLog(String entry) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        serverLog.add(entry + " at " + timestamp);
    }

    private void logServer(SocketChannel clientChannel, String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        serverLog.add(clientChannel.toString() + " " + action + " at " + timestamp);
    }

    public void stopServer() {
        running = false;           // wyjdziemy z pętli wątku serwera
        if (selector != null)      // przerwij blokujące select()
            selector.wakeup();

        try {                      // poczekaj aż wątek naprawdę skończy
            serverThread.join();
        } catch (InterruptedException ignored) {
        }

        try {
            serverSocketChannel.close();
        } catch (IOException ignored) {
        }
        try {
            selector.close();
        } catch (IOException ignored) {
        }
    }

    public String getServerLog() {
        return String.join("\n", serverLog);
    }

    private static class ClientContext {
        String id;
        StringBuilder buffer = new StringBuilder();
    }
    private void addServerLogRaw(String entry) {
        serverLog.add(entry);
    }
    /** przyjmij od razu wszystkie gniazda czekające w kolejce */
    private void acceptPending() throws IOException {
        SocketChannel ch;
        while ((ch = serverSocketChannel.accept()) != null) {
            ch.configureBlocking(false);
            ch.register(selector, SelectionKey.OP_READ);
            clients.put(ch, new ClientContext());
            /* NIE logujemy tu "connected" – wymagany wynik go nie zawiera */
        }
    }

}