import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {
    private static Scheduler instance;

    public synchronized static Scheduler instance() {
        if (instance == null)
            instance = new Scheduler();

        return instance;
    }

    private final Queue<Job> jobs;
    private final Queue<WorkerNodeInfo> workers;

    public Scheduler() {
        this.jobs = new LinkedBlockingQueue<>();
        this.workers = new LinkedBlockingQueue<>();
    }

    public boolean isJobsAvailable() {
        return !jobs.isEmpty();
    }

    public Job getNextJob() {
        return jobs.poll();
    }

    public void scheduleJob(Job job) {
        jobs.add(job);
    }

    public boolean isWorkerAvailable() {
        return !workers.isEmpty();
    }

    public WorkerNodeInfo getNextWorker() {
        return workers.poll();
    }

    public void addWorker(WorkerNodeInfo worker) {
        workers.add(worker);
    }
}
