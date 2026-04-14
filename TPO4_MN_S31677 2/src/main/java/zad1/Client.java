/**
 *
 *  @author Mejza Nadia S31677
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    private final String host;
    private final int port;
    private final String id;
    private SocketChannel channel;
    private final StringBuilder readBuffer = new StringBuilder();

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));
        while (!channel.finishConnect()) {
            // Wait for connection
        }
        }
        catch(IOException e){
        }
    }

    public String send(String req) {
        ByteBuffer out = ByteBuffer.wrap((req + "\n").getBytes(StandardCharsets.UTF_8));
        while (out.hasRemaining()) {
            try {
                channel.write(out);
            } catch (IOException e) {
                return "IO Exception";
            }
        }

        final long TIMEOUT_MS = 2000;
        long start = System.currentTimeMillis();

        ByteBuffer in = ByteBuffer.allocate(1024);

        while (System.currentTimeMillis() - start < TIMEOUT_MS) {
            int bytes;
            try {
                bytes = channel.read(in);
            } catch (IOException e) {
                return "";
            }

            if (bytes == -1) {
                return "";
            }
            if (bytes == 0) {
                continue;
            }

            in.flip();
            readBuffer.append(StandardCharsets.UTF_8.decode(in));
            in.clear();
            start = System.currentTimeMillis(); // reset timeout after every byte

            int nl = readBuffer.indexOf("\n");
            if (nl != -1) {
                String line = readBuffer.substring(0, nl);
                readBuffer.delete(0, nl + 1);
                return line;
            }
        }

        return "no more lines to read";
    }

    public String getId() {
        return id;
    }
}