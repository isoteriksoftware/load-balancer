import java.io.Serializable;

public class Job implements Serializable {
    public final long durationMillis;

    public Job(long durationMillis) {
        this.durationMillis = durationMillis;
    }
}
