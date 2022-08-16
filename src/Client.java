import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int OPTION_SCHEDULE_JOB = 1;
    private static final int OPTION_QUIT = 2;

    public static void main(String[] args) {
        if (args.length < 2) {
            showUsage();
            return;
        }

        String loadBalancerHost;
        int loadBalancerPort;

        try {
            loadBalancerHost = args[0];
            loadBalancerPort = Integer.parseInt(args[1]);
        } catch (Exception e) {
            showUsage();
            return;
        }

        try {
            Socket connection = new Socket(loadBalancerHost, loadBalancerPort);
            ConnectionHandler connectionHandler = new ConnectionHandler(connection);
            connectionHandler.start();

            Scanner scanner = new Scanner(System.in);
            int option;

            do {
                option = showMenu(scanner);

                if (option == OPTION_SCHEDULE_JOB) {
                    System.out.print("Enter the duration for the job (in seconds): ");

                    long duration;

                    do {
                        try {
                            duration = scanner.nextLong();
                            if (duration <= 0)
                                System.out.println("Duration must be >= 0\n");
                            else {
                                connectionHandler.scheduleJob(duration);
                                System.out.println("Job scheduled");
                            }
                        } catch (Exception e) {
                            System.out.println("Enter a valid duration\n");
                            duration = 0;
                        }
                    } while (duration <= 0);
                }
            } while (option != OPTION_QUIT);
        } catch (Exception e) {
            System.out.println("Connection refused by load balancer");
        }
    }

    private static int showMenu(Scanner scanner) {
        int option = -1;

        do {
            System.out.printf("%n%d. Schedule a job%n", OPTION_SCHEDULE_JOB);
            System.out.printf("%d. Quit%n", OPTION_QUIT);
            System.out.print("Choose one of the options: ");

            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Choose a valid option\n");
            }
        } while (option == -1);

        return option;
    }

    private static void showUsage() {
        System.out.printf("Usage: %s <LOAD_BALANCER_HOST> <LOAD_BALANCER_PORT>%n",
                Client.class.getName());
    }

    public static class ConnectionHandler extends BaseConnectionHandler {
        public ConnectionHandler(Socket connection) throws IOException, ClassNotFoundException {
            super(connection);

            // Send a registration message to the load balancer
            sendMessage(new ClientNodeRegistrationMessage());

            // See if the balancer accepts our request
            Object response = inputStream.readObject();
            if (!(response instanceof RegistrationSuccessfulMessage)) {
                connection.close();
                throw new IllegalStateException("Connection refused by load balancer");
            }

            System.out.println("Client connected to load balancer");
        }

        @Override
        protected void handleConnection() {

        }

        public void scheduleJob(long durationMillis) {
            sendMessage(new Job(durationMillis));
        }
    }
}
