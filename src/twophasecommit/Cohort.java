import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Cohort {

    private List<CohortThread> threadList;
    private List<String> log;
    private boolean accepting = true;
    public double balance;
    public String name;

    public Cohort(double initBalance, String name) {
        threadList = new ArrayList<CohortThread>();
        log = new ArrayList<String>();
        this.balance = initBalance;
        this.name = name;
    }

    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;

        //Socket cSocket = null;
        ServerSocket serverSocket = null;
        double init = 499;
        String name = "Account 1";
        Cohort server = new Cohort(init, name);

        try {
            serverSocket = new ServerSocket(PORTNR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (server.accepting) {
            try {
                Socket cSocket = serverSocket.accept();
                CohortThread cThread = new CohortThread(server, cSocket);
                server.threadList.add(cThread);
                System.out.println("Added a new client.");
                server.log.add("NOT_SENT");
                cThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
