import java.io.IOException;
import java.net.Socket;

public class WorkerNode {
    public static void main(String[] args) {
        if (args.length < 3) {
            showUsage();
            return;
        }

        String nodeName, loadBalancerHost;
        int loadBalancerPort;

        try {
            nodeName = args[0];
            loadBalancerHost = args[1];
            loadBalancerPort = Integer.parseInt(args[2]);
        } catch (Exception e) {
            showUsage();
            return;
        }

        try {
            Socket connection = new Socket(loadBalancerHost, loadBalancerPort);
            ConnectionHandler connectionHandler = new ConnectionHandler(connection, nodeName);
            connectionHandler.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("Connection refused by load balancer");
        }
    }

    private static void showUsage() {
        System.out.printf("Usage: %s <NODE_NAME> <LOAD_BALANCER_HOST> <LOAD_BALANCER_PORT>%n",
                WorkerNode.class.getName());
    }

    public static class ConnectionHandler extends BaseConnectionHandler {
        private final String nodeName;

        public ConnectionHandler(Socket connection, String nodeName) throws IOException, ClassNotFoundException {
            super(connection);
            this.nodeName = nodeName;

            // Send a registration message to the load balancer
            sendMessage(new WorkerNodeRegistrationMessage(nodeName));

            // See if the balancer accepts our request
            Object response = inputStream.readObject();
            if (!(response instanceof RegistrationSuccessfulMessage)) {
                connection.close();
                throw new IllegalStateException("Connection refused by load balancer");
            }

            System.out.printf("WorkerNode (%s) connected to load balancer", nodeName);
        }

        @Override
        protected void handleConnection() {

        }
    }
}
