import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    public static final int LISTENING_PORT = 3333;

    public static void main(String[] args) {
        ServerSocket listener;

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Load balancer listening on port: " + LISTENING_PORT);

            Scheduler.instance();

            while (true) {
                Socket connection = listener.accept();

                try {
                    ConnectionHandler connectionHandler = new ConnectionHandler(connection);
                    connectionHandler.start();
                } catch (Exception e) {
                    // Connection refused or some other error occurred

                    try {
                        connection.close();
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred in load balancer: " + e);
        }
    }

    public static class ConnectionHandler extends BaseConnectionHandler {
        enum ClientType {
            WORKER_NODE,
            CLIENT_NODE
        }

        private final ClientType clientType;

        public ConnectionHandler(Socket connection) throws IOException, ClassNotFoundException {
            super(connection);

            // Receive registration message
            Object handshake = inputStream.readObject();

            if (handshake instanceof WorkerNodeRegistrationMessage) {
                WorkerNodeRegistrationMessage nodeRegistrationMessage = (WorkerNodeRegistrationMessage) handshake;
                sendMessage(new RegistrationSuccessfulMessage());
                clientType = ClientType.WORKER_NODE;

                System.out.printf("Accepted connection from WorkerNode (%s)%n", nodeRegistrationMessage.nodeName);

                // Store the node
                Scheduler.instance().addWorker(new WorkerNodeInfo(nodeRegistrationMessage.nodeName, this));
            }
            else if (handshake instanceof ClientNodeRegistrationMessage) {
                ClientNodeRegistrationMessage registrationMessage = (ClientNodeRegistrationMessage) handshake;
                sendMessage(new RegistrationSuccessfulMessage());
                clientType = ClientType.CLIENT_NODE;

                System.out.println("Accepted connection from Client");
            }
            else
                throw new IllegalStateException("Unknown client");
        }

        @Override
        protected void handleConnection() throws IOException, ClassNotFoundException {
            if (clientType == ClientType.CLIENT_NODE) {
                // read job requests
                Object incoming = inputStream.readObject();
                if (incoming instanceof Job) {
                    Job job = (Job) incoming;
                    Scheduler.instance().scheduleJob(job);

                    System.out.printf("Accepted new job with duration of %d milliseconds %n", job.durationMillis);
                }
            }
        }
    }
}
