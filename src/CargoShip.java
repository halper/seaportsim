import java.util.Scanner;

/**
 * file name: CargoShip.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class simulates a cargo ship
 */
public class CargoShip extends Ship {
    private double cargoValue;
    private double cargoVolume;
    private double cargoWeight;

    /**
     * Constructor for scanner object
     *
     * @param sc
     */
    public CargoShip(Scanner sc) {
        super(sc);
        if (sc.hasNextDouble()) cargoValue = sc.nextDouble();
        if (sc.hasNextDouble()) cargoVolume = sc.nextDouble();
        if (sc.hasNextDouble()) cargoWeight = sc.nextDouble();
    }
}
