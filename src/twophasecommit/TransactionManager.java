import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Petter on 09/05/14.
 *
 */
public class TransactionManager {

    public void transferMoney(double amount, Socket fromServer, Socket toServer){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CohortQueryThread transferMoneyToQuery = new CohortQueryThread(toServer, amount + "", 2, atomicInteger);
        CohortQueryThread transferMoneyFromQuery = new CohortQueryThread(fromServer, "-" + amount, 2, atomicInteger);
        transferMoneyFromQuery.start();
        transferMoneyToQuery.start();
        try {
            transferMoneyFromQuery.join();
            transferMoneyToQuery.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
