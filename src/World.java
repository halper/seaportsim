import java.util.*;

/**
 * file name: World.java
 *
 * @author Ryan Cohan
 *         date: 12/12/16
 *         purpose: This class is container for simulation. It holds all the ports processed by process function in ports
 */
public class World extends Thing {
    HashMap<Integer, SeaPort> ports;
    PortTime time;
    static boolean init = false;

    /**
     * Default constructor of the class
     */
    public World() {
        ports = new HashMap<Integer, SeaPort>();
    }

    /**
     * Read files and process data
     *
     * @param sc
     */
    public void readFile(Scanner sc) {
        HashMap<Integer, Dock> dockHashMap = new HashMap<Integer, Dock>();
        HashMap<Integer, Ship> shipHashMap = new HashMap<Integer, Ship>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line.replaceAll("^\\s+", "");
            if (line.length() > 0 && line.charAt(0) != '/') {
                Scanner objectScanner = new Scanner(line);
                if (!objectScanner.hasNext())
                    return;
                String keyword = objectScanner.next().toLowerCase();
                if (keyword.matches("port")) addPort(objectScanner);
                else if (keyword.matches("dock")) addDock(objectScanner, dockHashMap);
                else if (keyword.matches("pship")) addPassengerShip(objectScanner, dockHashMap, shipHashMap);
                else if (keyword.matches("cship")) addCargoShip(objectScanner, dockHashMap, shipHashMap);
                else if (keyword.matches("person")) addPerson(objectScanner);
                else if (keyword.matches("job")) addJob(objectScanner, shipHashMap);

            }
        }
    }

    private void addJob(Scanner objectScanner, HashMap<Integer, Ship> shipHashMap) {
        Job job = new Job(objectScanner);
        if (shipHashMap.get(job.getParent()) != null) {
            job.setParentThing(shipHashMap);
            shipHashMap.get(job.getParent()).jobs.add(job);
        }
    }


    /**
     * Creates person object from scanner object
     * And adds the person to its parent sea port object
     * Person is created from scanner object
     *
     * @param objectScanner
     */
    private void addPerson(Scanner objectScanner) {
        Person person = new Person(objectScanner);
        // Add person to port
        ports.get(person.getParent()).addPerson(person);
    }

    /**
     * Creates a cargo ship object from scanner object
     * And assign the ship to sea port object which is parent of it's parent object dock
     *
     * @param objectScanner
     * @param dockHashMap
     * @param shipHashMap
     */
    private void addCargoShip(Scanner objectScanner, HashMap<Integer, Dock> dockHashMap, HashMap<Integer, Ship> shipHashMap) {
        CargoShip cargoShip = new CargoShip(objectScanner);
        shipHashMap.put(cargoShip.getIndex(), cargoShip);
        assignShip(cargoShip, dockHashMap);
    }

    /**
     * Creates a passenger ship object from scanner object
     * And assign the ship to sea port object which is parent of it's parent object dock
     *
     * @param objectScanner
     * @param shipHashMap
     */
    private void addPassengerShip(Scanner objectScanner, HashMap<Integer, Dock> dockHashMap, HashMap<Integer, Ship> shipHashMap) {
        PassengerShip passengerShip = new PassengerShip(objectScanner);
        shipHashMap.put(passengerShip.getIndex(), passengerShip);
        assignShip(passengerShip, dockHashMap);
    }

    /**
     * Creates new dock object from scanner object
     * And adds it to its parent sea port object
     *
     * @param objectScanner
     * @param dockHashMap
     */
    private void addDock(Scanner objectScanner, HashMap<Integer, Dock> dockHashMap) {
        Dock dock = new Dock(objectScanner);
        dock.setParentThing(ports);
        dockHashMap.put(dock.getIndex(), dock);
        // Add dock to port
        ports.get(dock.getParent()).addDock(dock);
    }

    /**
     * Creates new sea port object from scanner object
     * And adds it to ports
     *
     * @param sc
     */
    private void addPort(Scanner sc) {
        SeaPort seaPort = new SeaPort(sc);
        ports.put(seaPort.getIndex(), seaPort);
    }

    /**
     * Assigns a ship to it's parent port
     *
     * @param ship
     * @param dockHashMap
     */
    private void assignShip(Ship ship, HashMap<Integer, Dock> dockHashMap) {
        Dock md = dockHashMap.get(ship.getParent());
        if (md == null) {
            ship.setParentThing(ports);
            ports.get(ship.getParent()).addShip(ship);
            ports.get(ship.getParent()).addToQue(ship);
            return;
        }
        if (md.getShip() != null)
            ports.get(ship.getParent()).addToQue(ship);
        else
            md.setShip(ship);
        ports.get(md.getParent()).addShip(ship);
    }

    /**
     * For each seaport object of ports runs toString method of SeaPort class
     *
     * @return String
     */
    public String toString() {
        String st = "The world: ";
        for (SeaPort sp : ports.values()) st += sp.toString();
        return st;
    }

    public String sortByName() {
        List<SeaPort> comparisonList = new ArrayList<SeaPort>();
        String str = "";

        for (SeaPort seaPort : ports.values()) {
            seaPort.sortAllListsByName();
            comparisonList.add(seaPort);
        }
        Collections.sort(comparisonList);
        for (SeaPort seaPort : comparisonList) {
            str += "Port: ";
            str += seaPort.getName() + "\n  Ships:\n    ";
            for (Ship ship : seaPort.ships) {
                str += ship.getName() + "\n    ";
                str += "  Jobs:\n        ";
                for (Job job : ship.jobs) {
                    str += job.getName() + "\n        ";
                }
                str += "\n    ";
            }
            str += "\n  Docks:\n    ";
            for (Dock dock : seaPort.docks) {
                str += dock.getName() + "\n    ";
            }
            str += "\n  People:\n    ";
            for (Person person : seaPort.persons) {
                str += person.getName() + "\n    ";
            }
        }
        return str;
    }

    /**
     * Sorts all que ships in all sea ports by weight
     *
     * @return sorted names of ships
     */
    public String sortByWeight() {
        String str = "";
        for (SeaPort seaPort : ports.values()) {
            seaPort.sortByWeight();
            str += "Port: ";
            str += seaPort.getName() + "\n  Ships:\n    ";
            for (Ship ship : seaPort.que) {
                str += ship.getName() + ": " + ship.weight + "\n    ";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Sorts all que ships in all sea ports by length
     *
     * @return sorted names of ships
     */
    public String sortByLength() {
        String str = "";
        for (SeaPort seaPort : ports.values()) {
            seaPort.sortByLength();
            str += "Port: ";
            str += seaPort.getName() + "\n  Ships:\n    ";
            for (Ship ship : seaPort.que) {
                str += ship.getName() + ": " + ship.length + "\n    ";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Sorts all que ships in all sea ports by width
     *
     * @return sorted names of ships
     */
    public String sortByWidth() {
        String str = "";
        for (SeaPort seaPort : ports.values()) {
            seaPort.sortByWidth();
            str += "Port: ";
            str += seaPort.getName() + "\n  Ships:\n    ";
            for (Ship ship : seaPort.que) {
                str += ship.getName() + ": " + ship.width + "\n    ";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Sorts all que ships in all sea ports by draft
     *
     * @return sorted names of ships
     */
    public String sortByDraft() {
        String str = "";
        for (SeaPort seaPort : ports.values()) {
            seaPort.sortByDraft();
            str += "Port: ";
            str += seaPort.getName() + "\n  Ships:\n    ";
            for (Ship ship : seaPort.que) {
                str += ship.getName() + ": " + ship.draft + "\n    ";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Gets dock object from its index
     *
     * @param index
     * @return Dock
     */
    private Dock getDockByIndex(int index) {
        for (SeaPort msp : ports.values())
            for (Dock md : msp.docks)
                if (md.getIndex() == index)
                    return md;
        return null;
    }

    /**
     * Gets a ship object from it's index
     *
     * @param index
     * @return Ship
     */
    private Ship getShipByIndex(int index) {
        for (SeaPort msp : ports.values())
            for (Ship ms : msp.ships)
                if (ms.getIndex() == index)
                    return ms;
        return null;
    }

    /**
     * Gets a person object from it's index
     *
     * @param index
     * @return Person
     */
    private Person getPersonByIndex(int index) {
        for (SeaPort port : ports.values()) {
            for (Person person : port.persons) {
                if (person.getIndex() == index)
                    return person;
            }
        }
        return null;
    }

    /**
     * Searches for Things with given index in the World
     *
     * @param search: Search string
     * @return result
     */
    public String searchIndex(String search) {
        int index;
        try {
            index = Integer.parseInt(search);
        } catch (NumberFormatException e) {
            return "That is not a number!";
        }
        String result = "";

        result += (getDockByIndex(index) != null) ? getDockByIndex(index).toString() : "";
        result += (getShipByIndex(index) != null) ? getShipByIndex(index).toString() : "";
        result += (getPersonByIndex(index) != null) ? getPersonByIndex(index).toString() : "";
        result += (ports.get(index) != null) ? ports.get(index).toString() : "";

        return result;
    }

    /**
     * Searches for Things with given name in the World
     *
     * @param search: Search string
     * @return result
     */
    public String searchName(String search) {
        String result = "";
        for (SeaPort port : ports.values()) {
            for (Dock dock : port.docks) {
                if (dock.getName().toLowerCase().matches(search.toLowerCase()))
                    result += dock.toString();
            }
            for (Ship ship : port.ships) {
                if (ship.getName().toLowerCase().matches(search.toLowerCase()))
                    result += ship.toString();
            }
            for (Person person : port.persons) {
                if (person.getName().toLowerCase().matches(search.toLowerCase()))
                    result += person.toString();
            }
            if (port.getName().toLowerCase().matches(search.toLowerCase()))
                result += port.toString();
        }
        return result;
    }

    /**
     * Searches for Persons with given skill in the World
     *
     * @param search: Search string
     * @return result
     */
    public String searchSkill(String search) {
        String result = "";
        for (SeaPort port : ports.values()) {
            for (Person person : port.persons) {
                if (person.getSkill().toLowerCase().matches(search.toLowerCase()))
                    result += person.toString();
            }
        }
        return result;
    }

    /**
     * This is set when the world and GUI is ready
     */
    public static void setInit(){
        init = true;
    }

    /**
     * Check whether the world is initialized
     * @return
     */
    public static boolean getInit(){
        return init;
    }
}
