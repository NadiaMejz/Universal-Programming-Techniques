package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {

    private final String host;
    private final int    port;
    private final String id;

    /* NIO */
    private SocketChannel chan;
    private Selector      selector;

    /* helper structures */
    private final Queue<ByteBuffer> outQ = new ConcurrentLinkedQueue<ByteBuffer>();
    private final ExecutorService   ioExec = Executors.newSingleThreadExecutor();

    private final StringBuilder view = new StringBuilder();
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final StringBuilder pending   = new StringBuilder();

    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id   = id;
    }

    /* ============ public API ============ */

    public void login() {
        try {
            openConnection();
            sendRaw(id + " logged in\n");
        } catch (IOException ex) {
            appendErr(ex);
        }
    }


    public void send(String req) {
        sendRaw(id + ": " + req + '\n');
    }

    public String getChatView() {
        ioExec.shutdown();
        try { ioExec.awaitTermination(1, TimeUnit.SECONDS); }
        catch (InterruptedException ignored) {}
        return "\n=== " + id + " chat view\n" + view.toString();
    }

    /* ============ internals ============ */

    private void openConnection() throws IOException {
        if (connected.get()) return;

        chan = SocketChannel.open();
        chan.configureBlocking(false);
        chan.connect(new InetSocketAddress(host, port));

        /* wait until the OS reports the socket as connected */
        while (!chan.finishConnect()) {
            try { Thread.sleep(1); } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while connecting", ie);
            }
        }

        selector = Selector.open();
        chan.register(selector, SelectionKey.OP_READ);   // OP_WRITE added on demand

        ioExec.execute(new Runnable() {                  // start background I/O
            @Override public void run() { eventLoop(); }
        });
        connected.set(true);
    }


    private void sendRaw(String txt) {
        if (!connected.get()) return;
        outQ.add(ByteBuffer.wrap(txt.getBytes(StandardCharsets.UTF_8)));

        SelectionKey key = chan.keyFor(selector);
        if (key != null && key.isValid())
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    /* write immediately everything that is in outQ – used by logout() */
    private void flushQueueBlocking() throws IOException {
        ByteBuffer b;
        while ((b = outQ.poll()) != null) {
            while (b.hasRemaining()) chan.write(b);
        }
    }

    private void eventLoop() {
        try {
            while (!Thread.currentThread().isInterrupted() && selector.isOpen()) {
                selector.select();

                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                     it.hasNext();) {
                    SelectionKey k = it.next();
                    it.remove();

                    if (!k.isValid()) continue;
                    if (k.isConnectable()) finishConnect(k);
                    if (k.isReadable())    read(k);
                    if (k.isWritable())    write(k);
                }
            }
            } catch (ClosedSelectorException | java.nio.channels.CancelledKeyException ignored) {
            /* normal shutdown path – ignore */
            } catch (Exception ex) {
            appendErr(ex);                      // real problems still logged
        }
    }

    private void finishConnect(SelectionKey k) throws IOException {
        if (chan.finishConnect()) {
            k.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey k) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        int n = chan.read(buf);
        if (n == -1) { selector.close(); return; }

        buf.flip();
        String chunk = StandardCharsets.UTF_8.decode(buf).toString();
        pending.append(chunk);

        int idx;
        while ((idx = pending.indexOf("\n")) != -1) {
            String line = pending.substring(0, idx);
            view.append(line).append('\n');
            pending.delete(0, idx + 1);
        }
    }

    /* ① add – put it near the other helpers
     * ------------------------------------ */
    private void waitUntilQueueIsFlushed() {
    /* spin until the selector thread has written everything
       AND has removed OP_WRITE from the interest set         */
        while (true) {
            SelectionKey key = chan.keyFor(selector);
            if (key == null || !key.isValid()) break;                 // channel gone
            if (outQ.isEmpty() && (key.interestOps() & SelectionKey.OP_WRITE) == 0)
                break;                                                // all done
            selector.wakeup();                                        // give it a nudge
            try { Thread.sleep(1); }                                  // tiny back‑off
            catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
    }

    /* ② replace the busy‑wait block in logout() with the helper
     * --------------------------------------------------------- */
    public void logout() {
        if (!connected.get()) return;

        sendRaw(id + " logged out\n");
        waitUntilQueueIsFlushed();           // <— this call replaces the old while‑loop

        try {                                // leave the socket open for incoming data
            chan.shutdownOutput();           // graceful half‑close: server sees EOF
        } catch (IOException ex) {
            appendErr(ex);
        }
    }

    /* ③ filter the harmless RST in appendErr()
     * ---------------------------------------- */
    private void appendErr(Exception ex) {
        String m = ex.getMessage();
        if (m != null && m.contains("Connection reset by peer")) return;  // just ignore
        view.append("*** ").append(ex).append('\n');
    }

    private void write(SelectionKey k) throws IOException {
        ByteBuffer b;
        while ((b = outQ.peek()) != null) {
            chan.write(b);
            if (b.hasRemaining()) break;          // socket full
            outQ.poll();
        }
        if (outQ.isEmpty()) k.interestOps(SelectionKey.OP_READ);
    }

    @Override public String toString() { return id; }
}
