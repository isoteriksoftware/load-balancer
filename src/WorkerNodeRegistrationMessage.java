import java.io.Serializable;

public class WorkerNodeRegistrationMessage implements Serializable {
    public final String nodeName;

    public WorkerNodeRegistrationMessage(String nodeName) {
        this.nodeName = nodeName;
    }
}
