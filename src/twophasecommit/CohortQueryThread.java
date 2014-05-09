import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Petter on 08/05/14.
 * <p/>
 * This class connects to the cohorts. There is one for each query.
 */
public class CohortQueryThread extends Thread {
    Socket connection;
    String query;
    int numberOfCommits;
    private AtomicInteger numberOfYesVotes = new AtomicInteger();

    public CohortQueryThread(Socket connection, String query, int numberOfCommits) {
        this.connection = connection;
        this.query = query;
        this.numberOfCommits = numberOfCommits;
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

                        if (System.currentTimeMillis() - time > 60000) {
                            numberOfYesVotes.set(-1);
                            throw new IOException("Voting timed out");
                        } else if (numberOfYesVotes.get() < 0) {
                            numberOfYesVotes.set(-1);
                            throw new IOException("Another cohort aborted");
                        }
                    }

                    if (numberOfYesVotes.get() == numberOfCommits) {
                        writer.println("commit");
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