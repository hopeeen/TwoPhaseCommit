import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Petter on 09/05/14.
 */
public class TransactionManager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Two Phase Commit transaction manager." +
                "\n The following actions are available to you:" +
                "\n 1. Transfer money from one account to another" +
                "\n Please select an action:");
    }

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
