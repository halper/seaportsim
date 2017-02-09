import java.util.Scanner;

/**
 * file name: PassengerShip.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class simulates a passenger ship
 */
public class PassengerShip extends Ship {
    private int numberOfOccupiedRooms;
    private int numberOfPassengers;
    private int numberOfRooms;

    /**
     * Constructor for scanner object
     * @param sc
     */
    public PassengerShip(Scanner sc) {
        super(sc);
        if (sc.hasNextInt()) numberOfPassengers = sc.nextInt();
        if (sc.hasNextInt()) numberOfRooms = sc.nextInt();
        if (sc.hasNextInt()) numberOfOccupiedRooms = sc.nextInt();
    }
}
