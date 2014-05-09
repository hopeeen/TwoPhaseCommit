import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Petter on 08/05/14.
 *
 * This class connects to the cohorts. There is one for each query.
 *
 */
public class CohortQueryThread extends Thread {
    Socket connection;
    String query;
    int numberOfCommits;
    AtomicInteger numberOfYesVotes;

    public CohortQueryThread(Socket connection, String query, int numberOfCommits, AtomicInteger numberOfYesVotes) {
        this.connection = connection;
        this.query = query;
        this.numberOfCommits = numberOfCommits;
        this.numberOfYesVotes = numberOfYesVotes;
    }

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            OutputStream outputStream = connection.getOutputStream();

            try {
                writer.println(query);
                String input = bufferedReader.readLine();
                if (input.equals("commit")) {
                    if (numberOfYesVotes.getAndIncrement() < 0) {
                        numberOfYesVotes.set(-1);
                        throw new IOException("Another cohort aborted");
                    }

                    Long time = System.currentTimeMillis();
                    while (numberOfYesVotes.get() < numberOfCommits) {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            numberOfYesVotes.set(-1);
                            e.printStackTrace();
                            throw new IOException("Voting failed, sleep was interrupted");
                        }

                        if (System.currentTimeMillis() - time > 6000) {
                            System.out.println("Number of yes votes when timing out is: " + numberOfYesVotes.get());
                            numberOfYesVotes.set(-1);
                            throw new IOException("Voting timed out");
                        } else if (numberOfYesVotes.get() < 0) {
                            numberOfYesVotes.set(-1);
                            throw new IOException("Another cohort aborted");
                        }
                    }

                    if (numberOfYesVotes.get() == numberOfCommits) {
                        writer.println("global_commit");
                        String acknowledgementInput = bufferedReader.readLine();
                        if (acknowledgementInput.equals("ack")){
                            System.out.println("A cohort successfully executed a query");
                        }
                    } else {
                        numberOfYesVotes.set(-1);
                        throw new IOException("Voting is fucked. There are more votes than commits. Probably. Shieet.");
                    }
                } else {
                    numberOfYesVotes.set(-1);
                    throw new IOException("This cohort aborted. The error was: " + input);
                }

            } catch (IOException e) {
                writer.println("rollback");
                e.printStackTrace();

            }

            outputStream.close();
            bufferedReader.close();
            inputStreamReader.close();
            writer.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}