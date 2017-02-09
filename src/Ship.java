import java.util.ArrayList;
import java.util.Scanner;

/**
 * file name: Ship.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class is super class for different types of ships
 */
public class Ship extends Thing {
    private PortTime arrivalTime, dockTime;
    double weight, length, width, draft;
    ArrayList<Job> jobs = new ArrayList<Job>();

    private final Object lock = new Object();
    private boolean jobInProcess;


    /**
     * Constructor for scanner object
     *
     * @param sc
     */
    public Ship(Scanner sc) {
        super(sc);
        if (sc.hasNextDouble()) weight = sc.nextDouble();
        if (sc.hasNextDouble()) length = sc.nextDouble();
        if (sc.hasNextDouble()) width = sc.nextDouble();
        if (sc.hasNextDouble()) draft = sc.nextDouble();
        dockTime = new PortTime();
    }


    /**
     * Converts instance of ship object
     * And runs toString method for each of it's jobs
     *
     * @return String
     */
    public String toString() {
        String st = (this instanceof PassengerShip ? "Passenger " : "Cargo ");
        st += "Ship: " + super.toString();
        if (jobs.size() == 0)
            return st;
        for (Job mj : jobs) st += "\n       - " + mj.toString();
        return st;
    }

    /**
     * Checks whether the ship is docked
     *
     * @return true if ship is docked
     */
    public boolean isDocked() {
        if (parentThing instanceof SeaPort)
            ((SeaPort) parentThing).checkDocks();
        else
            ((Dock) parentThing).checkDock();
        return parentThing instanceof Dock && ((Dock)parentThing).getShip() == this;
    }

    /**
     * Checks if there is a job
     *
     * @return whether a job is eligible to run
     */
    public boolean doJob() {
        boolean doingJob = false;
        synchronized (lock) {
            if (!jobInProcess) {
                jobInProcess = true;
                doingJob = true;
            }
        }
        return doingJob;
    }

    /**
     * Removes job from jobs list
     *
     * @param job: Ship job
     */
    public void removeJob(Job job) {
        synchronized (lock) {
            jobInProcess = false;
        }
        if (jobs.size() > 0 && jobs.contains(job))
            jobs.remove(job);
        if (jobs.size() == 0) {
            ((Dock) parentThing).leaveDock();
        }
    }

    boolean askForPersonnel(ArrayList<String> requirements) {
        return ((Dock) parentThing).askForPersonnel(requirements);
    }

    public ArrayList<Person> requestWorkers(ArrayList<String> requirements, Job job) {
        return ((Dock) parentThing).requestWorkers(requirements, job);
    }

    public void releaseWorkers(ArrayList<Person> workers) {
        ((Dock) parentThing).releaseWorkers(workers);
    }
}
