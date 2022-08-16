import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class BaseConnectionHandler extends Thread {
    protected final Socket connection;
    protected final ObjectInputStream inputStream;
    protected final ObjectOutputStream outputStream;
    private boolean running = true;

    protected BaseConnectionHandler(Socket connection) throws IOException {
        this.connection = connection;
        this.outputStream = new ObjectOutputStream(connection.getOutputStream());
        this.inputStream = new ObjectInputStream(connection.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (running) {
                handleConnection();
            }
        } catch (Exception e) {
            System.out.println("An exception occurred in connection handler: " + e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Serializable message) {
        if (!running)
            return;

        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e);
        }
    }

    public void closeConnection() throws IOException {
        running = false;
        connection.close();
    }

    protected abstract void handleConnection() throws IOException, ClassNotFoundException;
}
