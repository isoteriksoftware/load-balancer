import java.io.Serializable;

public class WorkerNodeInfo implements Serializable {
    public final String name;

    public WorkerNodeInfo(String name) {
        this.name = name;
    }
}
