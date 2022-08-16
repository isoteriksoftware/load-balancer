import java.io.Serializable;

public class Job implements Serializable {
    public final long durationSeconds;

    public Job(long durationMillis) {
        this.durationSeconds = durationMillis;
    }
}
