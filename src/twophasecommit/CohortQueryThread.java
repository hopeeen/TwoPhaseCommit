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
                System.out.println("Query sent and waiting for answer (either commit or abort)");
                String input = bufferedReader.readLine();
                if (input.equals("commit")) {

                    System.out.println("Commit received from cohort");
                    if (numberOfYesVotes.getAndIncrement() < 0) {
                        numberOfYesVotes.set(-1);
                        throw new IOException("Another cohort aborted");
                    }

                    System.out.println("Set status as ready to global commit and checked if other cohorts have aborted. Entering wait loop to check if others are ready.");
                    Long time = System.currentTimeMillis();
                    while (numberOfYesVotes.get() < numberOfCommits) {

                        System.out.println("Others not ready, entering sleep for 100 milliseconds");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            numberOfYesVotes.set(-1);
                            e.printStackTrace();
                            throw new IOException("Voting failed, sleep was interrupted");
                        }

                        if (System.currentTimeMillis() - time > 10000) {
                            System.out.println("Timeout. Number of yes votes when timing out is: " + numberOfYesVotes.get());
                            numberOfYesVotes.set(-1);
                            throw new IOException("Voting timed out");
                        } else if (numberOfYesVotes.get() < 0) {
                            numberOfYesVotes.set(-1);
                            throw new IOException("Another cohort aborted");
                        }
                    }

                    if (numberOfYesVotes.get() == numberOfCommits) {
                        System.out.println("Other cohorts have agreed to commit, sending out global commit and waiting for acknowledgement");
                        writer.println("global_commit");
                        String acknowledgementInput = bufferedReader.readLine();
                        if (acknowledgementInput.equals("ack")){
                            System.out.println("A cohort successfully executed a query");
                        }
                    } else {
                        numberOfYesVotes.set(-1);
                        throw new IOException("Voting is fucked. There are more votes than commits. Probably. Shieeet.");
                    }
                } else {
                    numberOfYesVotes.set(-1);
                    throw new IOException("This cohort aborted. The error was: " + input);
                }

            } catch (IOException e) {
                System.out.println("A wild error appeared. Rolling back.");
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