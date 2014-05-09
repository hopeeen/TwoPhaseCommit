import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Petter on 09/05/14.
 */
public class Clientside {
    public static void main(String[] args) {
        TransactionManager transactionManager = new TransactionManager();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Two Phase Commit transaction manager." +
                "\n The following actions are available to you:" +
                "\n 1. Transfer money from one account to another" +
                "\n Please select an action:");
        String action = scanner.nextLine();
        while (!action.equals("")) {
            if (action.equals("1")) {

                System.out.println("Insert amount to be transferred: ");
                double amount = Double.parseDouble(scanner.nextLine());

                System.out.println("Insert withdrawal account IP: ");
                String fromServer = scanner.nextLine();

                System.out.println("Insert destination account IP: ");
                String toServer = scanner.nextLine();

                System.out.println("Insert withdrawal account port number: ");
                int fromServerPort = Integer.parseInt(scanner.nextLine());

                System.out.println("Insert money destination account port number: ");
                int toServerPort = Integer.parseInt(scanner.nextLine());

                try {
                    transactionManager.transferMoney(
                            amount,
                            new Socket(fromServer, fromServerPort),
                            new Socket(toServer, toServerPort));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Welcome to the Two Phase Commit transaction manager." +
                    "\n The following actions are available to you:" +
                    "\n 1. Transfer money from one account to another" +
                    "\n Please select an action:");
            action = scanner.nextLine();
        }
        scanner.close();
    }
}
