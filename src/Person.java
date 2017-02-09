import java.util.Scanner;

/**
 * file name: Person.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class is to run the Sea Port simulation
 */
public class Person extends Thing {
    private String skill;
    boolean isAvailable = true;

    /**
     * Constructor for scanner object
     *
     * @param sc
     */
    public Person(Scanner sc) {
        super(sc);
        if (sc.hasNext()) skill = sc.next();
    }

    public String getSkill() {
        return skill;
    }

    /**
     * Concatenate it's field skill with the output from its super method
     *
     * @return String
     */
    public String toString() {
        return " Person: " + super.toString() + " " + skill;
    }

    synchronized public Person hire() {
        if (isAvailable) {
            isAvailable = false;
            return this;
        }
        return null;
    }

    /**
     * Makes person available for upcoming job requests
     */
    synchronized public void release() {
        if (!isAvailable)
            isAvailable = true;
    }

    /**
     * Checks if the person has required skill
     * @param requirement
     * @return boolean
     */
    public boolean hasSkill(String requirement) {
        return requirement.toLowerCase().matches(skill.toLowerCase());
    }
}
