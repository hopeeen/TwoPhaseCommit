import java.io.*;
import java.net.Socket;

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

    @Override
    public void run() {
        try {
            InputStreamReader iReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(iReader);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Transaction detected on " + server.name + " " + "current balance is " + server.balance);

            System.out.println("Hello!");
            writer.println("You have connected");
            writer.println("You have connected. Yes");
            String line = null;


            try {
                line = reader.readLine();
            } catch (IOException e) {
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
