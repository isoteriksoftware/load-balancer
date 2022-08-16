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
            WORKER_NODE
        }

        private final ClientType clientType;

        public ConnectionHandler(Socket connection) throws IOException, ClassNotFoundException {
            super(connection);

            // Receive registration message
            Object handshake = inputStream.readObject();

            if (handshake instanceof NodeRegistrationMessage) {
                NodeRegistrationMessage nodeRegistrationMessage = (NodeRegistrationMessage) handshake;
                sendMessage(new NodeRegistrationSuccessfulMessage());
                clientType = ClientType.WORKER_NODE;

                System.out.printf("Accepted connection from WorkerNode (%s)%n", nodeRegistrationMessage.nodeName);
            }
            else
                throw new IllegalStateException("Unknown client");
        }

        @Override
        protected void handleConnection() {

        }
    }
}
