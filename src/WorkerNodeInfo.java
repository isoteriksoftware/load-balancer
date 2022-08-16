public class WorkerNodeInfo {
    public final String nodeName;
    public final BaseConnectionHandler connectionHandler;

    public WorkerNodeInfo(String name, BaseConnectionHandler connectionHandler) {
        this.nodeName = name;
        this.connectionHandler = connectionHandler;
    }
}
