/**
 *
 *  @author Mejza Nadia S31677
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {
    private ClientTask(Callable<String> callable) {
        super(callable);
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask(() -> {
            try {
                c.connect();
                String loginRes = c.send("login " + c.getId());
                if (showSendRes) System.out.println(loginRes);

                for (String req : reqs) {
                    String res = c.send(req);
                    if (showSendRes) System.out.println(res);
                }

                return c.send("bye and log transfer");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }
}