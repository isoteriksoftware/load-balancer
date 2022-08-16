import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler extends Thread {
    private static Scheduler instance;

    public synchronized static Scheduler instance() {
        if (instance == null)
            instance = new Scheduler();

        return instance;
    }

    private final LinkedBlockingQueue<Job> jobs;
    private final LinkedBlockingQueue<WorkerNodeInfo> workers;

    private boolean running = true;

    public Scheduler() {
        this.jobs = new LinkedBlockingQueue<>();
        this.workers = new LinkedBlockingQueue<>();
        start();
    }

    public boolean isJobsAvailable() {
        return !jobs.isEmpty();
    }

    public Job getNextJob() throws InterruptedException {
        return jobs.take();
    }

    public synchronized void scheduleJob(Job job) {
        jobs.add(job);
    }

    public boolean isWorkerAvailable() {
        return !workers.isEmpty();
    }

    public synchronized WorkerNodeInfo getNextWorker() throws InterruptedException {
        return workers.take();
    }

    public void addWorker(WorkerNodeInfo worker) {
        workers.add(worker);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Get the next job
                Job job = getNextJob();

                // Get a worker node
                WorkerNodeInfo worker = getNextWorker();

                // Send the job request
                worker.connectionHandler.sendMessage(job);
                System.out.printf("Dispatched job with duration %d seconds to %s%n", job.durationSeconds,
                        worker.nodeName);
            } catch (InterruptedException e) {
                System.out.println("Exception in scheduler: " + e);
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}
