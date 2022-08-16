import java.io.Serializable;

public class JobCompletedMessage implements Serializable {
    public final Job job;
    public final String nodeName;

    public JobCompletedMessage(Job job, String nodeName) {
        this.job = job;
        this.nodeName = nodeName;
    }
}
