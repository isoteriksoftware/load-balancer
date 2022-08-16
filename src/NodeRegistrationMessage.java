import java.io.Serializable;

public class NodeRegistrationMessage implements Serializable {
    public final String nodeName;

    public NodeRegistrationMessage(String nodeName) {
        this.nodeName = nodeName;
    }
}
