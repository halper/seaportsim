import java.util.ArrayList;
import java.util.Scanner;

/**
 * file name: Dock.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class simulates a dock
 */
public class Dock extends Thing {
    private Ship ship;

    /**
     * Constructor for scanner object
     *
     * @param sc
     */
    public Dock(Scanner sc) {
        super(sc);
    }

    /**
     * Mutator method to set object's ship object
     *
     * @param ship
     */
    public void setShip(Ship ship) {
        synchronized (this) {
            this.ship = ship;
            if (ship != null) {
                this.ship.parentThing = this;
            }
        }

    }

    /**
     * Child ship call this method when jobs are done
     */
    public void leaveDock() {
        setShip(null);
        checkDock();
    }

    /**
     * Asks and gets a new ship from its' port
     */
    private void askForNewShip() {
        Ship ship = ((SeaPort) parentThing).getShipFromQue();
        if (ship != null)
            setShip(ship);
    }

    /**
     * Mutator method to get ship object
     *
     * @return ship of the class
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns it's super method along with it's ship object's
     *
     * @return String
     */
    public String toString() {
        if (ship != null)
            return "\n  Dock: " + super.toString() + "\n    " + ship.toString();
        else
            return "\n  Dock: " + super.toString();


    }

    /**
     * Checks dock, if it is empty notify port
     */
    public void checkDock() {
        if (ship == null || ship.jobs.size() == 0)
            askForNewShip();
    }

    boolean askForPersonnel(ArrayList<String> requirements) {
        return ((SeaPort) parentThing).askForPersonnel(requirements);
    }

    public ArrayList<Person> requestWorkers(ArrayList<String> requirements, Job job) {
        return ((SeaPort) parentThing).requestWorkers(requirements, job);
    }

    public void releaseWorkers(ArrayList<Person> workers) {
        ((SeaPort) parentThing).releaseWorkers(workers);
    }

}
