import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by martinhagerup on 08.05.14.
 */
public class CohortThread extends Thread {

    private Socket clientSocket = null;
    private Cohort server = null;
    private DataInputStream inputStream = null;
    private PrintWriter writer = null;


    public CohortThread(Cohort server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            writer = new PrintWriter(clientSocket.getOutputStream());
            writer.println("Transaction initiated on " + server.name + " " + "current balance is " + server.balance);
            


        } catch (IOException e) {

        }
    }
}
