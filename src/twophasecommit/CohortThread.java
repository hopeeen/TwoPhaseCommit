import java.io.*;
import java.net.Socket;
import java.text.ParseException;

/**
 * Created by martinhagerup on 08.05.14.
 */
public class CohortThread extends Thread {

    private Socket clientSocket = null;
    private Cohort server = null;
    private PrintWriter writer = null;


    public CohortThread(Cohort server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    private boolean verifyTransaction(double transactionValue) {
        double balanceCopy = server.balance;
        if ((balanceCopy + transactionValue) > 0) {
            return true;
        } else return false;
    }

    private double parseTransaction(String value) throws ParseException {
        double parsedDouble = Double.parseDouble(value);
        return parsedDouble;
    }

    @Override
    public void run() {
        try {
            InputStreamReader iReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(iReader);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            //writer.println("Transaction detected on " + server.name + " " + "current balance is " + server.balance);

            //writer.println("You have connected");
            String line = null;


            try {
                line = reader.readLine();

                double parsedInValue = parseTransaction(line);

                if (verifyTransaction(parsedInValue)) {
                    writer.println("commit");
                } else {
                    writer.println("abort");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            while(line != null) {
                System.out.println(line);

                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Closing connection");
            reader.close();
            writer.close();
            clientSocket.close();


        } catch (IOException e) {

        }
    }
}
