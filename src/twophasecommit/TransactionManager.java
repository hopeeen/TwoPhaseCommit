import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Petter on 09/05/14.
 */
public class TransactionManager {

    public void transferMoney(int amount, Socket fromServer, Socket toServer){
        CohortQueryThread transferMoneyToQuery = new CohortQueryThread(toServer, amount + "", 2);
        CohortQueryThread transferMoneyFromQuery = new CohortQueryThread(fromServer, "-" + amount, 2);
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
