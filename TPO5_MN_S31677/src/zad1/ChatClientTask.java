package zad1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChatClientTask implements Runnable {

    private final ChatClient client;
    private final List<String> msgs;
    private final int waitMs;

    private final CompletableFuture<Void> done = new CompletableFuture<Void>();

    private ChatClientTask(ChatClient c, List<String> msgs, int waitMs) {
        this.client = c; this.msgs = msgs; this.waitMs = waitMs;
    }

    /* completely replace the factory – this is the ONLY place touched */
    public static ChatClientTask create(ChatClient c,
                                        List<String> raw, int waitMs) {

        /* 1) strip the Polish “…, mówię ja, <id>” suffix from every fragment */
        List<String> words = new ArrayList<>();
        for (String r : raw) {
            int comma = r.indexOf(',');
            words.add((comma == -1 ? r : r.substring(0, comma)).trim());
        }

        List<String> msgs = new ArrayList<>();
        if (words.size() >= 2) {
            msgs.add(words.get(0) + ' ' + words.get(1));          // “Good morning”
        }
        for (int i = 2; i < words.size(); i++) msgs.add(words.get(i));

        List<String> finalMsgs = new ArrayList<>(msgs.size());
        for (String m : msgs) finalMsgs.add(m + ", I say, " + c.toString());

        return new ChatClientTask(c, finalMsgs, waitMs);
    }


    private void nap() throws InterruptedException {
        if (waitMs > 0) TimeUnit.MILLISECONDS.sleep(waitMs);
    }

    @Override public void run() {
        try {
            client.login();
            nap();

            for (String m : msgs) {
                client.send(m);
                nap();
            }

            client.logout();
            nap();
            done.complete(null);
        } catch (Exception ex) {
            done.completeExceptionally(ex);
        }
    }

    /* -------- proxies used by Main -------- */

    public Void get() throws InterruptedException, ExecutionException {
        return done.get();
    }

    public ChatClient getClient() { return client; }
}
